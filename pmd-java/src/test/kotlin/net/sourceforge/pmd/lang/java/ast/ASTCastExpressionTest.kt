/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType.INT
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx

class ASTCastExpressionTest : ParserTestSpec({

    parserTest("Simple cast") {

        "(Foo) obj" should matchExpr<ASTCastExpression> {

            it::getCastType shouldBe child<ASTClassOrInterfaceType> {

            }

            unspecifiedChild()
        }

        "(@F Foo) obj" should matchExpr<ASTCastExpression> {

            annotation("F")

            it::getCastType shouldBe child<ASTClassOrInterfaceType> {

            }

            unspecifiedChild()
        }
    }
    parserTest("Nested casts") {

        inContext(ExpressionParsingCtx) {
            "(Foo) (int) obj" should parseAs {
                castExpr {
                    classType("Foo")
                    castExpr {
                        primitiveType(INT)
                        variableRef("obj")
                    }
                }
            }
        }
    }

    parserTest("Test intersection in cast", javaVersions = JavaVersion.J1_8..Latest) {

        "(@F Foo & Bar) obj" should matchExpr<ASTCastExpression> {

            annotation("F")

            it::getCastType shouldBe child<ASTIntersectionType> {

                child<ASTClassOrInterfaceType> {
                    it::getTypeImage shouldBe "Foo"
                }

                child<ASTClassOrInterfaceType> {
                    it::getTypeImage shouldBe "Bar"
                }

            }

            unspecifiedChild()
        }

        "(@F Foo & @B Bar) obj" should notParseIn(ExpressionParsingCtx)
    }

    parserTest("Test intersection ambiguity", javaVersions = Earliest..Latest) {
        inContext(ExpressionParsingCtx) {


            "(modifiers & InputEvent.Foo) != 0" should parseAs {
                equalityExpr(BinaryOp.NE) {
                    parenthesized {
                        andExpr {
                            variableRef("modifiers")
                            fieldAccess("Foo") {
                                unspecifiedChild()
                            }
                        }
                    }

                    number()
                }
            }


            "(modifiers) != 0" should parseAs {
                equalityExpr(BinaryOp.NE) {
                    parenthesized {
                        variableRef("modifiers")
                    }

                    number()
                }
            }


            "(modifiers) * 0" should parseAs {
                multiplicativeExpr(BinaryOp.MUL) {
                    parenthesized {
                        variableRef("modifiers")
                    }

                    number()
                }
            }

        }
    }


})
