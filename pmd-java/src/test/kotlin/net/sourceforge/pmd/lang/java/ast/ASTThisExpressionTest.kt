package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTThisExpressionTest : ParserTestSpec({

    parserTest("Unqualified this") {

        inContext(ExpressionParsingCtx) {
            "this" should parseAs { thisExpr { null } }
        }

    }

    parserTest("Qualified this") {
        inContext(ExpressionParsingCtx) {
            "Type.this" should parseAs {
                thisExpr {
                    classType("Type")
                }
            }

            "net.sourceforge.pmd.lang.java.ast.ASTThisExpression.this" should parseAs {
                thisExpr {
                    qualClassType("net.sourceforge.pmd.lang.java.ast.ASTThisExpression") {
                        it::getTypeArguments shouldBe null
                        it::getQualifier shouldBe null

                        it::getAmbiguousLhs shouldBe child {
                            it::getName shouldBe "net.sourceforge.pmd.lang.java.ast"
                        }
                    }
                }
            }
        }
    }


    parserTest("Neg cases") {
        inContext(ExpressionParsingCtx) {

            // type arguments and annots are disallowed on the qualifier
            "T.B<C>.this" shouldNot parse()
            "T.@F B.this" shouldNot parse()
        }
    }



    parserTest("This/cast lookahead bug in parens") {

        inContext(ExpressionParsingCtx) {

            """
                (Set<String>) (new Transformer() {
                    public Object transform(final Object obj) {
                        final String value = this.attributes.get(key);
                    }
                })
            """.trim() should parseAs {
                castExpr {
                    classType("Set") {
                        unspecifiedChild()
                    }
                    parenthesized {
                        child<ASTConstructorCall>(ignoreChildren = true) {

                        }
                    }
                }
            }
            """
                (Set<String>) (OUTER.this)
            """.trim() should parseAs {
                castExpr {
                    classType("Set") {
                        unspecifiedChild()
                    }
                    parenthesized {
                        child<ASTThisExpression>(ignoreChildren = true) {

                        }
                    }
                }
            }

            """
                (Set<String>) new Transformer() {
                    public Object transform(final Object obj) {
                        final String value = HGXLIFFTypeConfiguration.this.attributes.get(key);
                    }
                }
            """.trim() should parseAs {
                castExpr {
                    classType("Set") {
                        unspecifiedChild()
                    }
                    child<ASTConstructorCall>(ignoreChildren = true) {

                    }
                }
            }
        }

    }


})
