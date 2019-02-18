package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * Nodes that previously corresponded to ASTAllocationExpression.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTMethodReferenceTest : ParserTestSpec({

    parserTest("Method reference") {

        "this::foo" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "foo"
            it::getMethodName shouldBePresent "foo"
            it::getLhsType.shouldBeEmpty()
            it::isConstructorReference shouldBe false
            it::getTypeArguments.shouldBeEmpty()

            it::getLhsExpression shouldBePresent child<ASTThisExpression> {

            }
        }

        "foobar.b::foo" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "foo"
            it::getMethodName shouldBePresent "foo"
            it::getLhsType.shouldBeEmpty()
            it::isConstructorReference shouldBe false
            it::getTypeArguments.shouldBeEmpty()

            it::getLhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                it::getImage shouldBe "foobar.b"
            }
        }

        "foobar.b::<B>foo" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "foo"
            it::getMethodName shouldBePresent "foo"
            it::getLhsType.shouldBeEmpty()
            it::isConstructorReference shouldBe false

            it::getLhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                it::getImage shouldBe "foobar.b"
            }

            it::getTypeArguments shouldBePresent child {
                unspecifiedChild()
            }

        }


        "foobar.b<B>::foo" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "foo"
            it::getMethodName shouldBePresent "foo"
            it::isConstructorReference shouldBe false
            it::getLhsExpression.shouldBeEmpty()
            it::getTypeArguments.shouldBeEmpty()

            it::getLhsType shouldBePresent child<ASTClassOrInterfaceType> {

                it::getTypeArguments shouldBePresent child {
                    child<ASTTypeArgument> {
                        child<ASTClassOrInterfaceType> {
                            it::getTypeImage shouldBe "B"
                        }
                    }
                }
            }
        }
    }

    parserTest("Constructor reference") {

        "foobar.b::new" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "new"
            it::getMethodName.shouldBeEmpty()
            it::isConstructorReference shouldBe true
            it::getTypeArguments.shouldBeEmpty()

            it::getLhsExpression.shouldBeEmpty()
            it::getLhsType shouldBePresent child<ASTClassOrInterfaceType> {
                it::getTypeImage shouldBe "foobar.b"
            }

        }


        "foobar.b<B>::new" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "new"
            it::getMethodName.shouldBeEmpty()
            it::isConstructorReference shouldBe true
            it::getTypeArguments.shouldBeEmpty()

            it::getLhsExpression.shouldBeEmpty()
            it::getLhsType shouldBePresent child<ASTClassOrInterfaceType> {
                it::getTypeImage shouldBe "foobar.b"

                it::getTypeArguments shouldBePresent child {
                    child<ASTTypeArgument> {
                        child<ASTClassOrInterfaceType> {
                            it::getTypeImage shouldBe "B"
                        }
                    }
                }
            }
        }

        "int[]::new" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "new"
            it::getMethodName.shouldBeEmpty()
            it::isConstructorReference shouldBe true
            it::getTypeArguments.shouldBeEmpty()

            it::getLhsExpression.shouldBeEmpty()
            it::getLhsType shouldBePresent child<ASTArrayType> {
                it::getTypeImage shouldBe "int"

                it::getElementType shouldBe child<ASTPrimitiveType> {
                    it::getTypeImage shouldBe "int"
                }

                it::getDimensions shouldBe child {
                    child<ASTArrayTypeDim> {}
                }
            }
        }

        "ArrayList<String>::new" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "new"
            it::getMethodName.shouldBeEmpty()
            it::isConstructorReference shouldBe true
            it::getTypeArguments.shouldBeEmpty()

            it::getLhsExpression.shouldBeEmpty()
            it::getLhsType shouldBePresent child<ASTClassOrInterfaceType> {
                it::getTypeImage shouldBe "ArrayList"

                it::getTypeArguments shouldBePresent child {
                    child<ASTTypeArgument> {
                        child<ASTClassOrInterfaceType> {
                            it::getTypeImage shouldBe "String"
                        }
                    }
                }
            }
        }

        "ArrayList::<String>new" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "new"
            it::getMethodName.shouldBeEmpty()
            it::isConstructorReference shouldBe true

            it::getLhsExpression.shouldBeEmpty()
            it::getLhsType shouldBePresent child<ASTClassOrInterfaceType> {
                it::getTypeImage shouldBe "ArrayList"
                it::getTypeArguments.shouldBeEmpty()
            }

            it::getTypeArguments shouldBePresent child {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it::getTypeImage shouldBe "String"
                    }
                }
            }
        }
    }
})