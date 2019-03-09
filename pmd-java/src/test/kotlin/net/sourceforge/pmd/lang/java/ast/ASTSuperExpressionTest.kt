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
            it::getLhsExpression shouldBe child<ASTSuperExpression> {}

            it::getArguments shouldBe child {  }

        }

    }

    parserTest("Qualified super") {
        "Type.super" should matchExpr<ASTSuperExpression> {

            it::getQualifier shouldBe child {
                it::getImage shouldBe "Type"
            }
        }

        "net.sourceforge.pmd.lang.java.ast.ASTThisExpression.super" should matchExpr<ASTSuperExpression> {

            it::getQualifier shouldBe child {
                it::getImage shouldBe "ASTThisExpression"
                it::getTypeArguments shouldBe null
                it::getLhsType shouldBe null

                it::getAmbiguousLhs shouldBe child {
                    it::getName shouldBe "net.sourceforge.pmd.lang.java.ast"
                }
            }
        }
    }
})