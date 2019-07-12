package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType.INT
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx

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
            it::getLhsExpression shouldBe null
            it::getLhsType shouldBe null

            it::getAmbiguousLhs shouldBe ambiguousName("foobar.b")

        }

        "foobar.b::<B>foo" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "foo"
            it::getMethodName shouldBe "foo"
            it::isConstructorReference shouldBe false
            it::getLhsExpression shouldBe null
            it::getLhsType shouldBe null

            it::getAmbiguousLhs shouldBe ambiguousName("foobar.b")

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
                    child<ASTClassOrInterfaceType> {
                        it::getTypeImage shouldBe "B"
                    }
                }
            }
        }

        "java.util.Map<String, String>.Entry<String, String>::foo" should matchExpr<ASTMethodReference> {

            it::getMethodName shouldBe "foo"
            it::getLhsType shouldBe classType("Entry") // ignore the rest
        }

        inContext(ExpressionParsingCtx) {

            "super::foo" should parseAs {
                methodRef("foo") {
                    it::getLhsType shouldBe null
                    it::getLhsExpression shouldBe child<ASTSuperExpression> {

                    }
                }
            }

            "T.B.super::foo" should parseAs {
                methodRef("foo") {
                    it::getLhsType shouldBe null
                    it::getLhsExpression shouldBe child<ASTSuperExpression> {
                        it::getQualifier shouldBe classType("B") {
                            ambiguousName("T")
                        }
                    }
                }
            }

        }

    }

    parserTest("Neg tests") {

        inContext(ExpressionParsingCtx) {

            "foo::bar::bar" shouldNot parse()
            "foo::bar.foo()" shouldNot parse()
            "foo::bar.foo" shouldNot parse()

        }

    }

    parserTest("Constructor reference") {

        inContext(ExpressionParsingCtx) {

            "foobar.b::new" should parseAs {
                constructorRef {
                    it::getTypeArguments shouldBe null

                    classType("b") {
                        it::getAmbiguousLhs shouldBe ambiguousName("foobar")
                    }
                }
            }

            "foobar.b<B>::new" should parseAs {
                constructorRef {
                    it::getTypeArguments shouldBe null

                    classType("b") {
                        it::getAmbiguousLhs shouldBe ambiguousName("foobar")
                        it::getTypeArguments shouldBe typeArgList {
                            classType("B")
                        }
                    }
                }
            }

            "int[]::new" should parseAs {
                constructorRef {
                    it::getTypeArguments shouldBe null

                    arrayType {
                        primitiveType(INT)
                        it::getDimensions shouldBe child {
                            arrayDim()
                        }
                    }
                }
            }

            "Class<?>[]::new" should parseAs {
                constructorRef {
                    it::getTypeArguments shouldBe null

                    arrayType {
                        classType("Class") {
                            typeArgList {
                                child<ASTWildcardType> { }
                            }
                        }
                        it::getDimensions shouldBe child {
                            arrayDim()
                        }
                    }
                }
            }

            "ArrayList::<String>new" should parseAs {
                constructorRef {
                    val lhs = classType("ArrayList")

                    it::getTypeArguments shouldBe typeArgList {
                        classType("String")
                    }

                    lhs
                }
            }
        }
    }

    parserTest("Type annotations") {

        inContext(ExpressionParsingCtx) {

            "@Vernal Date::getDay" should parseAs {
                methodRef(methodName = "getDay") {
                    classType("Date") {
                        annotation("Vernal")
                    }

                    it::getTypeArguments shouldBe null
                }
            }

            // annotated method ref in cast ctx (lookahead trickery)
            "(Foo) @Vernal Date::getDay" should parseAs {
                castExpr {
                    classType("Foo")

                    methodRef(methodName = "getDay") {
                        classType("Date") {
                            annotation("Vernal")
                        }

                        it::getTypeArguments shouldBe null
                    }
                }
            }
        }
    }
})
