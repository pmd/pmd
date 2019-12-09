/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.BinaryOp.*
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx
import net.sourceforge.pmd.lang.java.ast.UnaryOp.PrefixOp.UNARY_MINUS


/**
 * @author Clément Fournier
 */
class ASTSwitchExpressionTests : ParserTestSpec({


    parserTest("Simple switch expressions") {


        """
            switch (day) {
                case FRIDAY, SUNDAY -> 6;
                case WEDNESDAY      -> 9;
                case SONNABEND      -> throw new MindBlownException();
                default             -> {
                    int k = day * 2;
                    int result = f(k);
                    break result;
                }
            }
        """.trimIndent() should matchExpr<ASTSwitchExpression> {
            it::getTestedExpression shouldBe variableAccess("day")

            child<ASTSwitchLabeledExpression> {
                child<ASTSwitchLabel> {
                    it::isDefault shouldBe false

                    variableAccess("FRIDAY")
                    variableAccess("SUNDAY")
                }
                int(6)
            }

            child<ASTSwitchLabeledExpression> {
                child<ASTSwitchLabel> {
                    it::isDefault shouldBe false

                    variableAccess("WEDNESDAY")
                }
                int(9)
            }

            child<ASTSwitchLabeledThrowStatement> {
                child<ASTSwitchLabel> {
                    it::isDefault shouldBe false

                    variableAccess("SONNABEND")
                }
                child<ASTThrowStatement> {
                    child<ASTConstructorCall> {
                        child<ASTClassOrInterfaceType> {
                            it::getTypeImage shouldBe "MindBlownException"
                        }
                        child<ASTArgumentList> {}
                    }
                }
            }

            child<ASTSwitchLabeledBlock> {
                child<ASTSwitchLabel> {
                    it::isDefault shouldBe true
                }
                child<ASTBlock> {
                    child<ASTBlockStatement>(ignoreChildren = true) {}
                    child<ASTBlockStatement>(ignoreChildren = true) {}
                    child<ASTBlockStatement> {
                        child<ASTStatement> {
                            child<ASTBreakStatement> {
                                it::getImage shouldBe "result"
                            }
                        }
                    }
                }
            }
        }
    }


    parserTest("Non-trivial labels") {


        """ switch (day) {
                case a + b, 4 * 2 / Math.PI -> 6;
            }
        """ should matchExpr<ASTSwitchExpression> {

            it::getTestedExpression shouldBe variableAccess("day")

            child<ASTSwitchLabeledExpression> {

                child<ASTSwitchLabel> {
                    it::isDefault shouldBe false

                    additiveExpr(ADD) {
                        variableAccess("a")
                        variableAccess("b")
                    }
                    multiplicativeExpr(DIV) {
                        multiplicativeExpr(MUL) {
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

    parserTest("Switch precedence") {


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


    parserTest("Nested switch expressions") {


        """
            switch (day) {
                case FRIDAY -> 6;
                case WEDNESDAY      -> switch (foo) {
                  case 2 -> 5;
                  default -> 3;
                };
                default             -> 3;
            }
        """.trimIndent() should matchExpr<ASTSwitchExpression> {

            it::getTestedExpression shouldBe variableAccess("day")

            child<ASTSwitchLabeledExpression> {

                child<ASTSwitchLabel> {
                    it::isDefault shouldBe false

                    variableAccess("FRIDAY")
                }
                int(6)
            }

            child<ASTSwitchLabeledExpression> {

                child<ASTSwitchLabel> {
                    it::isDefault shouldBe false

                    variableAccess("WEDNESDAY")
                }

                child<ASTSwitchExpression> {

                    it::getTestedExpression shouldBe variableAccess("foo")

                    child<ASTSwitchLabeledExpression> {
                        child<ASTSwitchLabel> {
                            it::isDefault shouldBe false

                            int(2)
                        }
                        int(5)
                    }
                    child<ASTSwitchLabeledExpression> {
                        child<ASTSwitchLabel> {
                            it::isDefault shouldBe true
                        }
                        int(3)
                    }
                }
            }
            child<ASTSwitchLabeledExpression> {
                child<ASTSwitchLabel> {
                    it::isDefault shouldBe true
                }
                int(3)
            }
        }
    }


    parserTest("Non-fallthrough nested in fallthrough") {


        """
            switch (day) {
                case FRIDAY: foo(); break;
                case WEDNESDAY  : switch (foo) {
                  case 2 -> 5;
                  default -> 3;
                }
                default             : bar();
            }
        """.trimIndent() should matchStmt<ASTSwitchStatement> {
            it::isExhaustiveEnumSwitch shouldBe false

            it::getTestedExpression shouldBe variableAccess("day")

            child<ASTSwitchLabel> {
                it::isDefault shouldBe false

                child<ASTExpression>(ignoreChildren = true) {}

            }
            child<ASTBlockStatement> {
                child<ASTStatement> {
                    child<ASTStatementExpression>(ignoreChildren = true) {}
                }
            }
            child<ASTBlockStatement> {
                child<ASTStatement> {
                    child<ASTBreakStatement> {}
                }
            }
            child<ASTSwitchLabel> {
                it::isDefault shouldBe false

                child<ASTExpression>(ignoreChildren = true) {}

            }
            child<ASTBlockStatement> {
                child<ASTStatement> {
                    child<ASTSwitchStatement> {

                        it::getTestedExpression shouldBe child(ignoreChildren = true) {}

                        child<ASTSwitchLabeledExpression>(ignoreChildren = true) {}
                        child<ASTSwitchLabeledExpression>(ignoreChildren = true) {}

                    }
                }
            }
            child<ASTSwitchLabel> {
                it::isDefault shouldBe true
            }
            child<ASTBlockStatement> {
                child<ASTStatement> {
                    child<ASTStatementExpression>(ignoreChildren = true) {}
                }
            }
        }
    }


    parserTest("Switch statement with non-fallthrough labels") {

        """
        switch (day) {
            case THURSDAY, SATURDAY     -> System.out.println("  8");
            case WEDNESDAY              -> System.out.println("  9");
        }
        """.trimIndent() should matchStmt<ASTSwitchStatement> {
            it::isExhaustiveEnumSwitch shouldBe false

            it::getTestedExpression shouldBe variableAccess("day")

            child<ASTSwitchLabeledExpression> {
                child<ASTSwitchLabel> {
                    it::isDefault shouldBe false

                    variableAccess("THURSDAY")
                    variableAccess("SATURDAY")
                }
                child<ASTMethodCall>(ignoreChildren = true) {
                    it::getMethodName shouldBe "println"
                }
            }
            child<ASTSwitchLabeledExpression> {
                child<ASTSwitchLabel> {
                    it::isDefault shouldBe false

                    variableAccess("WEDNESDAY")
                }
                child<ASTMethodCall>(ignoreChildren = true) {
                    it::getMethodName shouldBe "println"
                }
            }
        }
    }

    parserTest("Fallthrough switch statement") {

        """
          switch (day) {
            case TUESDAY               : System.out.println("  7"); break;
            case THURSDAY, SATURDAY    : System.out.println("  8"); break;
            default                    : break;
          }
        """.trimIndent() should matchStmt<ASTSwitchStatement> {
            it::isExhaustiveEnumSwitch shouldBe false

            it::getTestedExpression shouldBe variableAccess("day")


            child<ASTSwitchLabel> {
                it::isDefault shouldBe false

                variableAccess("TUESDAY")
            }
            child<ASTBlockStatement>(ignoreChildren = true) { }


            child<ASTBlockStatement> {
                child<ASTStatement> {
                    child<ASTBreakStatement> {}
                }
            }

            child<ASTSwitchLabel> {
                it::isDefault shouldBe false

                child<ASTExpression>(ignoreChildren = true) {}
                child<ASTExpression>(ignoreChildren = true) {}
            }

            child<ASTBlockStatement>(ignoreChildren = true) { }


            child<ASTBlockStatement> {
                child<ASTStatement> {
                    child<ASTBreakStatement> {}
                }
            }

            child<ASTSwitchLabel> {
                it::isDefault shouldBe true
            }

            child<ASTBlockStatement> {
                child<ASTStatement> {
                    child<ASTBreakStatement> {}
                }
            }
        }
    }


})
