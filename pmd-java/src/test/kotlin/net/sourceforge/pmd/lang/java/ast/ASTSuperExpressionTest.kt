/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.test.ast.shouldBe

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTSuperExpressionTest : ParserTestSpec({
    parserTestContainer("Unqualified super") {
        inContext(ExpressionParsingCtx) {
            "super.foo()" should parseAs {
                methodCall("foo") {
                    it::getQualifier shouldBe child<ASTSuperExpression> {}

                    it::getArguments shouldBe argList { }
                }
            }
        }
    }

    parserTestContainer("Neg cases") {
        inContext(ExpressionParsingCtx) {
            // single super should be followed by either
            // a method call, field access, or method reference
            "super" shouldNot parse()

            // type arguments and annotations are disallowed on the qualifier
            "T.B<C>.super::foo" shouldNot parse()
            "T.B<C>.super.foo()" shouldNot parse()
            "T.@F B.super.foo()" shouldNot parse()
        }
    }

    parserTestContainer("Qualified super") {
        inContext(ExpressionParsingCtx) {
            "Type.super.foo()" should parseAs {
                methodCall("foo") {
                    it::getQualifier shouldBe child<ASTSuperExpression> {
                        it::getQualifier shouldBe classType("Type")
                    }

                    unspecifiedChild()
                }
            }

            "net.sourceforge.pmd.lang.java.ast.ASTThisExpression.super.foo()" should parseAs {
                methodCall("foo") {
                    it::getQualifier shouldBe child<ASTSuperExpression> {
                        it::getQualifier shouldBe qualClassType("net.sourceforge.pmd.lang.java.ast.ASTThisExpression") {
                            it::getTypeArguments shouldBe null
                            it::getQualifier shouldBe null

                            it::getAmbiguousLhs shouldBe child {
                                it::getName shouldBe "net.sourceforge.pmd.lang.java.ast"
                            }
                        }
                    }

                    unspecifiedChild()
                }
            }
        }
    }
})
