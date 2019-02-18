package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTMethodCallTest : ParserTestSpec({


    parserTest("Method call exprs") {

        "Type.this.foo()" should matchExpr<ASTMethodCall> {
            it::getMethodName shouldBe "foo"
            it::getImage shouldBe "foo"

            it::getLhsExpression shouldBePresent child<ASTThisExpression>(ignoreChildren = true) {}

            it::getArguments shouldBe child {}

        }

        "foo().bar()" should matchExpr<ASTMethodCall> {
            it::getMethodName shouldBe "bar"
            it::getImage shouldBe "bar"

            it::getLhsExpression shouldBePresent child<ASTMethodCall> {
                it::getMethodName shouldBe "foo"
                it::getImage shouldBe "foo"

                it::getLhsExpression.shouldBeEmpty()

                it::getArguments shouldBe child {}
            }

            it::getArguments shouldBe child {}
        }

        "foo.bar.baz()" should matchExpr<ASTMethodCall> {
            it::getMethodName shouldBe "baz"
            it::getImage shouldBe "baz"

            it::getLhsExpression shouldBePresent child<ASTAmbiguousName> {
                it::getImage shouldBe "foo.bar"
            }

            it::getArguments shouldBe child {}
        }

        "foo.<B>f()" should matchExpr<ASTMethodCall> {
            it::getMethodName shouldBe "f"
            it::getImage shouldBe "f"

            it::getLhsExpression shouldBePresent child<ASTAmbiguousName> {
                it::getImage shouldBe "foo"
            }

            it::getExplicitTypeArguments shouldBePresent child {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it::getTypeImage shouldBe "B"
                    }
                }
            }

            it::getArguments shouldBe child {}
        }

        "foo.bar(e->it.f(e))" should matchExpr<ASTMethodCall> {

            it::getMethodName shouldBe "bar"
            it::getImage shouldBe "bar"

            it::getLhsExpression shouldBePresent child<ASTAmbiguousName> {
                it::getImage shouldBe "foo"
            }

            it::getArguments shouldBe child {
                child<ASTLambdaExpression>(ignoreChildren = true) {}
            }
        }
    }

})