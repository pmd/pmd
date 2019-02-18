package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTThisExpressionTest : ParserTestSpec({

    parserTest("Unqualified this") {

        "this" should matchExpr<ASTThisExpression> { }

    }

    parserTest("Qualified this") {
        "Type.this" should matchExpr<ASTThisExpression> {

            it::getQualifier shouldBePresent child {
                it::getImage shouldBe "Type"
            }
        }

        "net.sourceforge.pmd.lang.java.ast.ASTThisExpression.this" should matchExpr<ASTThisExpression> {

            it::getQualifier shouldBePresent child {
                it::getImage shouldBe "ASTThisExpression"
                it::getTypeArguments.shouldBeEmpty()
                it::getLhsType.shouldBeEmpty()

                it::getAmbiguousLhs shouldBePresent child {
                    it::getName shouldBe "net.sourceforge.pmd.lang.java.ast"
                }
            }
        }
    }
})