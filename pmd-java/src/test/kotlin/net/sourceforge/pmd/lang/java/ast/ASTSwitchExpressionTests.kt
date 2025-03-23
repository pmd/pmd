/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.test.ast.shouldBe
import net.sourceforge.pmd.lang.test.ast.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.BinaryOp.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.UnaryOp.UNARY_MINUS


/**
 * @author Clément Fournier
 */
class ASTSwitchExpressionTests : ParserTestSpec({
    val switchVersions = JavaVersion.since(J14)
    val notSwitchVersions = JavaVersion.except(switchVersions)

    parserTestContainer("No switch expr before j13 preview", javaVersions = notSwitchVersions) {
        inContext(ExpressionParsingCtx) {
            """
            switch (day) {
                case FRIDAY, SUNDAY -> 6;
                case WEDNESDAY      -> 9;
                case SONNABEND      -> throw new MindBlownException();
                default             -> {
                    int k = day * 2;
                    int result = f(k);
                    break result * 4;
                }
            }
        """ shouldNot parse()
        }
    }


    parserTestContainer("Simple switch expressions", javaVersions = switchVersions) {
        inContext(ExpressionParsingCtx) {
            """
            switch (day) {
                case FRIDAY, SUNDAY -> 6;
                case WEDNESDAY      -> 9;
                case SONNABEND      -> throw new MindBlownException();
                default             -> {
                    int k = day * 2;
                    int result = f(k);
                    yield result;
                }
            }
        """ should parseAs {
                switchExpr {
                    it::getTestedExpression shouldBe variableAccess("day")

                    switchArrow {
                        it::getLabel shouldBe switchLabel {
                            variableAccess("FRIDAY")
                            variableAccess("SUNDAY")
                        }
                        int(6)
                    }


                    switchArrow {
                        it::getLabel shouldBe switchLabel {
                            variableAccess("WEDNESDAY")
                        }
                        int(9)
                    }


                    switchArrow {
                        it::getLabel shouldBe switchLabel {
                            variableAccess("SONNABEND")
                        }
                        throwStatement()
                    }

                    switchArrow {
                        it::getLabel shouldBe switchDefaultLabel()
                        block {
                            localVarDecl()
                            localVarDecl()
                            yieldStatement {
                                variableAccess("result")
                            }
                        }
                    }
                }
            }
        }
    }

    parserTestContainer("Non-trivial labels", javaVersions = switchVersions) {
        inContext(ExpressionParsingCtx) {
            """ 
            switch (day) {
                case a + b, 4 * 2 / Math.PI -> 6;
            }
        """ should parseAs {
                switchExpr {
                    it::getTestedExpression shouldBe variableAccess("day")

                    switchArrow {
                        it::getLabel shouldBe switchLabel {
                            infixExpr(ADD) {
                                variableAccess("a")
                                variableAccess("b")
                            }
                            infixExpr(DIV) {
                                infixExpr(MUL) {
                                    int(4)
                                    int(2)
                                }
                                fieldAccess("PI")
                            }
                        }
                        int(6)
                    }
                }
            }
        }
    }

    parserTestContainer("Switch expr precedence", javaVersions = switchVersions) {
        inContext(ExpressionParsingCtx) {
            "2 * switch (day) {default -> 6;}" should parseAs {
                infixExpr(MUL) {
                    number()
                    switchExpr()
                }
            }

            "switch (day) {default -> 6;} * 4" should parseAs {
                infixExpr(MUL) {
                    switchExpr()
                    number()
                }
            }

            "switch (day) {default -> 6;} + 6" should parseAs {
                infixExpr(ADD) {
                    switchExpr()
                    number()
                }
            }
            "-switch (day) {default -> 6;}" should parseAs {
                unaryExpr(UNARY_MINUS) {
                    switchExpr()
                }
            }
        }
    }

    parserTestContainer("Nested switch expressions", javaVersions = switchVersions) {
        inContext(ExpressionParsingCtx) {
            """
            switch (day) {
                case FRIDAY -> 6;
                case WEDNESDAY      -> switch (foo) {
                  case 2 -> 5;
                  default -> 3;
                };
                default             -> 3;
            }
        """.trimIndent() should parseAs {
                switchExpr {
                    it::getTestedExpression shouldBe variableAccess("day")

                    it.branches.toList() shouldBe listOf(
                            switchArrow {
                                switchLabel {
                                    variableAccess("FRIDAY")
                                }
                                int(6)
                            },

                            switchArrow {
                                switchLabel {
                                    variableAccess("WEDNESDAY")
                                }

                                switchExpr {
                                    it::getTestedExpression shouldBe variableAccess("foo")

                                    switchArrow {
                                        switchLabel {
                                            int(2)
                                        }
                                        int(5)
                                    }
                                    switchArrow {
                                        switchDefaultLabel()
                                        int(3)
                                    }
                                }
                            },

                            switchArrow {
                                switchDefaultLabel()
                                int(3)
                            }
                    )
                }
            }
        }
    }

    parserTestContainer("Test yield expressions", javaVersions = JavaVersion.since(J14)) {
        inContext(ExpressionParsingCtx) {
            """
            switch (day) {
                case FRIDAY -> 6;
                case WEDNESDAY      -> switch (foo) {
                  case 2 -> 5;
                  default -> {
                    yield 7;
                  }
                };
                default             ->  {
                    yield 4;
                }
            }
        """.trimIndent() should parseAs {
                switchExpr {
                    val outerYields = mutableListOf<ASTExpression>()

                    it::getTestedExpression shouldBe variableAccess("day")

                    switchArrow {
                        switchLabel {
                            variableAccess("FRIDAY")
                        }
                        int(6).also { outerYields += it }
                    }

                    switchArrow {
                        switchLabel {
                            variableAccess("WEDNESDAY")
                        }

                        switchExpr {
                            it::getTestedExpression shouldBe variableAccess("foo")

                            switchArrow {
                                switchLabel {
                                    int(2)
                                }
                                int(5)
                            }
                            switchArrow {
                                switchDefaultLabel()
                                block {
                                    yieldStatement {
                                        int(7)
                                    }
                                }
                            }
                        }.also { outerYields += it }
                    }

                    switchArrow {
                        switchDefaultLabel()
                        block {
                            yieldStatement {
                                int(4).also { outerYields += it }
                            }
                        }
                    }

                    outerYields[0].shouldMatchN {
                        int(6)
                    }

                    outerYields[1].shouldMatchN {
                        switchExpr(EmptyAssertions)
                    }

                    outerYields[2].shouldMatchN {
                        int(4)
                    }
                }
            }
        }
    }

    parserTestContainer("Non-fallthrough nested in fallthrough", javaVersions = switchVersions) {
        inContext(StatementParsingCtx) {
            """
            switch (day) {
                case FRIDAY: foo(); break;
                case WEDNESDAY  : switch (foo) {
                  case 2 -> 5;
                  default -> 3;
                }
                default             : bar();
            }
        """ should parseAs {
                switchStmt {

                    // TODO Needs typeres
                    // it::isExhaustiveEnumSwitch shouldBe false
                    it::getTestedExpression shouldBe variableAccess("day")

                    it.branches.toList() shouldBe listOf(


                            switchFallthrough {
                                switchLabel {
                                    variableAccess("FRIDAY")
                                }
                                exprStatement()
                                breakStatement()
                            },
                            switchFallthrough {
                                switchLabel {
                                    variableAccess("WEDNESDAY")
                                }
                                switchStmt {
                                    variableAccess("foo")
                                    switchArrow {
                                        switchLabel()
                                        int(5)
                                    }
                                    switchArrow {
                                        switchDefaultLabel()
                                        int(3)
                                    }
                                }
                            },
                            switchFallthrough {
                                switchDefaultLabel()
                                exprStatement()
                            }
                    )
                }
            }
        }
    }

    parserTestContainer("Switch statement with non-fallthrough labels", javaVersions = switchVersions) {
        inContext(StatementParsingCtx) {
            """
        switch (day) {
            case THURSDAY, SATURDAY     -> System.out.println("  8");
            case WEDNESDAY              -> System.out.println("  9");
        }
        """ should parseAs {
                switchStmt {
                    it::getTestedExpression shouldBe variableAccess("day")

                    switchArrow {
                        switchLabel {
                            variableAccess("THURSDAY")
                            variableAccess("SATURDAY")
                        }
                        methodCall("println")
                    }
                    switchArrow {
                        switchLabel {
                            variableAccess("WEDNESDAY")
                        }
                        methodCall("println")
                    }
                }
            }
        }
    }

    parserTestContainer("Fallthrough switch statement", javaVersions = Earliest..Latest) {
        inContext(StatementParsingCtx) {
            """
          switch (day) {
            case TUESDAY               : System.out.println("  7"); break;
            case THURSDAY              : System.out.println("  8"); break;
            default                    : break;
          }
        """ should parseAs {
                switchStmt {
                    val switch = it

                    it::getTestedExpression shouldBe variableAccess("day")

                    switchFallthrough {
                        switchLabel {
                            variableAccess("TUESDAY")
                        }

                        exprStatement()
                        breakStatement {
                            it::getTarget shouldBe switch
                        }
                    }

                    switchFallthrough {
                        switchLabel {
                            variableAccess("THURSDAY")
                        }

                        exprStatement()
                        breakStatement()
                    }

                    switchFallthrough {
                        switchDefaultLabel()
                        breakStatement()
                    }
                }
            }
        }
    }
})
