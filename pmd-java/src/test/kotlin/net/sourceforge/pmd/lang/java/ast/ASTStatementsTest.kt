/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

class ASTStatementsTest : ParserTestSpec({

    parserTest("Foreach loop") {

        inContext(StatementParsingCtx) {
            """
                 for (Integer i : new Iter<>()) 
                    loop();
            """ should parseAs {

                foreachLoop {

                    it::getVariableId shouldBe fromChild<ASTLocalVariableDeclaration, ASTVariableDeclaratorId> {
                        it::getTypeNode shouldBe classType("Integer")
                        fromChild<ASTVariableDeclarator, ASTVariableDeclaratorId> {
                            variableId("i")
                        }
                    }

                    it::getIterableExpr shouldBe constructorCall()

                    exprStatement {
                        methodCall("loop")
                    }
                }
            }
            """
                 for (@Nullable final Integer i : new Iter<>()) {
                 
                 
                 }
            """ should parseAs {

                foreachLoop {

                    localVarDecl {
                        annotation("Nullable")
                        classType("Integer")
                        variableDeclarator("i")
                    }

                    it.variableId::isFinal shouldBe true

                    it::getIterableExpr shouldBe constructorCall()

                    block()
                }
            }
        }
    }


    parserTest("For loop") {
        inContext(StatementParsingCtx) {

            "for (;;) {}" should parseAs {
                forLoop {
                    it::getInit shouldBe null
                    it::getCondition shouldBe null
                    it::getUpdate shouldBe null

                    block { }
                }
            }

            "for (int i = 0; i < 2; i++);" should parseAs {
                forLoop {
                    it::getInit shouldBe forInit {
                        localVarDecl()
                    }
                    it::getCondition shouldBe infixExpr(BinaryOp.LT) {
                        variableAccess("i")
                        int(2)
                    }
                    it::getUpdate shouldBe forUpdate {
                        statementExprList {
                            unspecifiedChild()
                        }
                    }
                    emptyStatement()
                }
            }

            "for (i = 1, j = 1; ; i *= 2, j += 2);" should parseAs {
                forLoop {
                    it::getInit shouldBe forInit {
                        statementExprList {
                            assignmentExpr(AssignmentOp.ASSIGN)
                            assignmentExpr(AssignmentOp.ASSIGN)
                        }
                    }
                    it::getCondition shouldBe null
                    it::getUpdate shouldBe forUpdate {
                        statementExprList {
                            assignmentExpr(AssignmentOp.MUL_ASSIGN)
                            assignmentExpr(AssignmentOp.ADD_ASSIGN)
                        }
                    }

                    emptyStatement()
                }
            }
        }
    }

    parserTest("Blocks") {

        inContext(StatementParsingCtx) {

            """
               {
                 for (;;) {}
                 for (Integer i : new Iter<>()) loop();
                 a = 0;
               }
            """ should parseAs {
                block {
                    forLoop()
                    foreachLoop()
                    exprStatement()
                }
            }
        }
    }
})
