package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTSuperExpressionTest : ParserTestSpec({

    parserTest("Unqualified super") {

        "super" should matchExpr<ASTSuperExpression> { }

        "super.foo()" should matchExpr<ASTMethodCall> {
            it::getLhsExpression shouldBePresent child<ASTSuperExpression> {}

            it::getArguments shouldBe child {  }

        }

    }

    parserTest("Qualified super") {
        "Type.super" should matchExpr<ASTSuperExpression> {

            it::getQualifier shouldBePresent child {
                it::getImage shouldBe "Type"
            }
        }

        "net.sourceforge.pmd.lang.java.ast.ASTThisExpression.super" should matchExpr<ASTSuperExpression> {

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