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
            it::getMethodName shouldBe "foo"
            it::getLhsType shouldBe null
            it::isConstructorReference shouldBe false
            it::getTypeArguments shouldBe null

            it::getLhsExpression shouldBe child<ASTThisExpression> {

            }
        }

        "foobar.b::foo" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "foo"
            it::getMethodName shouldBe "foo"
            it::isConstructorReference shouldBe false
            it::getTypeArguments shouldBe null

            val lhs = child<ASTAmbiguousName> {
                it::getImage shouldBe "foobar.b"
            }

            it::getLhsExpression shouldBe lhs
            it::getLhsType shouldBe lhs
        }

        "foobar.b::<B>foo" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "foo"
            it::getMethodName shouldBe "foo"
            it::isConstructorReference shouldBe false

            val lhs = child<ASTAmbiguousName> {
                it::getImage shouldBe "foobar.b"
            }

            it::getLhsExpression shouldBe lhs
            it::getLhsType shouldBe lhs

            it::getTypeArguments shouldBe child {
                unspecifiedChild()
            }

        }


        "foobar.b<B>::foo" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "foo"
            it::getMethodName shouldBe "foo"
            it::isConstructorReference shouldBe false
            it::getLhsExpression shouldBe null
            it::getTypeArguments shouldBe null

            it::getLhsType shouldBe child<ASTClassOrInterfaceType> {

                it::getImage shouldBe "b"

                it::getAmbiguousLhs shouldBe child {
                    it::getName shouldBe "foobar"
                }

                it::getTypeArguments shouldBe child {
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
            it::getMethodName shouldBe null
            it::isConstructorReference shouldBe true
            it::getTypeArguments shouldBe null

            it::getLhsExpression shouldBe null
            it::getLhsType shouldBe child<ASTClassOrInterfaceType> {
                it::getImage shouldBe "b"
                it::getTypeImage shouldBe "foobar.b"

                it::getAmbiguousLhs shouldBe child<ASTAmbiguousName> {
                    it::getName shouldBe "foobar"
                }
            }

        }


        "foobar.b<B>::new" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "new"
            it::getMethodName shouldBe null
            it::isConstructorReference shouldBe true
            it::getTypeArguments shouldBe null

            it::getLhsExpression shouldBe null
            it::getLhsType shouldBe child<ASTClassOrInterfaceType> {
                it::getTypeImage shouldBe "foobar.b"
                it::getImage shouldBe "b"

                it::getAmbiguousLhs shouldBe child {
                    it::getName shouldBe "foobar"
                    it::getTypeImage shouldBe "foobar"
                }


                it::getTypeArguments shouldBe child {
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
            it::getMethodName shouldBe null
            it::isConstructorReference shouldBe true
            it::getTypeArguments shouldBe null

            it::getLhsExpression shouldBe null
            it::getLhsType shouldBe child<ASTArrayType> {
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
            it::getMethodName shouldBe null
            it::isConstructorReference shouldBe true
            it::getTypeArguments shouldBe null

            it::getLhsExpression shouldBe null
            it::getLhsType shouldBe child<ASTClassOrInterfaceType> {
                it::getTypeImage shouldBe "ArrayList"

                it::getTypeArguments shouldBe child {
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
            it::getMethodName shouldBe null
            it::isConstructorReference shouldBe true

            it::getLhsExpression shouldBe null
            it::getLhsType shouldBe child<ASTClassOrInterfaceType> {
                it::getTypeImage shouldBe "ArrayList"
                it::getTypeArguments shouldBe null
            }

            it::getTypeArguments shouldBe child {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it::getTypeImage shouldBe "String"
                    }
                }
            }
        }
    }
})