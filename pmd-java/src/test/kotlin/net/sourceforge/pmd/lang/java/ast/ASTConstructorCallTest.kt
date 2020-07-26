package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType.READ

class ASTConstructorCallTest : ParserTestSpec({

    parserTest("Class instance creation") {

        inContext(ExpressionParsingCtx) {
            "new Foo(a)" should parseAs {
                constructorCall {
                    it::isQualifiedInstanceCreation shouldBe false

                    it::getTypeNode shouldBe classType("Foo")

                    it::getArguments shouldBe argList(1) {
                        variableAccess("a")
                    }
                }
            }

            "new <Bar> Foo<F>()" should parseAs {
                constructorCall {
                    it::isQualifiedInstanceCreation shouldBe false

                    it::getExplicitTypeArguments shouldBe child {
                        unspecifiedChild()
                    }

                    it::getTypeNode shouldBe classType("Foo") {
                        it::getTypeArguments shouldBe child {
                            unspecifiedChild()
                        }
                    }

                    it::getArguments shouldBe child {}
                }
            }

            "new @Lol Foo<F>()" should parseAs {
                constructorCall {
                    it::isQualifiedInstanceCreation shouldBe false

                    it::getExplicitTypeArguments shouldBe null

                    it::getTypeNode shouldBe classType("Foo") {

                        annotation("Lol")

                        it::getTypeArguments shouldBe child {
                            unspecifiedChild()
                        }
                    }

                    it::getArguments shouldBe argList(0) {}
                }
            }
        }
    }

    parserTest("Qualified class instance auto disambiguation") {
        /* JLS:
         *  A name is syntactically classified as an ExpressionName in these contexts:
         *       ...
         *     - As the qualifying expression in a qualified class instance creation expression (§15.9)*
         */
        // hence here it must be a field access

        inContext(ExpressionParsingCtx) {
            "a.g.c.new Foo(a)" should parseAs {
                constructorCall {
                    it::isQualifiedInstanceCreation shouldBe true

                    it::getQualifier shouldBe fieldAccess("c", READ) {
                        it::getFieldName shouldBe "c"

                        it::getQualifier shouldBe ambiguousName("a.g")
                    }

                    it::getTypeNode shouldBe classType("Foo")

                    it::getArguments shouldBe argList {

                        variableAccess("a")
                    }
                }
            }

            // and here a variable reference
            "a.new Foo(a)" should parseAs {
                constructorCall {
                    it::isQualifiedInstanceCreation shouldBe true

                    it::getQualifier shouldBe variableAccess("a")

                    it::getTypeNode shouldBe classType("Foo")

                    it::getArguments shouldBe child {

                        variableAccess("a")
                    }
                }
            }
        }
    }

    parserTest("Qualified class instance creation") {

        inContext(ExpressionParsingCtx) {
            "new O().new <Bar> Foo<F>()" should parseAs {
                constructorCall {
                    it::isQualifiedInstanceCreation shouldBe true

                    it::getQualifier shouldBe child<ASTConstructorCall> {

                        it::getTypeNode shouldBe classType("O")

                        it::getArguments shouldBe child {}
                    }

                    it::getExplicitTypeArguments shouldBe child {
                        unspecifiedChild()
                    }

                    it::getTypeNode shouldBe classType("Foo") {

                        it::getTypeArguments shouldBe typeArgList()
                    }

                    it::getArguments shouldBe argList {}
                }
            }

            "method().new @Lol Foo<F>()" should parseAs {
                constructorCall {
                    it::isQualifiedInstanceCreation shouldBe true

                    it::getQualifier shouldBe child<ASTMethodCall> {
                        it::getMethodName shouldBe "method"
                        it::getArguments shouldBe child {}
                    }

                    it::getExplicitTypeArguments shouldBe null

                    it::getTypeNode shouldBe classType("Foo") {

                        annotation("Lol")

                        it::getTypeArguments shouldBe typeArgList()
                    }

                    it::getArguments shouldBe argList {}
                }
            }
        }
    }

})
