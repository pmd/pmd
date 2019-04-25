/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * Nodes that previously corresponded to ASTAllocationExpression.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTExplicitConstructorInvocationTest : ParserTestSpec({

    parserTest("Explicit this invocation") {

        "Foo() { this(); }" should matchDeclaration<ASTConstructorDeclaration> {

            child<ASTFormalParameters> { }

            child<ASTExplicitConstructorInvocation> {

                it::isThis shouldBe true
                it::isSuper shouldBe false

                child<ASTArgumentList> {}
            }
        }

        "Foo() { <String>this(); }" should matchDeclaration<ASTConstructorDeclaration> {

            child<ASTFormalParameters> { }

            child<ASTExplicitConstructorInvocation> {
                it::isThis shouldBe true
                it::isSuper shouldBe false

                child<ASTTypeArguments> {
                    unspecifiedChild()
                }

                child<ASTArgumentList> {}
            }
        }
    }

    parserTest("Explicit super invocation") {

        "Foo() { super(); }" should matchDeclaration<ASTConstructorDeclaration> {

            child<ASTFormalParameters> { }

            child<ASTExplicitConstructorInvocation> {
                it::isThis shouldBe false
                it::isSuper shouldBe true

                child<ASTArgumentList> {

                }
            }
        }

        "Foo() { <String>super(); }" should matchDeclaration<ASTConstructorDeclaration> {

            child<ASTFormalParameters> { }

            child<ASTExplicitConstructorInvocation> {
                it::isThis shouldBe false
                it::isSuper shouldBe true

                child<ASTTypeArguments> {
                    unspecifiedChild()
                }

                child<ASTArgumentList> {

                }
            }
        }
    }


    parserTest("Explicit super invocation with LHS") {

        "Foo() { o.super(); }" should matchDeclaration<ASTConstructorDeclaration> {

            child<ASTFormalParameters> { }

            child<ASTExplicitConstructorInvocation> {
                it::isThis shouldBe false
                it::isSuper shouldBe true

                child<ASTVariableReference> { }

                child<ASTArgumentList> {

                }
            }
        }

        "Foo() { o.<String>super(); }" should matchDeclaration<ASTConstructorDeclaration> {

            child<ASTFormalParameters> { }

            child<ASTExplicitConstructorInvocation> {
                it::isThis shouldBe false
                it::isSuper shouldBe true

                child<ASTVariableReference> { }

                child<ASTTypeArguments> {
                    unspecifiedChild()
                }

                child<ASTArgumentList> {

                }
            }
        }
    }
})
