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
                it::isQualified shouldBe false
                it::getLhsExpression shouldBe null

                it::getArgumentsList shouldBe child {}
            }
        }

        "Foo() { <String>this(); }" should matchDeclaration<ASTConstructorDeclaration> {

            child<ASTFormalParameters> { }

            child<ASTExplicitConstructorInvocation> {
                it::isThis shouldBe true
                it::isSuper shouldBe false
                it::isQualified shouldBe false
                it::getLhsExpression shouldBe null


                it::getExplicitTypeArguments shouldBe child {
                    classType("String")
                }

                it::getArgumentsList shouldBe child {}
            }
        }
    }

    parserTest("Explicit super invocation") {

        "Foo() { super(); }" should matchDeclaration<ASTConstructorDeclaration> {

            child<ASTFormalParameters> { }

            child<ASTExplicitConstructorInvocation> {
                it::isThis shouldBe false
                it::isSuper shouldBe true
                it::isQualified shouldBe false
                it::getLhsExpression shouldBe null
                it::getExplicitTypeArguments shouldBe null

                it::getArgumentsList shouldBe child {}

            }
        }

        "Foo() { <String>super(); }" should matchDeclaration<ASTConstructorDeclaration> {

            child<ASTFormalParameters> { }

            child<ASTExplicitConstructorInvocation> {
                it::isThis shouldBe false
                it::isSuper shouldBe true
                it::isQualified shouldBe false
                it::getLhsExpression shouldBe null

                it::getExplicitTypeArguments shouldBe child {
                    classType("String")
                }

                it::getArgumentsList shouldBe child {}
            }
        }
    }


    parserTest("Explicit super invocation with LHS") {

        "Foo() { o.super(); }" should matchDeclaration<ASTConstructorDeclaration> {

            child<ASTFormalParameters> { }

            child<ASTExplicitConstructorInvocation> {
                it::isThis shouldBe false
                it::isSuper shouldBe true
                it::isQualified shouldBe true
                it::getArgumentCount shouldBe 0

                it::getExplicitTypeArguments shouldBe null
                it::getLhsExpression shouldBe variableRef("o")

                it::getArgumentsList shouldBe child {}
            }
        }

        "Foo() { o.<String>super(); }" should matchDeclaration<ASTConstructorDeclaration> {

            child<ASTFormalParameters> { }

            child<ASTExplicitConstructorInvocation> {
                it::isThis shouldBe false
                it::isSuper shouldBe true
                it::isQualified shouldBe true
                it::getArgumentCount shouldBe 0

                it::getLhsExpression shouldBe variableRef("o")

                it::getExplicitTypeArguments shouldBe child {
                    classType("String")
                }

                it::getArgumentsList shouldBe child { }
            }
        }

        "Foo() { o.<S>foo().<String>super(); }" should matchDeclaration<ASTConstructorDeclaration> {

            child<ASTFormalParameters> { }

            child<ASTExplicitConstructorInvocation> {
                it::isThis shouldBe false
                it::isSuper shouldBe true
                it::isQualified shouldBe true
                it::getArgumentCount shouldBe 0

                it::getLhsExpression shouldBe child<ASTMethodCall>(ignoreChildren = true) { }

                it::getExplicitTypeArguments shouldBe child {
                    classType("String")
                }

                it::getArgumentsList shouldBe child { }
            }
        }

        // An explicit constructor invocation statement in a constructor body may not refer to any instance
        // variables or instance methods or inner classes declared in this class or any superclass, or use
        // this or super in any expression; otherwise, a compile-time error occurs.

        // so we don't test those
    }
})
