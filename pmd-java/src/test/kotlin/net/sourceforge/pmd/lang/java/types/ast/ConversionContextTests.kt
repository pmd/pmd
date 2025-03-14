/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.ast

import io.kotest.assertions.withClue
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind
import net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind.*
import net.sourceforge.pmd.lang.test.ast.*
import net.sourceforge.pmd.lang.test.ast.shouldBe

class ConversionContextTests : ProcessorTestSpec({

    fun haveContext(kind: ExprContextKind, target: JTypeMirror?): Matcher<ASTExpression> = Matcher {
        val ctx = it.conversionContext
        MatcherResult(
            passed = ctx.targetType == target && ctx.kind == kind,
            failureMessageFn = { "Expected $kind (target $target), but got ${ctx.kind} (target ${ctx.targetType})" },
            negatedFailureMessageFn = { "Expected not $kind (target $target), but got ${ctx.kind} (target ${ctx.targetType})" },
        )
    }

    fun TypeDslMixin.haveBooleanContext(): Matcher<ASTExpression> = haveContext(BOOLEAN, boolean)
    fun haveNoContext(): Matcher<ASTExpression> = haveContext(MISSING, null)

    parserTest("Test simple contexts") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
        class Foo {
            double foo() {
                String.valueOf((Double) 1d);
                return 2;
            }
        }
    """
        )

        val (valueOf, _, doubleCast, doubleLit, intLit) = acu.descendants(ASTExpression::class.java).toList()

        spy.shouldBeOk {
            valueOf should haveNoContext()
            doubleCast should haveContext(INVOCATION, ts.OBJECT)
            doubleLit should haveContext(CAST, double.box())
            intLit should haveContext(ASSIGNMENT, double)
        }
    }

    parserTest("Test standalone ternary context") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
        class Foo {
            double foo() {
                double r = true ? 1 : (short) 5;
                return 2;
            }
        }
    """
        )

        val (ternary, _, num1, shortCast, num5) = acu.descendants(ASTExpression::class.java).toList()

        spy.shouldBeOk {
            // ternary is in double assignment context
            ternary should haveContext(ASSIGNMENT, double)

            // but it has type int
            ternary shouldHaveType int

            // more importantly, both branch expressions have context int and not double

            num1 shouldHaveType int
            shortCast shouldHaveType short

            num1 should haveContext(TERNARY, int)
            shortCast should haveContext(TERNARY, int)
            num5 should haveContext(CAST, short)
        }
    }

    parserTest("Test standalone ternary context (2, boxing)") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
        class Foo {
            double foo(Integer i, Long l, boolean c) {
                var z = c ? (Integer) null
                          : 4;
            }
        }
    """
        )

        val (ternary, _, integerCast, _, num4) = acu.descendants(ASTExpression::class.java).toList()

        spy.shouldBeOk {
            // ternary is in double assignment context
            ternary should haveNoContext()

            // but it has type int
            ternary shouldHaveType int

            // more importantly, both branch expressions have context int and not double

            integerCast shouldHaveType int.box()
            num4 shouldHaveType int

            integerCast should haveContext(TERNARY, int)
            num4 should haveContext(TERNARY, int)
        }
    }

    parserTest("Test context of assert stmt") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
        class Foo {
            static void m(Boolean boxedBool, boolean bool, String str) {
                assert boxedBool;
                assert bool : str;
            }
        }
    """
        )

        val (boxedBool, bool, str) = acu.descendants(ASTVariableAccess::class.java).toList()

        spy.shouldBeOk {
            boxedBool should haveBooleanContext()
            bool should haveBooleanContext()
            str should haveContext(STRING, ts.STRING)
        }
    }

    parserTest("Test context of statements with conditions") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
        class Foo {
            static void m(Boolean boxedBool, boolean bool, String str, int[] ints) {
                if (boxedBool);
                while (boxedBool);
                for (int i = 0; boxedBool; i++) {}
                do; while (boxedBool);
                for (int i : ints);
            }
        }
    """
        )

        val (ifstmt, whilestmt, forstmt, _, dostmt, foreachstmt) = acu.descendants(ASTVariableAccess::class.java)
            .toList()
        val forUpdate = acu.descendants(ASTForUpdate::class.java).firstOrThrow().exprList[0]

        spy.shouldBeOk {

            ifstmt should haveBooleanContext()
            whilestmt should haveBooleanContext()
            forstmt should haveBooleanContext()
            dostmt should haveBooleanContext()

            forUpdate should haveNoContext()
            foreachstmt should haveNoContext()
        }
    }

    parserTest("Test missing context in qualifier") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
        class Scratch {
            static void m(Boolean boxedBool) {
                ((Boolean) boxedBool).booleanValue(); 
                ((Object) boxedBool).somefield;
            }
        }
    """
        )

        val (booleanCast, objectCast) = acu.descendants(ASTCastExpression::class.java).toList()

        spy.shouldBeOk {
            booleanCast should haveNoContext()
            objectCast should haveNoContext()
        }
    }

    parserTest("Test context of ternary condition") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
        class Scratch {
            static void m(Boolean boxedBool, boolean bool, String str, int[] ints) {
                str = (boolean) boxedBool ? "a" : "b";
            }
        }
    """
        )

        val (booleanCast) = acu.descendants(ASTCastExpression::class.java).toList()

        spy.shouldBeOk {
            booleanCast should haveBooleanContext()
        }
    }

    parserTest("Test numeric context") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
        class Scratch {
            static void m() {
                int i, j, k;
                double d, e;
                
                eat(i * j);
                eat(i << j);
                eat(i & j);

                eatbool(i == (Integer) j);

                eat(i + e);
                eat(i * e);
            }
            void eat(double d) {}
            void eatbool(boolean d) {}
        }
    """
        )

        val (mulint, lshift, and, cmp, plusdouble, muldouble) = acu.descendants(ASTInfixExpression::class.java).toList()

        spy.shouldBeOk {
            listOf(mulint, lshift, and).forEach {
                withClue(it) {
                    it.leftOperand should haveContext(NUMERIC, int)
                    it.rightOperand should haveContext(NUMERIC, int)
                }
            }
            withClue(cmp) {
                cmp should haveContext(INVOCATION, boolean)

                listOf(cmp.leftOperand, cmp.rightOperand).forEach {
                    withClue(it) {
                        it should haveContext(NUMERIC, int)
                    }
                }
            }
            listOf(plusdouble, muldouble).forEach {
                withClue(it) {
                    it.leftOperand should haveContext(NUMERIC, double)
                    it.rightOperand should haveContext(NUMERIC, double)
                }
            }
        }
    }

    parserTest("String contexts") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
        class Scratch {
            static void m(int i) {
                eat(" " + i);
                eat(i + " ");
                eat(" " + " ");
                eat(" " + i + i);
            }
            void eat(Object d) {}
        }
    """
        )

        val concats = acu.descendants(ASTInfixExpression::class.java).toList()

        spy.shouldBeOk {
            concats.forEach {
                withClue(it) {
                    JavaAstUtils.isStringConcatExpr(it) shouldBe true
                    it.leftOperand should haveContext(STRING, ts.STRING)
                    it.rightOperand should haveContext(STRING, ts.STRING)
                }
            }
        }
    }

    parserTest("Relational ops") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
        class Scratch {
            static void m(int i) {
                eat(i < i++);       //l0
                eat(i > (long) i);  //l1
            }
            void eat(Object d) {}
        }
    """
        )

        val (l0, l1) = acu.descendants(ASTInfixExpression::class.java).toList()

        spy.shouldBeOk {
            l0.leftOperand should haveContext(NUMERIC, int)
            l0.rightOperand should haveContext(NUMERIC, int)

            l1.leftOperand should haveContext(NUMERIC, long)
            l1.rightOperand should haveContext(NUMERIC, long)
        }
    }

    parserTest("Boolean contexts") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Scratch {
                static void m(boolean a, Boolean b) {
                    eat(a == b);       
                    eat(a != b);  
                    eat(a && b);  
                    eat(a || b);  
                    eat(a ^ b);  
                    eat(a & b);  
                    eat(a | b);  
                }
                void eat(Object d) {}
            }
        """
        )

        val exprs = acu.descendants(ASTVariableAccess::class.java).toList()

        spy.shouldBeOk {
            for (e in exprs) {
                withClue(e.parent) {
                    e should haveBooleanContext()
                }
            }
        }
    }

    parserTest("Switch scrutinee") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Scratch {
                static void m(boolean a, Boolean b) {
                  switch (4) { }
                  switch (Integer.valueOf(4)) { }
                  
                }
                void eat(Object d) {}
            }
        """
        )

        val exprs = acu.descendants(ASTSwitchLike::class.java).toList { it.testedExpression }

        spy.shouldBeOk {
            for (e in exprs) {
                withClue(e.parent) {
                    e should haveContext(NUMERIC, int)
                }
            }
        }
    }

    parserTest("Lambda ctx") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Foo {
                record Item(int cents) {}
                Object map(Item item) {
                    // here the conversion is necessary to determine the context type
                    return map(item, it -> Long.valueOf(it.cents()));
                }
                Object map2(Item item) {
                    // here it is not necessary
                    return mapToLong(item, it -> Long.valueOf(it.cents()));
                }
                interface Fun<T,R> { R apply(T t); }
                interface ToLongFun<T> { long apply(T t); }
                <T,R> R map(T t, Fun<T,R> fun) {}
                <T> long mapToLong(T t, ToLongFun<T> fun) {}
            }
        """)

        val (lambda, lambdaToLong) = acu.descendants(ASTLambdaExpression::class.java).toList()

        spy.shouldBeOk {
            lambda.expressionBody!! should haveNoContext()
            lambda.conversionContext::getKind shouldBe INVOCATION

            lambdaToLong.expressionBody!! should haveContext(ASSIGNMENT, long)
        }
    }

    parserTest("Ctx of nested invocation") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Foo {
                int eatByte(byte b) {}
                int eatInt(int b) {}

                void eq(Object val) {
                    Bar b = (Bar) val;
                    eatByte(b.x);
                    eatInt(eatByte(b.x));
                }
                class Bar { byte x; }
            }
        """)

        val (nonNested, nested) = acu.descendants(ASTFieldAccess::class.java).toList()

        spy.shouldBeOk {
            nested should haveContext(INVOCATION, byte) // not int
            nonNested should haveContext(INVOCATION, byte)
        }
    }

    parserTest("Context of unary exprs") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Foo {
                void eq(boolean b, short s, int i, double d) {
                    eat(!a);
                    eat(~s);
                    eat(+s); eat(+i); eat(+d);
                    eat(-s); eat(-i); eat(-d);
                    i++;
                    --d;
                }
                void eat(Object o) {}
            }
        """
        )

        val (not, complement, plusShort, plusInt, plusDouble,
            minusShort, minusInt, minusDouble, iplusplus, minusminusd) = acu.descendants(ASTUnaryExpression::class.java)
            .toList { it.operand }

        spy.shouldBeOk {
            not should haveBooleanContext()
            complement should haveContext(NUMERIC, int)
            plusShort should haveContext(NUMERIC, int)
            plusInt should haveContext(NUMERIC, int)
            plusDouble should haveContext(NUMERIC, double)
            minusShort should haveContext(NUMERIC, int)
            minusInt should haveContext(NUMERIC, int)
            minusDouble should haveContext(NUMERIC, double)
            iplusplus should haveNoContext()
            minusminusd should haveNoContext()
        }
    }
})
