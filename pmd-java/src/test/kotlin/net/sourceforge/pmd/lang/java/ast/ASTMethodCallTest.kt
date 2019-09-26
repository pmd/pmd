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

            it::getLhs shouldBe thisExpr { classType("Type") }

            it::getArguments shouldBe child {}

        }

        "foo().bar()" should matchExpr<ASTMethodCall> {
            it::getMethodName shouldBe "bar"
            it::getImage shouldBe "bar"

            it::getLhs shouldBe child<ASTMethodCall> {
                it::getMethodName shouldBe "foo"
                it::getImage shouldBe "foo"

                it::getLhs shouldBe null

                it::getArguments shouldBe child {}
            }

            it::getArguments shouldBe child {}
        }

        "foo.bar.baz()" should matchExpr<ASTMethodCall> {
            it::getMethodName shouldBe "baz"
            it::getImage shouldBe "baz"

            it::getLhs shouldBe child<ASTAmbiguousName> {
                it::getImage shouldBe "foo.bar"
            }

            it::getArguments shouldBe child {}
        }

        "foo.<B>f()" should matchExpr<ASTMethodCall> {
            it::getMethodName shouldBe "f"
            it::getImage shouldBe "f"

            it::getLhs shouldBe ambiguousName("foo")

            it::getExplicitTypeArguments shouldBe typeArgList {
                classType("B")
            }

            it::getArguments shouldBe child {}
        }

        "foo.bar(e->it.f(e))" should matchExpr<ASTMethodCall> {

            it::getMethodName shouldBe "bar"
            it::getImage shouldBe "bar"

            it::getLhs shouldBe ambiguousName("foo")

            it::getArguments shouldBe child {
                child<ASTLambdaExpression>(ignoreChildren = true) {}
            }
        }

        "foo.bar(foo::bar).foreach(System.out::println)" should matchExpr<ASTMethodCall> {

            it::getMethodName shouldBe "foreach"

            it::getLhs shouldBe child<ASTMethodCall> {

                it::getMethodName shouldBe "bar"
                it::getImage shouldBe "bar"

                it::getLhs shouldBe ambiguousName("foo")

                it::getArguments shouldBe child {
                    methodRef("bar")
                }
            }

            it::getArguments shouldBe child {
                methodRef("println")
            }
        }
    }

})
