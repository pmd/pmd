/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType.READ
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType.WRITE
import net.sourceforge.pmd.lang.java.ast.AssignmentOp.*

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTAssignmentExpressionTest : ParserTestSpec({

    parserTest("Simple assignment expressions") {

        inContext(ExpressionParsingCtx) {

            "a = b -> { foo(b); }" should parseAs {
                assignmentExpr(ASSIGN) {
                    it::isCompound shouldBe false

                    it::getLeftOperand shouldBe variableAccess("a", WRITE)

                    it::getRightOperand shouldBe child<ASTLambdaExpression> {
                        unspecifiedChildren(2)
                    }
                }
            }

            "a = 2" should parseAs {
                assignmentExpr(ASSIGN) {
                    it::isCompound shouldBe false

                    it::getLeftOperand shouldBe variableAccess("a", WRITE)

                    it::getRightOperand shouldBe int(2)
                }
            }

            "a.b().f *= 2" should parseAs {
                assignmentExpr(MUL_ASSIGN) {
                    it::isCompound shouldBe true

                    it::getLeftOperand shouldBe fieldAccess("f", WRITE)
                    it::getRightOperand shouldBe int(2)

                }
            }

            "a >>>= 2" should parseAs {
                assignmentExpr(UNSIGNED_RIGHT_SHIFT_ASSIGN) {
                    it::isCompound shouldBe true


                    it::getLeftOperand shouldBe variableAccess("a", WRITE)

                    it::getRightOperand shouldBe int(2)
                }
            }

            "a %= b" should parseAs {
                assignmentExpr(MOD_ASSIGN)
            }

            "a /= b" should parseAs {
                assignmentExpr(DIV_ASSIGN)
            }

            "a &= b" should parseAs {
                assignmentExpr(AND_ASSIGN)
            }

            "a |= b" should parseAs {
                assignmentExpr(OR_ASSIGN)
            }

            "a ^= b" should parseAs {
                assignmentExpr(XOR_ASSIGN)
            }

            "a += b" should parseAs {
                assignmentExpr(ADD_ASSIGN)
            }

            "a -= b" should parseAs {
                assignmentExpr(SUB_ASSIGN)
            }

            "a <<= b" should parseAs {
                assignmentExpr(LEFT_SHIFT_ASSIGN)
            }

            "a >>= b" should parseAs {
                assignmentExpr(RIGHT_SHIFT_ASSIGN)
            }

            "a >>>= b" should parseAs {
                assignmentExpr(UNSIGNED_RIGHT_SHIFT_ASSIGN)
            }
        }
    }

    parserTest("Right associativity") {

        inContext(ExpressionParsingCtx) {
            "a = b = c" should parseAs {
                assignmentExpr(ASSIGN) {
                    it::getLeftOperand shouldBe variableAccess("a", WRITE)

                    it::getRightOperand shouldBe assignmentExpr(ASSIGN) {
                        it::getLeftOperand shouldBe variableAccess("b", WRITE)
                        it::getRightOperand shouldBe variableAccess("c", READ)
                    }
                }
            }
        }
    }
})
