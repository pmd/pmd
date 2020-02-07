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

            it::getQualifier shouldBe thisExpr { classType("Type") }

            it::getArguments shouldBe argList {}

        }

        "foo().bar()" should matchExpr<ASTMethodCall> {
            it::getMethodName shouldBe "bar"
            it::getImage shouldBe "bar"

            it::getQualifier shouldBe child<ASTMethodCall> {
                it::getMethodName shouldBe "foo"
                it::getImage shouldBe "foo"

                it::getQualifier shouldBe null

                it::getArguments shouldBe argList {}
            }

            it::getArguments shouldBe argList {}
        }

        "foo.bar.baz()" should matchExpr<ASTMethodCall> {
            it::getMethodName shouldBe "baz"
            it::getImage shouldBe "baz"

            it::getQualifier shouldBe child<ASTAmbiguousName> {
                it::getImage shouldBe "foo.bar"
            }

            it::getArguments shouldBe argList {}
        }

        "foo.<B>f()" should matchExpr<ASTMethodCall> {
            it::getMethodName shouldBe "f"
            it::getImage shouldBe "f"

            it::getQualifier shouldBe ambiguousName("foo")

            it::getExplicitTypeArguments shouldBe typeArgList {
                classType("B")
            }

            it::getArguments shouldBe argList {  }
        }

        "foo.bar(e->it.f(e))" should matchExpr<ASTMethodCall> {

            it::getMethodName shouldBe "bar"
            it::getImage shouldBe "bar"

            it::getQualifier shouldBe ambiguousName("foo")

            it::getArguments shouldBe argList {
                child<ASTLambdaExpression>(ignoreChildren = true) {}
            }
        }

        "foo.bar(foo::bar).foreach(System.out::println)" should matchExpr<ASTMethodCall> {

            it::getMethodName shouldBe "foreach"

            it::getQualifier shouldBe methodCall("bar") {

                it::getQualifier shouldBe ambiguousName("foo")

                it::getArguments shouldBe argList {
                    methodRef("bar") {
                        ambiguousName("foo")
                    }
                }
            }

            it::getArguments shouldBe argList {
                methodRef("println") {
                    ambiguousName("System.out")
                }
            }
        }
    }

})
