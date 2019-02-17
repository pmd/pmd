package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTExpressionTest : FunSpec({

    testGroup("Test this expression ") {

        "this" should matchExpr<ASTThisExpression> { }

        "Type.this" should matchExpr<ASTThisExpression> {

            it.qualifier shouldBePresent child {
                it.image shouldBe "Type"
            }
        }

    }

    testGroup("Test field access exprs") {

        "Type.this.foo" should matchExpr<ASTFieldAccess> {
            it.fieldName shouldBe "foo"

            it.leftHandSide shouldBePresent child<ASTThisExpression> {
                it.qualifier shouldBePresent child<ASTAmbiguousNameExpr> { }
            }

            it.nameNode shouldBe child {
                (it is ASTAmbiguousNameExpr) shouldBe false
            }
        }

        "foo().foo" should matchExpr<ASTFieldAccess> {

            it.fieldName shouldBe "foo"

            it.leftHandSide shouldBePresent child<ASTMethodCall> {
                it.leftHandSide.shouldBeEmpty()
                it.methodName shouldBe "foo"

                it.nameNode shouldBe child {}
                it.arguments shouldBe child {}
            }

            it.nameNode shouldBe child {
                (it is ASTAmbiguousNameExpr) shouldBe false
            }
        }

    }


    testGroup("Test method call exprs") {

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