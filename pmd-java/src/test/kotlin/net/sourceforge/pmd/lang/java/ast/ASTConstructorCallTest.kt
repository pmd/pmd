package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

class ASTConstructorCallTest : ParserTestSpec({

    parserTest("Class instance creation") {

        "new Foo(a)" should matchExpr<ASTConstructorCall> {

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"
            }

            it::getArguments shouldBe child {

                child<ASTVariableReference> { }
            }
        }

        "new <Bar> Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getExplicitTypeArguments shouldBePresent child {
                unspecifiedChild()
            }

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                it::getTypeArguments shouldBePresent child {
                    unspecifiedChild()
                }
            }

            it::getArguments shouldBe child {}
        }

        "new @Lol Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getExplicitTypeArguments.shouldBeEmpty()

            child<ASTAnnotation>(ignoreChildren = true) {}

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                it::getTypeArguments shouldBePresent child {
                    unspecifiedChild()
                }
            }

            it::getArguments shouldBe child {}
        }
    }


    parserTest("Qualified class instance creation") {

        "a.g.c.new Foo(a)" should matchExpr<ASTConstructorCall> {

            it::getLhsExpression shouldBePresent child<ASTAmbiguousName> { // TODO should be a field access
                it::getImage shouldBe "a.g.c"
            }

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"
            }

            it::getArguments shouldBe child {

                child<ASTVariableReference> { }
            }
        }

        "new O().new <Bar> Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getLhsExpression shouldBePresent child<ASTConstructorCall> {

                it::getTypeNode shouldBe child {
                    it::getTypeImage shouldBe "O"
                }

                it::getArguments shouldBe child {}
            }

            it::getExplicitTypeArguments shouldBePresent child {
                unspecifiedChild()
            }

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                it::getTypeArguments shouldBePresent child {
                    unspecifiedChild()
                }
            }

            it::getArguments shouldBe child {}
        }

        "method().new @Lol Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getLhsExpression shouldBePresent child<ASTMethodCall> {
                it::getMethodName shouldBe "method"
                it::getArguments shouldBe child {}
            }

            it::getExplicitTypeArguments.shouldBeEmpty()

            child<ASTAnnotation>(ignoreChildren = true) {}

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                it::getTypeArguments shouldBePresent child {
                    unspecifiedChild()
                }
            }

            it::getArguments shouldBe child {}
        }
    }

})