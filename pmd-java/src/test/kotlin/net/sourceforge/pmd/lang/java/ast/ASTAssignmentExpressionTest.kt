package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType.READ
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType.WRITE

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTAssignmentExpressionTest : ParserTestSpec({

    parserTest("Simple assignment expressions") {

        "a = b -> { foo(b); }" should matchExpr<ASTAssignmentExpression> {
            it::getOperator shouldBe AssignmentOp.EQ
            it::isCompound shouldBe false

            it::getLeftOperand shouldBe variableAccess("a", WRITE)

            it::getRightOperand shouldBe child<ASTLambdaExpression> {
                unspecifiedChildren(2)
            }
        }

        "a = 2" should matchExpr<ASTAssignmentExpression> {
            it::getOperator shouldBe AssignmentOp.EQ
            it::isCompound shouldBe false

            it::getLeftOperand shouldBe variableAccess("a", WRITE)

            it::getRightOperand shouldBe int(2)
        }

        "a.b().f *= 2" should matchExpr<ASTAssignmentExpression> {
            it::getOperator shouldBe AssignmentOp.MUL_EQ
            it::isCompound shouldBe true

            it::getLeftOperand shouldBe fieldAccess("f", WRITE)
            it::getRightOperand shouldBe int(2)

        }

        "a >>>= 2" should matchExpr<ASTAssignmentExpression> {
            it::getOperator shouldBe AssignmentOp.UNSIGNED_RIGHT_SHIFT_EQ
            it::isCompound shouldBe true


            it::getLeftOperand shouldBe variableAccess("a", WRITE)

            it::getRightOperand shouldBe int(2)
        }

    }

    parserTest("Right associativity") {

        "a = b = c" should matchExpr<ASTAssignmentExpression> {
            it::getOperator shouldBe AssignmentOp.EQ
            it::isCompound shouldBe false

            it::getLeftOperand shouldBe variableAccess("a", WRITE)

            it::getRightOperand shouldBe assignmentExpr(AssignmentOp.EQ) {
                it::isCompound shouldBe false

                it::getLeftOperand shouldBe variableAccess("b", WRITE)
                it::getRightOperand shouldBe variableAccess("c", READ)
            }
        }
    }
})
