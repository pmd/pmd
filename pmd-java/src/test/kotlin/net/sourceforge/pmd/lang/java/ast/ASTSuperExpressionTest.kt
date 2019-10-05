package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTSuperExpressionTest : ParserTestSpec({

    parserTest("Unqualified super") {

        "super.foo()" should matchExpr<ASTMethodCall> {
            it::getQualifier shouldBe child<ASTSuperExpression> {}

            it::getArguments shouldBe child {  }

        }

    }

    parserTest("Neg cases") {
        inContext(ExpressionParsingCtx) {
            // single super should be followed by either
            // a method call, field access, or method reference
            "super" shouldNot parse()

            // type arguments and annots are disallowed on the qualifier
            "T.B<C>.super::foo" shouldNot parse()
            "T.B<C>.super.foo()" shouldNot parse()
            "T.@F B.super.foo()" shouldNot parse()
        }

    }

    parserTest("Qualified super") {
        "Type.super.foo()" should matchExpr<ASTMethodCall> {

            it::getQualifier shouldBe child<ASTSuperExpression> {
                it::getQualifier shouldBe child {
                    it::getImage shouldBe "Type"
                }
            }

            unspecifiedChild()
        }

        "net.sourceforge.pmd.lang.java.ast.ASTThisExpression.super.foo()" should matchExpr<ASTMethodCall> {

            it::getQualifier shouldBe child<ASTSuperExpression> {
                it::getQualifier shouldBe child {
                    it::getImage shouldBe "ASTThisExpression"
                    it::getTypeArguments shouldBe null
                    it::getLhsType shouldBe null

                    it::getAmbiguousLhs shouldBe child {
                        it::getName shouldBe "net.sourceforge.pmd.lang.java.ast"
                    }
                }
            }

            unspecifiedChild()
        }
    }
})
