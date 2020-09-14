/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTMethodCallTest : ParserTestSpec({


    parserTest("Method call exprs") {

        inContext(ExpressionParsingCtx) {

            "Type.this.foo()" should parseAs {
                methodCall("foo") {

                    it::getQualifier shouldBe thisExpr { classType("Type") }

                    it::getArguments shouldBe argList {}
                }
            }

            "foo().bar()" should parseAs {
                methodCall("bar") {

                    it::getQualifier shouldBe child<ASTMethodCall> {
                        it::getMethodName shouldBe "foo"

                        it::getQualifier shouldBe null

                        it::getArguments shouldBe argList {}
                    }

                    it::getArguments shouldBe argList {}
                }
            }

            "foo.bar.baz()" should parseAs {
                methodCall("baz") {

                    it::getQualifier shouldBe child<ASTAmbiguousName> {
                        it::getName shouldBe "foo.bar"
                    }

                    it::getArguments shouldBe argList {}
                }
            }
            "foo.<B>f()" should parseAs {
                methodCall("f") {

                    it::getQualifier shouldBe ambiguousName("foo")

                    it::getExplicitTypeArguments shouldBe typeArgList {
                        classType("B")
                    }

                    it::getArguments shouldBe argList { }
                }
            }

            "foo.bar(e->it.f(e))" should parseAs {
                methodCall("bar") {

                    it::getQualifier shouldBe ambiguousName("foo")

                    it::getArguments shouldBe argList {
                        child<ASTLambdaExpression>(ignoreChildren = true) {}
                    }
                }
            }

            "foo.bar(foo::bar).foreach(System.out::println)" should parseAs {

                methodCall("foreach") {

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
        }
    }


})
