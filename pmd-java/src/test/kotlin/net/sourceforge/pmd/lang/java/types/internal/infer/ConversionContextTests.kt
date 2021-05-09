/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.component6
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil
import net.sourceforge.pmd.lang.java.types.STRING
import net.sourceforge.pmd.lang.java.types.parseWithTypeInferenceSpy
import net.sourceforge.pmd.lang.java.types.shouldHaveType

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

        val (ternary, _, num1, shortCast, num5) = acu.descendants(ASTExpression::class.java).toList()

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

        val (ternary, _, integerCast, nullLit, num4) = acu.descendants(ASTExpression::class.java).toList()

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

                    eat(i + e);
                    eat(i * e);
                }
                void eat(double d) {}
            }
        """)

        val (mulint, lshift, and, plusdouble, muldouble) = acu.descendants(ASTInfixExpression::class.java).toList()

        spy.shouldBeOk {
            listOf(mulint, lshift, and).forEach {
                withClue(it) {
                    it.leftOperand.conversionContext::getTargetType shouldBe ts.INT
                    it.rightOperand.conversionContext::getTargetType shouldBe ts.INT
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
                    JavaRuleUtil.isStringConcatExpr(it) shouldBe true
                    it.leftOperand.conversionContext::getTargetType shouldBe ts.STRING
                    it.rightOperand.conversionContext::getTargetType shouldBe ts.STRING
                }
            }
        }
    }
})
