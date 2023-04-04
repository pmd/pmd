/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest

class Java11Test : ParserTestSpec({

    parserTestGroup("Test lambda parameter with var keyword") {

        // var keyword should be a normal type pre-java 11
        onVersions(J1_8..J10) {

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

        // var keyword should generate no type after java 11
        onVersions(J11..Latest) {

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
    }

})
