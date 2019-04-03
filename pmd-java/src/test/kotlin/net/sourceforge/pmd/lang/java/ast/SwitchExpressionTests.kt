/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe

/**
 * @author Clément Fournier
 */
class ASTSwitchExpressionTests : ParserTestSpec({


    parserTest("Simple switch expressions") {


        """
            switch (day) {
                case FRIDAY, SUNDAY -> 6;
                case WEDNESDAY      -> 9;
                default             -> {
                    int k = day * 2;
                    int result = f(k);
                    break result;
                }
            }
        """.trimIndent() should matchExpr<ASTSwitchExpression> {

            child<ASTExpression> {

                child<ASTPrimaryExpression> {

                    child<ASTPrimaryPrefix> {

                        child<ASTName> {
                            it.image shouldBe "day"
                        }
                    }
                }
            }

            child<ASTSwitchLabeledExpression> {

                child<ASTSwitchLabel> {
                    it.isDefault shouldBe false

                    child<ASTExpression> {

                        child<ASTPrimaryExpression> {

                            child<ASTPrimaryPrefix> {

                                child<ASTName> {
                                    it.image shouldBe "FRIDAY"
                                }
                            }
                        }
                    }

                    child<ASTExpression> {
                        it.isStandAlonePrimitive shouldBe false

                        child<ASTPrimaryExpression> {

                            child<ASTPrimaryPrefix> {

                                child<ASTName> {
                                    it.image shouldBe "SUNDAY"
                                }
                            }
                        }
                    }
                }

                child<ASTExpression>(ignoreChildren = true) {}

            }

            child<ASTSwitchLabeledExpression> {

                child<ASTSwitchLabel> {
                    it.isDefault shouldBe false

                    child<ASTExpression> {
                        it.isStandAlonePrimitive shouldBe false

                        child<ASTPrimaryExpression> {

                            child<ASTPrimaryPrefix> {

                                child<ASTName> {
                                    it.nameDeclaration shouldBe null
                                }
                            }
                        }
                    }
                }

                child<ASTExpression>(ignoreChildren = true) {}
            }

            child<ASTSwitchLabeledBlock> {

                child<ASTSwitchLabel> {
                    it.isDefault shouldBe true
                }

                child<ASTBlock> {

                    child<ASTBlockStatement>(ignoreChildren = true) {}
                    child<ASTBlockStatement>(ignoreChildren = true) {}

                    child<ASTBlockStatement> {
                        child<ASTStatement> {
                            child<ASTBreakStatement> {}
                        }
                    }
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

            child<ASTExpression> {

                child<ASTPrimaryExpression> {

                    child<ASTPrimaryPrefix> {

                        child<ASTName> {
                            it.image shouldBe "day"
                        }
                    }
                }
            }

            child<ASTSwitchLabeledExpression> {

                child<ASTSwitchLabel> {
                    it.isDefault shouldBe false

                    child<ASTExpression> {
                        child<ASTPrimaryExpression> {
                            child<ASTPrimaryPrefix> {
                                child<ASTName> {
                                    it.image shouldBe "FRIDAY"
                                }
                            }
                        }
                    }

                }

                child<ASTExpression>(ignoreChildren = true) {}

            }

            child<ASTSwitchLabeledExpression> {

                child<ASTSwitchLabel> {
                    it.isDefault shouldBe false

                    child<ASTExpression>(ignoreChildren = true) {}
                }

                child<ASTExpression> {


                    child<ASTSwitchExpression> {
                        child<ASTExpression>(ignoreChildren = true) {}

                        child<ASTSwitchLabeledExpression> {

                            child<ASTSwitchLabel> {
                                it.isDefault shouldBe false

                                child<ASTExpression>(ignoreChildren = true) {}
                            }
                            child<ASTExpression>(ignoreChildren = true) {}

                        }
                        child<ASTSwitchLabeledExpression> {

                            child<ASTSwitchLabel> {
                                it.isDefault shouldBe true
                            }
                            child<ASTExpression>(ignoreChildren = true) {}
                        }
                    }

                }
            }

            child<ASTSwitchLabeledExpression> {

                child<ASTSwitchLabel> {
                    it.isDefault shouldBe true
                }

                child<ASTExpression>(ignoreChildren = true) {}
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
            it.isExhaustiveEnumSwitch shouldBe false

            it.testedExpression shouldBe child(ignoreChildren = true) {}
            child<ASTSwitchLabel> {
                it.isDefault shouldBe false

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
                it.isDefault shouldBe false

                child<ASTExpression>(ignoreChildren = true) {}

            }
            child<ASTBlockStatement> {
                child<ASTStatement> {
                    child<ASTSwitchStatement> {

                        it.testedExpression shouldBe child(ignoreChildren = true) {}

                        child<ASTSwitchLabeledExpression>(ignoreChildren = true) {}
                        child<ASTSwitchLabeledExpression>(ignoreChildren = true) {}

                    }
                }
            }
            child<ASTSwitchLabel> {
                it.isDefault shouldBe true
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
            it.isExhaustiveEnumSwitch shouldBe false

            it.testedExpression shouldBe child {
                child<ASTPrimaryExpression> {
                    child<ASTPrimaryPrefix> {
                        child<ASTName> {
                            it.image shouldBe "day"
                        }
                    }
                }
            }

            child<ASTSwitchLabeledExpression> {

                child<ASTSwitchLabel> {
                    it.isDefault shouldBe false

                    child<ASTExpression>(ignoreChildren = true) {}
                    child<ASTExpression>(ignoreChildren = true) {}
                }

                child<ASTExpression>(ignoreChildren = true) {}
            }

            child<ASTSwitchLabeledExpression> {

                child<ASTSwitchLabel> {
                    it.isDefault shouldBe false

                    child<ASTExpression>(ignoreChildren = true) {}
                }

                child<ASTExpression>(ignoreChildren = true) {}
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
            it.isExhaustiveEnumSwitch shouldBe false

            it.testedExpression shouldBe child {
                child<ASTPrimaryExpression> {
                    child<ASTPrimaryPrefix> {
                        child<ASTName> {
                            it.image shouldBe "day"
                        }
                    }
                }
            }


            child<ASTSwitchLabel> {
                it.isDefault shouldBe false

                child<ASTExpression>(ignoreChildren = true) {}
            }

            child<ASTBlockStatement> {
                child<ASTStatement> {
                    child<ASTStatementExpression> {
                        child<ASTPrimaryExpression>(ignoreChildren = true) {}
                    }
                }
            }

            child<ASTBlockStatement> {
                child<ASTStatement> {
                    child<ASTBreakStatement> {}
                }
            }

            child<ASTSwitchLabel> {
                it.isDefault shouldBe false

                child<ASTExpression>(ignoreChildren = true) {}
                child<ASTExpression>(ignoreChildren = true) {}
            }

            child<ASTBlockStatement> {
                child<ASTStatement> {
                    child<ASTStatementExpression> {
                        child<ASTPrimaryExpression>(ignoreChildren = true) {}
                    }
                }
            }

            child<ASTBlockStatement> {
                child<ASTStatement> {
                    child<ASTBreakStatement> {}
                }
            }

            child<ASTSwitchLabel> {
                it.isDefault shouldBe true
            }

            child<ASTBlockStatement> {
                child<ASTStatement> {
                    child<ASTBreakStatement> {}
                }
            }
        }
    }


})
