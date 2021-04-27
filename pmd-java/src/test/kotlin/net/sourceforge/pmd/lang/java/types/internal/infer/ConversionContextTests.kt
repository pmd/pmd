/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTExpression
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess
import net.sourceforge.pmd.lang.java.ast.ProcessorTestSpec
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
                    assert bool;
                    assert bool : str;
                }
            }
        """)

        val (boxedBool, bool, bool2, str) = acu.descendants(ASTVariableAccess::class.java).toList()

        spy.shouldBeOk {
            boxedBool.conversionContext::getTargetType shouldBe ts.BOOLEAN
            bool.conversionContext::getTargetType shouldBe ts.BOOLEAN
            bool2.conversionContext::getTargetType shouldBe ts.BOOLEAN
            str.conversionContext::getTargetType shouldBe ts.STRING
        }
    }
})
