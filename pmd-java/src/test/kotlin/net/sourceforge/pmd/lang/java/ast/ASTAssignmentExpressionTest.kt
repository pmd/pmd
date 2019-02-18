package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTAssignmentExpressionTest : ParserTestSpec({

    parserTest("Simple assignment expressions") {

        "a = b -> { foo(b); }" should matchExpr<ASTAssignmentExpression> {
            it::getOp shouldBe AssignmentOp.EQ
            it::isCompound shouldBe false

            it::getLeftHandSide shouldBe child<ASTVariableReference> {
                it::getImage shouldBe "a"
            }

            it::getRightHandSide shouldBe child<ASTLambdaExpression> {
                unspecifiedChildren(2)
            }
        }

        "a = 2" should matchExpr<ASTAssignmentExpression> {
            it::getOp shouldBe AssignmentOp.EQ
            it::isCompound shouldBe false

            it::getLeftHandSide shouldBe child<ASTVariableReference> {
                it::getImage shouldBe "a"
            }

            it::getRightHandSide shouldBe child<ASTNumericLiteral> {}
        }

        "a.b().f *= 2" should matchExpr<ASTAssignmentExpression> {
            it::getOp shouldBe AssignmentOp.MUL_EQ
            it::isCompound shouldBe true

            it::getLeftHandSide shouldBe child<ASTFieldAccess> {
                it.fieldName shouldBe "f"

                unspecifiedChild()
            }

            it::getRightHandSide shouldBe child<ASTNumericLiteral> {}

        }

        "a >>>= 2" should matchExpr<ASTAssignmentExpression> {
            it::getOp shouldBe AssignmentOp.UNSIGNED_RIGHT_SHIFT_EQ
            it::isCompound shouldBe true


            it::getLeftHandSide shouldBe child<ASTVariableReference> {
                it::getImage shouldBe "a"
            }

            it::getRightHandSide shouldBe child<ASTNumericLiteral> {}
        }

    }

    parserTest("Right associativity") {

        "a = b = 3" should matchExpr<ASTAssignmentExpression> {
            it::getOp shouldBe AssignmentOp.EQ
            it::isCompound shouldBe false

            it::getLeftHandSide shouldBe child<ASTVariableReference> {
                it::getImage shouldBe "a"
            }

            it::getRightHandSide shouldBe child<ASTAssignmentExpression> {
                it::getOp shouldBe AssignmentOp.EQ
                it::isCompound shouldBe false

                it::getLeftHandSide shouldBe child<ASTVariableReference> {
                    it::getImage shouldBe "b"
                }

                it::getRightHandSide shouldBe child<ASTNumericLiteral> {}
            }
        }
    }
})