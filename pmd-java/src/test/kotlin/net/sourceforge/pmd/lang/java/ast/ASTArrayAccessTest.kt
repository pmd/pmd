package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * Nodes that previously corresponded to ASTAllocationExpression.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTArrayAccessTest : ParserTestSpec({

    parserTest("Array access auto disambiguation") {

        "a.b[0]" should matchExpr<ASTArrayAccess> {

            it::getLhsExpression shouldBe child<ASTFieldAccess> {
                it::getFieldName shouldBe "b"

                it::getLhsExpression shouldBe child<ASTAmbiguousName> {
                    it::getName shouldBe "a"
                }
            }

            it::getIndexExpression shouldBe child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 0
            }
        }


        "b[0]" should matchExpr<ASTArrayAccess> {

            it::getLhsExpression shouldBe child<ASTVariableAccess> {
                it::getVariableName shouldBe "b"
            }


            it::getIndexExpression shouldBe child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 0
            }
        }
    }
})
