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
            it::isConstructorReference shouldBe false
            it::getTypeArguments.shouldBeEmpty()

            val lhs = child<ASTAmbiguousName> {
                it::getImage shouldBe "foobar.b"
            }

            it::getLhsExpression shouldBePresent lhs
            it::getLhsType shouldBePresent lhs
        }

        "foobar.b::<B>foo" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "foo"
            it::getMethodName shouldBePresent "foo"
            it::isConstructorReference shouldBe false

            val lhs = child<ASTAmbiguousName> {
                it::getImage shouldBe "foobar.b"
            }

            it::getLhsExpression shouldBePresent lhs
            it::getLhsType shouldBePresent lhs

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

                it::getImage shouldBe "b"

                it::getAmbiguousLhs shouldBePresent child {
                    it::getName shouldBe "foobar"
                }

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
                it::getImage shouldBe "b"
                it::getTypeImage shouldBe "foobar.b"

                it::getAmbiguousLhs shouldBePresent child<ASTAmbiguousName> {
                    it::getName shouldBe "foobar"
                }
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
                it::getImage shouldBe "b"

                it::getAmbiguousLhs shouldBePresent child {
                    it::getName shouldBe "foobar"
                    it::getTypeImage shouldBe "foobar"
                }


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