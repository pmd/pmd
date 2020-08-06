/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

class ASTExplicitConstructorInvocationTest : ParserTestSpec({

    parserTest("Explicit this invocation") {

        inContext(TypeBodyParsingCtx) {

            "Foo() { this(); }" should parseAs {
                constructorDecl {

                    it::getModifiers shouldBe modifiers { }

                    formalsList(0) { }

                    block {
                        child<ASTExplicitConstructorInvocation> {
                            it::isThis shouldBe true
                            it::isSuper shouldBe false
                            it::isQualified shouldBe false
                            it::getQualifier shouldBe null

                            it::getArgumentsList shouldBe argList {}
                        }
                    }
                }
            }

            "Foo() { <String>this(); }" should parseAs {
                constructorDecl {

                    it::getModifiers shouldBe modifiers { }

                    formalsList(0) { }

                    block {
                        child<ASTExplicitConstructorInvocation> {
                            it::isThis shouldBe true
                            it::isSuper shouldBe false
                            it::isQualified shouldBe false
                            it::getQualifier shouldBe null


                            it::getExplicitTypeArguments shouldBe typeArgList {
                                classType("String")
                            }

                            it::getArgumentsList shouldBe argList {}
                        }
                    }
                }
            }
        }
    }

    parserTest("Explicit super invocation") {

        inContext(TypeBodyParsingCtx) {
            "Foo() { super(); }" should parseAs {
                constructorDecl {

                    it::getModifiers shouldBe modifiers { }

                    formalsList(0) { }

                    block {
                        child<ASTExplicitConstructorInvocation> {
                            it::isThis shouldBe false
                            it::isSuper shouldBe true
                            it::isQualified shouldBe false
                            it::getQualifier shouldBe null
                            it::getExplicitTypeArguments shouldBe null

                            it::getArgumentsList shouldBe argList {}

                        }
                    }
                }
            }
            "Foo() { <String>super(); }" should parseAs {
                constructorDecl {

                    it::getModifiers shouldBe modifiers { }

                    formalsList(0) { }

                    block {
                        child<ASTExplicitConstructorInvocation> {
                            it::isThis shouldBe false
                            it::isSuper shouldBe true
                            it::isQualified shouldBe false
                            it::getQualifier shouldBe null

                            it::getExplicitTypeArguments shouldBe typeArgList {
                                classType("String")
                            }

                            it::getArgumentsList shouldBe argList {}
                        }
                    }
                }
            }
        }
    }


    parserTest("Explicit super invocation with LHS") {

        inContext(TypeBodyParsingCtx) {
            "Foo() { o.super(); }" should parseAs {
                constructorDecl {

                    it::getModifiers shouldBe modifiers { }

                    formalsList(0) { }

                    block {
                        child<ASTExplicitConstructorInvocation> {
                            it::isThis shouldBe false
                            it::isSuper shouldBe true
                            it::isQualified shouldBe true
                            it::getArgumentCount shouldBe 0

                            it::getExplicitTypeArguments shouldBe null
                            it::getQualifier shouldBe variableAccess("o")

                            it::getArgumentsList shouldBe argList {}
                        }
                    }
                }
            }

            "Foo() { o.<String>super(); }" should parseAs {
                constructorDecl {

                    it::getModifiers shouldBe modifiers { }

                    formalsList(0) { }

                    block {
                        child<ASTExplicitConstructorInvocation> {
                            it::isThis shouldBe false
                            it::isSuper shouldBe true
                            it::isQualified shouldBe true
                            it::getArgumentCount shouldBe 0

                            it::getQualifier shouldBe variableAccess("o")

                            it::getExplicitTypeArguments shouldBe typeArgList {
                                classType("String")
                            }

                            it::getArgumentsList shouldBe argList { }
                        }
                    }
                }
            }

            "Foo() { o.<S>foo().<String>super(); }" should parseAs {
                constructorDecl {

                    it::getModifiers shouldBe modifiers { }

                    formalsList(0) { }

                    block {
                        child<ASTExplicitConstructorInvocation> {
                            it::isThis shouldBe false
                            it::isSuper shouldBe true
                            it::isQualified shouldBe true
                            it::getArgumentCount shouldBe 0

                            it::getQualifier shouldBe child<ASTMethodCall>(ignoreChildren = true) { }

                            it::getExplicitTypeArguments shouldBe typeArgList {
                                classType("String")
                            }

                            it::getArgumentsList shouldBe argList { }
                        }
                    }
                }
            }
            "public TabbedPaneLayout() { MetalTabbedPaneUI.this.super(); }" should parseAs {
                constructorDecl {

                    it::getModifiers shouldBe modifiers {
                        it::getExplicitModifiers shouldBe setOf(JModifier.PUBLIC)
                    }

                    formalsList(0) { }

                    it::getBody shouldBe block {
                        child<ASTExplicitConstructorInvocation> {
                            it::isThis shouldBe false
                            it::isSuper shouldBe true
                            it::isQualified shouldBe true
                            it::getExplicitTypeArguments shouldBe null
                            it::getArgumentCount shouldBe 0

                            it::getQualifier shouldBe child<ASTThisExpression>(ignoreChildren = true) { }


                            it::getArgumentsList shouldBe argList { }
                        }
                    }
                }
            }
        }

        // An explicit constructor invocation statement in a constructor body may not refer to any instance
        // variables or instance methods or inner classes declared in this class or any superclass, or use
        // this or super in any expression; otherwise, a compile-time error occurs.

        // so we don't test those
    }

    parserTest("Arguments of invocations") {

        inContext(TypeBodyParsingCtx) {

            """
            WebSocketReceivePublisher() {
                super(AbstractListenerWebSocketSession.this.getLogPrefix());
            }
        """ should parseAs {
                constructorDecl {

                    it::getModifiers shouldBe modifiers { }

                    formalsList(0) { }

                    block {
                        child<ASTExplicitConstructorInvocation> {
                            it::isThis shouldBe false
                            it::isSuper shouldBe true
                            it::isQualified shouldBe false
                            it::getArgumentCount shouldBe 1

                            it::getExplicitTypeArguments shouldBe null
                            it::getQualifier shouldBe null

                            it::getArgumentsList shouldBe argList {
                                child<ASTMethodCall> {
                                    child<ASTThisExpression> {
                                        classType("AbstractListenerWebSocketSession")
                                    }
                                    it::getArguments shouldBe argList {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Neg tests, not explicit invocations") {

        inContext(TypeBodyParsingCtx) {

            "Foo() { this.name = null; }" should parseAs {
                constructorDecl {

                    it::getModifiers shouldBe modifiers { }

                    formalsList(0) { }

                    block {
                        exprStatement()
                    }
                }
            }

            "Foo() { super.name = null; }" should parseAs {
                constructorDecl {
                    it::getModifiers shouldBe modifiers { }

                    formalsList(0) { }
                    block {
                        exprStatement()
                    }
                }
            }

            "Foo() { super.foo(); }" should parseAs {
                constructorDecl {
                    it::getModifiers shouldBe modifiers { }

                    formalsList(0) { }

                    block {
                        exprStatement()
                    }
                }
            }

            "Foo() { A.super.foo(); }" should parseAs {
                constructorDecl {
                    it::getModifiers shouldBe modifiers { }

                    formalsList(0) { }

                    block {
                        exprStatement()
                    }
                }
            }
        }
    }

})
