package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTExpressionTest : FunSpec({

    testGroup("this keyword") {

        "this" should matchExpr<ASTThisExpression> { }

        "Type.this" should matchExpr<ASTThisExpression> {

            it.qualifier shouldBePresent child {
                it.image shouldBe "Type"
            }
        }

    }

    testGroup("Field access exprs") {

        "Type.this.foo" should matchExpr<ASTFieldAccess> {
            it.fieldName shouldBe "foo"
            it.image shouldBe "foo"

            it.leftHandSide shouldBePresent child<ASTThisExpression> {
                it.qualifier shouldBePresent child<ASTAmbiguousNameExpr> { }
            }
        }

        "foo().foo" should matchExpr<ASTFieldAccess> {

            it.fieldName shouldBe "foo"
            it.image shouldBe "foo"

            it.leftHandSide shouldBePresent child<ASTMethodCall> {
                it.leftHandSide.shouldBeEmpty()
                it.methodName shouldBe "foo"

                it.nameNode shouldBe child {}
                it.arguments shouldBe child {}
            }
        }

    }


    testGroup("Method call exprs") {

        "Type.this.foo()" should matchExpr<ASTMethodCall> {
            it.methodName shouldBe "foo"

            it.leftHandSide shouldBePresent child<ASTThisExpression> {
                it.qualifier shouldBePresent child<ASTAmbiguousNameExpr> {
                    it.image shouldBe "Type"
                }
            }

            it.nameNode shouldBe child {
                (it is ASTAmbiguousNameExpr) shouldBe false
            }

            it.arguments shouldBe child {}

        }

        "foo().bar()" should matchExpr<ASTMethodCall> {
            it.methodName shouldBe "bar"

            it.leftHandSide shouldBePresent child<ASTMethodCall> {
                it.methodName shouldBe "foo"

                it.leftHandSide.shouldBeEmpty()

                it.nameNode shouldBe child {}
                it.arguments shouldBe child {}
            }

            it.nameNode shouldBe child {
                (it is ASTAmbiguousNameExpr) shouldBe false
            }

            it.arguments shouldBe child {}
        }

        "foo.bar.baz()" should matchExpr<ASTMethodCall> {
            it.methodName shouldBe "baz"

            it.leftHandSide shouldBePresent child<ASTAmbiguousNameExpr> {
                it.image shouldBe "foo.bar"
            }

            it.nameNode shouldBe child {
                (it is ASTAmbiguousNameExpr) shouldBe false
            }

            it.arguments shouldBe child {}
        }

        "foo.<B>f()" should matchExpr<ASTMethodCall> {
            it.methodName shouldBe "f"

            it.leftHandSide shouldBePresent child<ASTAmbiguousNameExpr> {
                it.image shouldBe "foo"
            }

            it.explicitTypeArguments shouldBePresent child {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it.typeImage shouldBe "B"
                    }
                }
            }

            it.nameNode shouldBe child {
                (it is ASTAmbiguousNameExpr) shouldBe false
            }

            it.arguments shouldBe child {}
        }

        "foo.bar(e->it.f(e))" should matchExpr<ASTMethodCall> {

            it.methodName shouldBe "bar"

            it.leftHandSide shouldBePresent child<ASTAmbiguousNameExpr> {
                it.image shouldBe "foo"
            }

            it.nameNode shouldBe child {
                (it is ASTAmbiguousNameExpr) shouldBe false

            }

            it.arguments shouldBe child {
                child<ASTLambdaExpression> {
                    child<ASTVariableDeclaratorId> {  }

                    child<ASTMethodCall> {
                        it.methodName shouldBe "f"

                        it.leftHandSide shouldBePresent child<ASTAmbiguousNameExpr> {
                            it.image shouldBe "it"
                        }

                        it.nameNode shouldBe child {
                            (it is ASTAmbiguousNameExpr) shouldBe false
                            it.xPathNodeName shouldBe "Name"
                        }

                        it.arguments shouldBe child {

                            child<ASTAmbiguousNameExpr> {
                                it.image shouldBe "e"
                            }
                        }
                    }
                }
            }
        }
    }

    testGroup("Method reference") {

        "this::foo" should matchExpr<ASTMethodReference> {

            it.image shouldBe "foo"
            it.methodName shouldBePresent "foo"
            it.lhsType.shouldBeEmpty()
            it.isConstructorReference shouldBe false
            it.typeArguments.shouldBeEmpty()

            it.lhsExpression shouldBePresent child<ASTThisExpression> {

            }
        }

        "foobar.b::foo" should matchExpr<ASTMethodReference> {

            it.image shouldBe "foo"
            it.methodName shouldBePresent "foo"
            it.lhsType.shouldBeEmpty()
            it.isConstructorReference shouldBe false
            it.typeArguments.shouldBeEmpty()

            it.lhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                it.image shouldBe "foobar.b"
            }
        }

        "foobar.b::<B>foo" should matchExpr<ASTMethodReference> {

            it.image shouldBe "foo"
            it.methodName shouldBePresent "foo"
            it.lhsType.shouldBeEmpty()
            it.isConstructorReference shouldBe false

            it.lhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                it.image shouldBe "foobar.b"
            }

            it.typeArguments shouldBePresent child {
                unspecifiedChild()
            }

        }


        "foobar.b<B>::foo" should matchExpr<ASTMethodReference> {

            it.image shouldBe "foo"
            it.methodName shouldBePresent "foo"
            it.isConstructorReference shouldBe false
            it.lhsExpression.shouldBeEmpty()
            it.typeArguments.shouldBeEmpty()

            it.lhsType shouldBePresent child<ASTClassOrInterfaceType> {

                it.typeArguments shouldBePresent child {
                    child<ASTTypeArgument> {
                        child<ASTClassOrInterfaceType> {
                            it.typeImage shouldBe "B"
                        }
                    }
                }
            }
        }
    }

    testGroup("Constructor reference") {

        "foobar.b::new" should matchExpr<ASTMethodReference> {

            it.image shouldBe "new"
            it.methodName.shouldBeEmpty()
            it.isConstructorReference shouldBe true
            it.typeArguments.shouldBeEmpty()

            it.lhsExpression.shouldBeEmpty()
            it.lhsType shouldBePresent child<ASTClassOrInterfaceType> {
                it.typeImage shouldBe "foobar.b"
            }

        }


        "foobar.b<B>::new" should matchExpr<ASTMethodReference> {

            it.image shouldBe "new"
            it.methodName.shouldBeEmpty()
            it.isConstructorReference shouldBe true
            it.typeArguments.shouldBeEmpty()

            it.lhsExpression.shouldBeEmpty()
            it.lhsType shouldBePresent child<ASTClassOrInterfaceType> {
                it.typeImage shouldBe "foobar.b"

                it.typeArguments shouldBePresent child {
                    child<ASTTypeArgument> {
                        child<ASTClassOrInterfaceType> {
                            it.typeImage shouldBe "B"
                        }
                    }
                }
            }
        }

        "int[]::new" should matchExpr<ASTMethodReference> {

            it.image shouldBe "new"
            it.methodName.shouldBeEmpty()
            it.isConstructorReference shouldBe true
            it.typeArguments.shouldBeEmpty()

            it.lhsExpression.shouldBeEmpty()
            it.lhsType shouldBePresent child<ASTArrayType> {
                it.typeImage shouldBe "int"

                it.elementType shouldBe child<ASTPrimitiveType> {
                    it.typeImage shouldBe "int"
                }

                it.dimensions shouldBe child {
                    child<ASTArrayTypeDim> {}
                }
            }
        }

        "ArrayList<String>::new" should matchExpr<ASTMethodReference> {

            it.image shouldBe "new"
            it.methodName.shouldBeEmpty()
            it.isConstructorReference shouldBe true
            it.typeArguments.shouldBeEmpty()

            it.lhsExpression.shouldBeEmpty()
            it.lhsType shouldBePresent child<ASTClassOrInterfaceType> {
                it.typeImage shouldBe "ArrayList"

                it.typeArguments shouldBePresent child {
                    child<ASTTypeArgument> {
                        child<ASTClassOrInterfaceType> {
                            it.typeImage shouldBe "String"
                        }
                    }
                }
            }
        }

        "ArrayList::<String>new" should matchExpr<ASTMethodReference> {

            it.image shouldBe "new"
            it.methodName.shouldBeEmpty()
            it.isConstructorReference shouldBe true

            it.lhsExpression.shouldBeEmpty()
            it.lhsType shouldBePresent child<ASTClassOrInterfaceType> {
                it.typeImage shouldBe "ArrayList"
                it.typeArguments.shouldBeEmpty()
            }

            it.typeArguments shouldBePresent child {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it.typeImage shouldBe "String"
                    }
                }
            }
        }
    }

    testGroup("Test ambiguous names") {

        "a.b.c" should matchExpr<ASTAmbiguousNameExpr> {
            it.image shouldBe "a.b.c"
        }

        "java.util.List<F>" should matchType<ASTClassOrInterfaceType> {

            it.leftHandSide.shouldBeEmpty()

            it.typeArguments shouldBePresent child {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it.typeImage shouldBe "F"
                    }
                }
            }
        }
    }

})