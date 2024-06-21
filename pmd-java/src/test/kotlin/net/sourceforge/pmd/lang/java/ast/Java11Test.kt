/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.test.ast.shouldBe

class Java11Test : ParserTestSpec({
    parserTestContainer("Test lambda parameter with var keyword (pre-java 11)", javaVersions = J1_8..J10) {
        // var keyword should be a normal type pre-java 11
        inContext(ExpressionParsingCtx) {
            "(var x) -> String.valueOf(x)" should parseAs {
                exprLambda {
                    it::getParameters shouldBe lambdaFormals {
                        lambdaParam {
                            modifiers { }
                            classType("var")
                            variableId("x")
                        }
                    }

                    methodCall("valueOf")
                }
            }

            "(var x, var y) -> x + y" should parseAs {
                exprLambda {
                    it::getParameters shouldBe lambdaFormals {
                        lambdaParam {
                            modifiers { }
                            classType("var")
                            variableId("x")
                        }

                        lambdaParam {
                            modifiers { }
                            classType("var")
                            variableId("y")
                        }
                    }

                    infixExpr(BinaryOp.ADD)
                }
            }

            "(@Nonnull var x) -> String.valueOf(x)" should parseAs {
                exprLambda {
                    it::getParameters shouldBe lambdaFormals {
                        lambdaParam {
                            modifiers {
                                annotation("Nonnull")
                            }
                            classType("var")
                            variableId("x")
                        }
                    }
                    methodCall("valueOf")
                }
            }
        }
    }

    parserTestContainer("Test lambda parameter with var keyword (java 11+)", javaVersions = J11..Latest) {
        // var keyword should generate no type after java 11
        inContext(ExpressionParsingCtx) {
            "(var x) -> String.valueOf(x)" should parseAs {
                exprLambda {
                    it::getParameters shouldBe lambdaFormals {
                        lambdaParam {
                            modifiers { }
                            it::isTypeInferred shouldBe true
                            variableId("x")
                        }
                    }

                    methodCall("valueOf")
                }
            }
        }
    }
})
