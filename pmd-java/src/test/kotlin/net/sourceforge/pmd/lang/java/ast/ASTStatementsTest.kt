/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.types.JPrimitiveType
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.INT

class ASTStatementsTest : ParserTestSpec({

    parserTest("Foreach loop") {

        inContext(StatementParsingCtx) {
            """
                 for (Integer i : new Iter<>()) 
                    loop();
            """ should parseAs {

                foreachLoop {

                    it::getVarId shouldBe fromChild<ASTLocalVariableDeclaration, ASTVariableDeclaratorId> {
                        it::getModifiers shouldBe modifiers {}

                        it::getTypeNode shouldBe classType("Integer")
                        fromChild<ASTVariableDeclarator, ASTVariableDeclaratorId> {
                            variableId("i") {
                                it::isLocalVariable shouldBe false
                                it::isForLoopVariable shouldBe false
                                it::isForeachVariable shouldBe true
                            }
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
                 
                    continue;
                 }
            """ should parseAs {

                foreachLoop {

                    val foreach = it

                    localVarDecl {
                        it::getModifiers shouldBe modifiers {
                            annotation("Nullable")
                        }
                        classType("Integer")
                        variableDeclarator("i")
                    }

                    it.varId::isFinal shouldBe true

                    it::getIterableExpr shouldBe constructorCall()

                    block {
                        continueStatement {
                            it::getTarget shouldBe foreach
                        }
                    }
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
                        localVarDecl {
                            localVarModifiers()
                            primitiveType(INT)
                            varDeclarator {
                                variableId("i") {
                                    it::isLocalVariable shouldBe true // different from foreach too
                                    it::isForLoopVariable shouldBe true
                                    it::isForeachVariable shouldBe false
                                }
                                int(0)
                            }
                        }
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

    parserTest("Labeled statements") {

        inContext(StatementParsingCtx) {

            """
               {
                 l: for (;;) {
                    i: for (;;)
                        if (true || false)
                            break l;
                        else
                            continue i;
                 }

                 for (Integer i : new Iter<>())
                    break l;
               }
            """ should parseAs {
                block {
                    labeledStatement("l") {
                        forLoop {
                            val loopL = it
                            block {
                                labeledStatement("i") {
                                    forLoop {
                                        val loopI = it;
                                        ifStatement {
                                            it::getCondition shouldBe unspecifiedChild()
                                            it::getThenBranch shouldBe breakStatement("l") {
                                                it::getTarget shouldBe loopL
                                            }
                                            it::getElseBranch shouldBe continueStatement("i") {
                                                it::getTarget shouldBe loopI
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    foreachLoop {
                        unspecifiedChildren(2)
                        breakStatement("l") {
                            it::getTarget shouldBe null // invalid code
                        }
                    }
                }
            }
        }
    }
})
