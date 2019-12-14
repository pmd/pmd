/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.BinaryOp.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J12__PREVIEW
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J13__PREVIEW
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.StatementParsingCtx
import net.sourceforge.pmd.lang.java.ast.UnaryOp.UNARY_MINUS


/**
 * @author Clément Fournier
 */
class ASTSwitchExpressionTests : ParserTestSpec({


    parserTest("No switch expr before j12 preview", javaVersions = !J12__PREVIEW) {
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

    parserTest("No yield stmt before j13 preview", javaVersions = !J13__PREVIEW) {
        inContext(ExpressionParsingCtx) {

        """
            switch (day) {
                default             -> {
                    yield result * 4;
                }
            }
        """ shouldNot parse()
        }

    }


    parserTest("Simple switch expressions", javaVersions = listOf(J13__PREVIEW)) {


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



    parserTest("Non-trivial labels", javaVersions = listOf(J12__PREVIEW, J13__PREVIEW)) {
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

    parserTest("Switch expr precedence", javaVersions = listOf(J12__PREVIEW, J13__PREVIEW)) {


        inContext(ExpressionParsingCtx) {

            "2 * switch (day) {default -> 6;}" should parseAs {
                multiplicativeExpr(MUL) {
                    number()
                    switchExpr()
                }
            }

            "switch (day) {default -> 6;} * 4" should parseAs {
                multiplicativeExpr(MUL) {
                    switchExpr()
                    number()
                }
            }

            "switch (day) {default -> 6;} + 6" should parseAs {
                additiveExpr(ADD) {
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


    parserTest("Nested switch expressions", javaVersions = listOf(J12__PREVIEW, J13__PREVIEW)) {

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

                    switchArrow {
                        switchLabel {
                            variableAccess("FRIDAY")
                        }
                        int(6)
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
                                int(3)
                            }
                        }
                    }

                    switchArrow {
                        switchDefaultLabel()
                        int(3)
                    }
                }
            }
        }
    }


    parserTest("Non-fallthrough nested in fallthrough", javaVersions = listOf(J12__PREVIEW, J13__PREVIEW)) {

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

                    it::isExhaustiveEnumSwitch shouldBe false
                    it::getTestedExpression shouldBe variableAccess("day")

                    switchFallthrough {
                        switchLabel {
                            variableAccess("FRIDAY")
                        }
                        exprStatement()
                        breakStatement()
                    }

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
                    }
                    switchFallthrough {
                        switchDefaultLabel()
                        exprStatement()
                    }
                }
            }
        }
    }


    parserTest("Switch statement with non-fallthrough labels", javaVersions = listOf(J12__PREVIEW, J13__PREVIEW)) {

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

    parserTest("Fallthrough switch statement", javaVersions = Earliest..Latest) {

        inContext(StatementParsingCtx) {
            """
          switch (day) {
            case TUESDAY               : System.out.println("  7"); break;
            case THURSDAY              : System.out.println("  8"); break;
            default                    : break;
          }
        """ should parseAs {
                switchStmt {

                    it::getTestedExpression shouldBe variableAccess("day")

                    switchFallthrough {
                        switchLabel {
                            variableAccess("TUESDAY")
                        }

                        exprStatement()
                        breakStatement()
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
