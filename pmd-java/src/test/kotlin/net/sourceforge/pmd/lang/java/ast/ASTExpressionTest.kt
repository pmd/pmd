package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import net.sourceforge.pmd.lang.ast.test.shouldBe
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
                it.lhsExpression.shouldBeEmpty()
                it.methodName shouldBe "foo"
                it.image shouldBe "foo"

                it.arguments shouldBe child {}
            }
        }

    }


    testGroup("Method call exprs") {

        "Type.this.foo()" should matchExpr<ASTMethodCall> {
            it.methodName shouldBe "foo"
            it.image shouldBe "foo"

            it.lhsExpression shouldBePresent child<ASTThisExpression> {
                it.qualifier shouldBePresent child<ASTAmbiguousNameExpr> {
                    it.image shouldBe "Type"
                }
            }


            it.arguments shouldBe child {}

        }

        "foo().bar()" should matchExpr<ASTMethodCall> {
            it.methodName shouldBe "bar"
            it.image shouldBe "bar"

            it.lhsExpression shouldBePresent child<ASTMethodCall> {
                it.methodName shouldBe "foo"
                it.image shouldBe "foo"

                it.lhsExpression.shouldBeEmpty()

                it.arguments shouldBe child {}
            }

            it.arguments shouldBe child {}
        }

        "foo.bar.baz()" should matchExpr<ASTMethodCall> {
            it.methodName shouldBe "baz"
            it.image shouldBe "baz"

            it.lhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                it.image shouldBe "foo.bar"
            }

            it.arguments shouldBe child {}
        }

        "foo.<B>f()" should matchExpr<ASTMethodCall> {
            it.methodName shouldBe "f"
            it.image shouldBe "f"

            it.lhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                it.image shouldBe "foo"
            }

            it.explicitTypeArguments shouldBePresent child {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it.typeImage shouldBe "B"
                    }
                }
            }

            it.arguments shouldBe child {}
        }

        "foo.bar(e->it.f(e))" should matchExpr<ASTMethodCall> {

            it.methodName shouldBe "bar"
            it.image shouldBe "bar"

            it.lhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                it.image shouldBe "foo"
            }

            it.arguments shouldBe child {
                child<ASTLambdaExpression> {
                    child<ASTVariableDeclaratorId> { }

                    child<ASTMethodCall> {
                        it.methodName shouldBe "f"
                        it.image shouldBe "f"

                        it.lhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                            it.image shouldBe "it"
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

    testGroup("Ambiguous names") {

        "a.b.c" should matchExpr<ASTAmbiguousNameExpr> {
            it.image shouldBe "a.b.c"
        }

        "a" should matchExpr<ASTAmbiguousNameExpr> {
            it.image shouldBe "a"
        }
    }

    testGroup("Assignment expressions") {

        "a = 2" should matchExpr<ASTAssignmentExpression> {
            it::getOperator shouldBe AssignmentOperator.EQ
            it::isCompound shouldBe false

            it.leftHandSide shouldBe child<ASTAmbiguousNameExpr> {
                it.image shouldBe "a"
            }

            it.rightHandSide shouldBe child<ASTNumericLiteral> {}
        }

        "a *= 2" should matchExpr<ASTAssignmentExpression> {
            it::getOperator shouldBe AssignmentOperator.MUL_EQ
            it::isCompound shouldBe true

            it.leftHandSide shouldBe child<ASTAmbiguousNameExpr> {
                it.image shouldBe "a"
            }

            it.rightHandSide shouldBe child<ASTNumericLiteral> {}

        }

        "a >>>= 2" should matchExpr<ASTAssignmentExpression> {
            it::getOperator shouldBe AssignmentOperator.UNSIGNED_RIGHT_SHIFT_EQ
            it::isCompound shouldBe true


            it.leftHandSide shouldBe child<ASTAmbiguousNameExpr> {
                it.image shouldBe "a"
            }

            it.rightHandSide shouldBe child<ASTNumericLiteral> {}
        }

        "a = b = 3" should matchExpr<ASTAssignmentExpression> {
            it::getOperator shouldBe AssignmentOperator.EQ
            it::isCompound shouldBe false

            it.leftHandSide shouldBe child<ASTAmbiguousNameExpr> {
                it.image shouldBe "a"
            }

            it.rightHandSide shouldBe child<ASTAssignmentExpression> {
                it::getOperator shouldBe AssignmentOperator.EQ
                it::isCompound shouldBe false

                it.leftHandSide shouldBe child<ASTAmbiguousNameExpr> {
                    it.image shouldBe "b"
                }

                it.rightHandSide shouldBe child<ASTNumericLiteral> {}
            }
        }

    }

    testGroup("Class instance creation") {

        "new Foo(a)" should matchExpr<ASTConstructorCall> {

            it.typeNode shouldBe child {
                it.typeImage shouldBe "Foo"
            }

            it.arguments shouldBe child {

                child<ASTAmbiguousNameExpr> { }
            }
        }

        "new <Bar> Foo<F>()" should matchExpr<ASTConstructorCall> {

            it.explicitTypeArguments shouldBePresent child {
                unspecifiedChild()
            }

            it.typeNode shouldBe child {
                it.typeImage shouldBe "Foo"

                it.typeArguments shouldBePresent child {
                    unspecifiedChild()
                }
            }

            it.arguments shouldBe child {}
        }

        "new @Lol Foo<F>()" should matchExpr<ASTConstructorCall> {

            it.explicitTypeArguments.shouldBeEmpty()

            child<ASTAnnotation>(ignoreChildren = true) {}

            it.typeNode shouldBe child {
                it.typeImage shouldBe "Foo"

                it.typeArguments shouldBePresent child {
                    unspecifiedChild()
                }
            }

            it.arguments shouldBe child {}
        }
    }


    testGroup("Qualified class instance creation") {

        "a.g.c.new Foo(a)" should matchExpr<ASTConstructorCall> {

            it.lhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                it.image shouldBe "a.g.c"
            }

            it.typeNode shouldBe child {
                it.typeImage shouldBe "Foo"
            }

            it.arguments shouldBe child {

                child<ASTAmbiguousNameExpr> { }
            }
        }

        "new O().new <Bar> Foo<F>()" should matchExpr<ASTConstructorCall> {

            it.lhsExpression shouldBePresent child<ASTConstructorCall> {

                it.typeNode shouldBe child {
                    it.typeImage shouldBe "O"
                }

                it.arguments shouldBe child {}
            }

            it.explicitTypeArguments shouldBePresent child {
                unspecifiedChild()
            }

            it.typeNode shouldBe child {
                it.typeImage shouldBe "Foo"

                it.typeArguments shouldBePresent child {
                    unspecifiedChild()
                }
            }

            it.arguments shouldBe child {}
        }

        "method().new @Lol Foo<F>()" should matchExpr<ASTConstructorCall> {

            it.lhsExpression shouldBePresent child<ASTMethodCall> {
                it.methodName shouldBe "method"
                it.arguments shouldBe child {}
            }

            it.explicitTypeArguments.shouldBeEmpty()

            child<ASTAnnotation>(ignoreChildren = true) {}

            it.typeNode shouldBe child {
                it.typeImage shouldBe "Foo"

                it.typeArguments shouldBePresent child {
                    unspecifiedChild()
                }
            }

            it.arguments shouldBe child {}
        }
    }

    testGroup("Array creation") {

        "new int[2][]" should matchExpr<ASTArrayAllocation> {

            it.elementTypeNode shouldBe child<ASTPrimitiveType> {
                it.modelConstant shouldBe net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType.INT
                it.typeImage shouldBe "int"
            }

            it.arrayDims shouldBe child {
                it.arrayDepth shouldBe 2

                child<ASTNumericLiteral> {}
            }
        }

        "new @Foo int[3][2]" should matchExpr<ASTArrayAllocation> {
            child<ASTAnnotation> {
                it.annotationName shouldBe "Foo"

                child<ASTMarkerAnnotation> {
                    it.annotationName shouldBe "Foo"

                    child<ASTName> {
                        it.nameDeclaration shouldBe null
                    }
                }
            }

            it.elementTypeNode shouldBe child<ASTPrimitiveType> {
                it.isBoolean shouldBe false
                it.modelConstant shouldBe net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType.INT
                it.typeImage shouldBe "int"
            }

            it.arrayDims shouldBe child {
                it.isArray shouldBe true
                it.arrayDepth shouldBe 2

                child<ASTNumericLiteral> {
                    it.valueAsInt shouldBe 3
                }
                child<ASTNumericLiteral> {
                    it.valueAsInt shouldBe 2
                }
            }
        }

        "(new int[3])[2]" should matchExpr<ASTArrayAccess> {
            child<ASTParenthesizedExpression> {

                it.wrappedExpression shouldBe child<ASTArrayAllocation> {

                    it.elementTypeNode shouldBe child<ASTPrimitiveType> {
                        it.typeImage shouldBe "int"
                    }

                    it.arrayDims shouldBe child {
                        it.isArray shouldBe true
                        it.arrayDepth shouldBe 1

                        child<ASTNumericLiteral> {}
                    }
                }
            }
            child<ASTNumericLiteral> {}
        }

        "new Foo[0]" should matchExpr<ASTArrayAllocation> {
            it.elementTypeNode shouldBe child<ASTClassOrInterfaceType> {
                it.isAnonymousClass shouldBe false
                it.isReferenceToClassSameCompilationUnit shouldBe true
                it.typeImage shouldBe "Foo"
            }

            it.arrayDims shouldBe child {
                it.isArray shouldBe true
                it.arrayDepth shouldBe 1

                child<ASTNumericLiteral> {
                    it.valueAsInt shouldBe 0
                }
            }
        }


        "new Foo[] { f, g }" should matchExpr<ASTArrayAllocation> {

            it.elementTypeNode shouldBe child<ASTClassOrInterfaceType> {
                it.isAnonymousClass shouldBe false
                it.isReferenceToClassSameCompilationUnit shouldBe true
                it.typeImage shouldBe "Foo"
            }

            it.arrayDims shouldBe child {
                it.isArray shouldBe true
                it.arrayDepth shouldBe 1

                child<ASTArrayInitializer> {
                    child<ASTVariableInitializer> {
                        child<ASTAmbiguousNameExpr> {}
                    }
                    child<ASTVariableInitializer> {
                        child<ASTAmbiguousNameExpr> {}
                    }
                }
            }
        }

        "new int[][] { { 1 }, { 2 } }" should matchExpr<ASTArrayAllocation> {

            it.elementTypeNode shouldBe child<ASTPrimitiveType> {
                it.isBoolean shouldBe false
                it.modelConstant shouldBe net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType.INT
                it.typeImage shouldBe "int"
            }

            it.arrayDims shouldBe child {
                it.isArray shouldBe true
                it.arrayDepth shouldBe 2

                child<ASTArrayInitializer> {
                    child<ASTVariableInitializer> {
                        child<ASTArrayInitializer> {
                            child<ASTVariableInitializer> {
                                child<ASTNumericLiteral> {
                                    it.valueAsInt shouldBe 1
                                }
                            }
                        }
                    }
                    child<ASTVariableInitializer> {
                        child<ASTArrayInitializer> {
                            child<ASTVariableInitializer> {
                                child<ASTNumericLiteral> {
                                    it.valueAsInt shouldBe 2
                                }
                            }
                        }
                    }
                }
            }
        }

        "new int[][] { { 1 , 2 }, null }" should matchExpr<ASTArrayAllocation> {

            it.elementTypeNode shouldBe child<ASTPrimitiveType> {
                it.typeImage shouldBe "int"
            }

            it.arrayDims shouldBe child {
                it.isArray shouldBe true
                it.arrayDepth shouldBe 2

                child<ASTArrayInitializer> {
                    child<ASTVariableInitializer> {
                        child<ASTArrayInitializer> {
                            child<ASTVariableInitializer> {
                                child<ASTNumericLiteral> {
                                    it.valueAsInt shouldBe 1
                                }
                            }
                            child<ASTVariableInitializer> {
                                child<ASTNumericLiteral> {
                                    it.valueAsInt shouldBe 2
                                }
                            }
                        }
                    }
                    child<ASTVariableInitializer> {
                        child<ASTNullLiteral> {}
                    }
                }
            }
        }
    }
})