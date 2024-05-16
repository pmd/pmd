/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.ast

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils
import net.sourceforge.pmd.lang.java.types.STRING
import net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind.*
import net.sourceforge.pmd.lang.java.types.parseWithTypeInferenceSpy
import net.sourceforge.pmd.lang.java.types.shouldHaveType
import net.sourceforge.pmd.lang.test.ast.component6
import net.sourceforge.pmd.lang.test.ast.shouldBe

class ConversionContextTests : ProcessorTestSpec({

    parserTest("Test simple contexts") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Foo {
                double foo() {
                    String.valueOf((Double) 1d);
                    return 2;
                }
            }
        """)

        val (valueOf, _, doubleCast, doubleLit, intLit) = acu.descendants(ASTExpression::class.java).toList()

        spy.shouldBeOk {
            valueOf.conversionContext::isMissing shouldBe true
            doubleCast.conversionContext::getTargetType shouldBe ts.OBJECT
            doubleLit.conversionContext::getTargetType shouldBe double.box()
            intLit.conversionContext::getTargetType shouldBe double
        }
    }

    parserTest("Test standalone ternary context") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Foo {
                double foo() {
                    double r = true ? 1 : (short) 5;
                    return 2;
                }
            }
        """)

        val (ternary, _, num1, shortCast, _) = acu.descendants(ASTExpression::class.java).toList()

        spy.shouldBeOk {
            // ternary is in double assignment context
            ternary.conversionContext::isMissing shouldBe false
            ternary.conversionContext::getTargetType shouldBe double

            // but it has type int
            ternary shouldHaveType int

            // more importantly, both branch expressions have context int and not double

            num1 shouldHaveType int
            shortCast shouldHaveType short

            num1.conversionContext::getTargetType shouldBe int
            shortCast.conversionContext::getTargetType shouldBe int
        }
    }

    parserTest("Test standalone ternary context (2, boxing)") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Foo {
                double foo(Integer i, Long l, boolean c) {
                    var z = c ? (Integer) null
                              : 4;
                }
            }
        """)

        val (ternary, _, integerCast, _, num4) = acu.descendants(ASTExpression::class.java).toList()

        spy.shouldBeOk {
            // ternary is in double assignment context
            ternary.conversionContext::isMissing shouldBe true
            ternary.conversionContext::getTargetType shouldBe null

            // but it has type int
            ternary shouldHaveType int

            // more importantly, both branch expressions have context int and not double

            integerCast shouldHaveType int.box()
            num4 shouldHaveType int

            integerCast.conversionContext::getTargetType shouldBe int
            num4.conversionContext::getTargetType shouldBe int
        }
    }
    parserTest("Test context of assert stmt") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Foo {
                static void m(Boolean boxedBool, boolean bool, String str) {
                    assert boxedBool;
                    assert bool : str;
                }
            }
        """)

        val (boxedBool, bool, str) = acu.descendants(ASTVariableAccess::class.java).toList()

        spy.shouldBeOk {
            boxedBool.conversionContext::getTargetType shouldBe ts.BOOLEAN
            bool.conversionContext::getTargetType shouldBe ts.BOOLEAN
            str.conversionContext::getTargetType shouldBe ts.STRING
        }
    }

    parserTest("Test context of statements with conditions") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Foo {
                static void m(Boolean boxedBool, boolean bool, String str, int[] ints) {
                    if (boxedBool);
                    while (boxedBool);
                    for (int i = 0; boxedBool; i++) {}
                    do; while (boxedBool);
                    for (int i : ints);
                }
            }
        """)

        val (ifstmt, whilestmt, forstmt, _, dostmt, foreachstmt) = acu.descendants(ASTVariableAccess::class.java).toList()
        val forUpdate = acu.descendants(ASTForUpdate::class.java).firstOrThrow().exprList[0]

        spy.shouldBeOk {

            ifstmt.conversionContext::getTargetType shouldBe ts.BOOLEAN
            whilestmt.conversionContext::getTargetType shouldBe ts.BOOLEAN
            forstmt.conversionContext::getTargetType shouldBe ts.BOOLEAN
            dostmt.conversionContext::getTargetType shouldBe ts.BOOLEAN

            forUpdate.conversionContext::getTargetType shouldBe null
            foreachstmt.conversionContext::getTargetType shouldBe null
        }
    }

    parserTest("Test missing context in qualifier") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Scratch {
                static void m(Boolean boxedBool) {
                    ((Boolean) boxedBool).booleanValue(); 
                    ((Object) boxedBool).somefield;
                }
            }
        """)

        val (booleanCast, objectCast) = acu.descendants(ASTCastExpression::class.java).toList()

        spy.shouldBeOk {
            booleanCast.conversionContext::getTargetType shouldBe null
            objectCast.conversionContext::getTargetType shouldBe null
        }
    }

    parserTest("Test context of ternary condition") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Scratch {
                static void m(Boolean boxedBool, boolean bool, String str, int[] ints) {
                    str = (boolean) boxedBool ? "a" : "b";
                }
            }
        """)

        val (booleanCast) = acu.descendants(ASTCastExpression::class.java).toList()

        spy.shouldBeOk {
            booleanCast.conversionContext::getTargetType shouldBe boolean
        }
    }

    parserTest("Test numeric context") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
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
        """)

        val (mulint, lshift, and, cmp, plusdouble, muldouble) = acu.descendants(ASTInfixExpression::class.java).toList()

        spy.shouldBeOk {
            listOf(mulint, lshift, and).forEach {
                withClue(it) {
                    it.leftOperand.conversionContext::getTargetType shouldBe ts.INT
                    it.rightOperand.conversionContext::getTargetType shouldBe ts.INT
                }
            }
            withClue(cmp) {
                cmp.conversionContext::getTargetType shouldBe boolean
                cmp.conversionContext::getKind shouldBe INVOCATION

                listOf(cmp.leftOperand, cmp.rightOperand).forEach {
                    withClue(it) {
                        it.conversionContext::getTargetType shouldBe int
                        it.conversionContext::getKind shouldBe NUMERIC
                    }
                }
            }
            listOf(plusdouble, muldouble).forEach {
                withClue(it) {
                    it.leftOperand.conversionContext::getTargetType shouldBe ts.DOUBLE
                    it.rightOperand.conversionContext::getTargetType shouldBe ts.DOUBLE
                }
            }
        }
    }
    parserTest("String contexts") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Scratch {
                static void m(int i) {
                    eat(" " + i);
                    eat(i + " ");
                    eat(" " + " ");
                    eat(" " + i + i);
                }
                void eat(Object d) {}
            }
        """)

        val concats = acu.descendants(ASTInfixExpression::class.java).toList()

        spy.shouldBeOk {
            concats.forEach {
                withClue(it) {
                    JavaAstUtils.isStringConcatExpr(it) shouldBe true
                    it.leftOperand.conversionContext::getTargetType shouldBe ts.STRING
                    it.rightOperand.conversionContext::getTargetType shouldBe ts.STRING
                }
            }
        }
    }
    parserTest("Relational ops") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Scratch {
                static void m(int i) {
                    eat(i < i++);       //l0
                    eat(i > (long) i);  //l1
                }
                void eat(Object d) {}
            }
        """)

        val (l0, l1) = acu.descendants(ASTInfixExpression::class.java).toList()

        spy.shouldBeOk {
            l0.leftOperand.conversionContext::getTargetType shouldBe int
            l0.rightOperand.conversionContext::getTargetType shouldBe int

            l1.leftOperand.conversionContext::getTargetType shouldBe long
            l1.rightOperand.conversionContext::getTargetType shouldBe long
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
            lambda.expressionBody!!.conversionContext shouldBe ExprContext.getMissingInstance()

            lambdaToLong.expressionBody!!.conversionContext::getTargetType shouldBe long
            lambdaToLong.expressionBody!!.conversionContext::getKind shouldBe ASSIGNMENT

            lambda.conversionContext::getKind shouldBe INVOCATION
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
            nested.conversionContext.targetType shouldBe byte // not int
            nonNested.conversionContext.targetType shouldBe byte
            nested.conversionContext.kind shouldBe INVOCATION
            nonNested.conversionContext.kind shouldBe INVOCATION
        }
    }
})
