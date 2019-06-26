package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

class ASTConstructorCallTest : ParserTestSpec({

    parserTest("Class instance creation") {

        "new Foo(a)" should matchExpr<ASTConstructorCall> {

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"
            }

            it::getArguments shouldBe child {

                child<ASTVariableAccess> { }
            }
        }

        "new <Bar> Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getExplicitTypeArguments shouldBe child {
                unspecifiedChild()
            }

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                it::getTypeArguments shouldBe child {
                    unspecifiedChild()
                }
            }

            it::getArguments shouldBe child {}
        }

        "new @Lol Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getExplicitTypeArguments shouldBe null

            annotation {
                it::getAnnotationName shouldBe "Lol"
            }

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                it::getTypeArguments shouldBe child {
                    unspecifiedChild()
                }
            }

            it::getArguments shouldBe child {}
        }
    }

    parserTest("Qualified class instance auto disambiguation") {
        /* JLS:
         *  A name is syntactically classified as an ExpressionName in these contexts:
         *       ...
         *     - As the qualifying expression in a qualified class instance creation expression (§15.9)*
         */
        // hence here it must be a field access

        "a.g.c.new Foo(a)" should matchExpr<ASTConstructorCall> {

            it::getLhsExpression shouldBe child<ASTFieldAccess> {
                it::getFieldName shouldBe "c"

                it::getLhsExpression shouldBe child<ASTAmbiguousName> {
                    it::getName shouldBe "a.g"
                }
            }

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"
            }

            it::getArguments shouldBe child {

                child<ASTVariableAccess> { }
            }
        }

        // and here a variable reference
        "a.new Foo(a)" should matchExpr<ASTConstructorCall> {

            it::getLhsExpression shouldBe child<ASTVariableAccess> {
                it::getVariableName shouldBe "a"
            }

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"
            }

            it::getArguments shouldBe child {

                child<ASTVariableAccess> { }
            }
        }
    }


    parserTest("Qualified class instance creation") {

        "new O().new <Bar> Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getLhsExpression shouldBe child<ASTConstructorCall> {

                it::getTypeNode shouldBe child {
                    it::getTypeImage shouldBe "O"
                }

                it::getArguments shouldBe child {}
            }

            it::getExplicitTypeArguments shouldBe child {
                unspecifiedChild()
            }

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                it::getTypeArguments shouldBe child {
                    unspecifiedChild()
                }
            }

            it::getArguments shouldBe child {}
        }

        "method().new @Lol Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getLhsExpression shouldBe child<ASTMethodCall> {
                it::getMethodName shouldBe "method"
                it::getArguments shouldBe child {}
            }

            it::getExplicitTypeArguments shouldBe null

            annotation {
                it::getAnnotationName shouldBe "Lol"
            }

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                it::getTypeArguments shouldBe child {
                    unspecifiedChild()
                }
            }

            it::getArguments shouldBe child {}
        }
    }

})
