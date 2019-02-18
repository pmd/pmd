package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTExpressionTest : ParserTestSpec({

    parserTest("this keyword") {

        "this" should matchExpr<ASTThisExpression> { }

        "Type.this" should matchExpr<ASTThisExpression> {

            it::getQualifier shouldBePresent child {
                it::getImage shouldBe "Type"
            }
        }

    }

    parserTest("Field access exprs") {

        "Type.this.foo" should matchExpr<ASTFieldAccess> {
            it::getFieldName shouldBe "foo"
            it::getImage shouldBe "foo"

            it::getLeftHandSide shouldBePresent child<ASTThisExpression> {
                it::getQualifier shouldBePresent child<ASTAmbiguousNameExpr> { }
            }
        }

        "foo().foo" should matchExpr<ASTFieldAccess> {

            it::getFieldName shouldBe "foo"
            it::getImage shouldBe "foo"

            it::getLeftHandSide shouldBePresent child<ASTMethodCall> {
                it::getLhsExpression.shouldBeEmpty()
                it::getMethodName shouldBe "foo"
                it::getImage shouldBe "foo"

                it::getArguments shouldBe child {}
            }
        }

    }


    parserTest("Method call exprs") {

        "Type.this.foo()" should matchExpr<ASTMethodCall> {
            it::getMethodName shouldBe "foo"
            it::getImage shouldBe "foo"

            it::getLhsExpression shouldBePresent child<ASTThisExpression> {
                it::getQualifier shouldBePresent child<ASTAmbiguousNameExpr> {
                    it::getImage shouldBe "Type"
                }
            }


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

            it::getLhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                it::getImage shouldBe "foo.bar"
            }

            it::getArguments shouldBe child {}
        }

        "foo.<B>f()" should matchExpr<ASTMethodCall> {
            it::getMethodName shouldBe "f"
            it::getImage shouldBe "f"

            it::getLhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
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

            it::getLhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                it::getImage shouldBe "foo"
            }

            it::getArguments shouldBe child {
                child<ASTLambdaExpression> {
                    child<ASTVariableDeclaratorId> { }

                    child<ASTMethodCall> {
                        it::getMethodName shouldBe "f"
                        it::getImage shouldBe "f"

                        it::getLhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                            it::getImage shouldBe "it"
                        }

                        it::getArguments shouldBe child {

                            child<ASTAmbiguousNameExpr> {
                                it::getImage shouldBe "e"
                            }
                        }
                    }
                }
            }
        }
    }

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
            it::getLhsType.shouldBeEmpty()
            it::isConstructorReference shouldBe false
            it::getTypeArguments.shouldBeEmpty()

            it::getLhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                it::getImage shouldBe "foobar.b"
            }
        }

        "foobar.b::<B>foo" should matchExpr<ASTMethodReference> {

            it::getImage shouldBe "foo"
            it::getMethodName shouldBePresent "foo"
            it::getLhsType.shouldBeEmpty()
            it::isConstructorReference shouldBe false

            it::getLhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                it::getImage shouldBe "foobar.b"
            }

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
                it::getTypeImage shouldBe "foobar.b"
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

    parserTest("Ambiguous names") {

        "a.b.c" should matchExpr<ASTAmbiguousNameExpr> {
            it::getImage shouldBe "a.b.c"
        }

        "a" should matchExpr<ASTAmbiguousNameExpr> {
            it::getImage shouldBe "a"
        }
    }

    parserTest("Assignment expressions") {

        "a = b -> { foo(b); }" should matchExpr<ASTAssignmentExpression> {
            it::getOperator shouldBe AssignmentOperator.EQ
            it::isCompound shouldBe false

            it::getLeftHandSide shouldBe child<ASTAmbiguousNameExpr> {
                it::getImage shouldBe "a"
            }

            it::getRightHandSide shouldBe child<ASTLambdaExpression> {
                it.isFindBoundary shouldBe true
                it.type shouldBe null
                it.typeDefinition shouldBe null

                child<ASTVariableDeclaratorId> {
                    it.variableName shouldBe "b"
                }
                child<ASTBlock> {
                    child<ASTBlockStatement> {
                        it.isAllocation shouldBe false

                        child<ASTStatement> {
                            child<ASTStatementExpression> {
                                child<ASTMethodCall> {
                                    it.methodName shouldBe "foo"

                                    it.arguments shouldBe child {
                                        child<ASTAmbiguousNameExpr> {
                                            it.image shouldBe "b"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        "a = 2" should matchExpr<ASTAssignmentExpression> {
            it::getOperator shouldBe AssignmentOperator.EQ
            it::isCompound shouldBe false

            it::getLeftHandSide shouldBe child<ASTAmbiguousNameExpr> {
                it::getImage shouldBe "a"
            }

            it::getRightHandSide shouldBe child<ASTNumericLiteral> {}
        }

        "a.b().f *= 2" should matchExpr<ASTAssignmentExpression> {
            it::getOperator shouldBe AssignmentOperator.MUL_EQ
            it::isCompound shouldBe true

            it::getLeftHandSide shouldBe child<ASTFieldAccess> {
                it.fieldName shouldBe "f"

                child<ASTMethodCall> {
                    it.methodName shouldBe "b"

                    child<ASTAmbiguousNameExpr> {}

                    it.arguments shouldBe child {}
                }
            }

            it::getRightHandSide shouldBe child<ASTNumericLiteral> {}

        }

        "a >>>= 2" should matchExpr<ASTAssignmentExpression> {
            it::getOperator shouldBe AssignmentOperator.UNSIGNED_RIGHT_SHIFT_EQ
            it::isCompound shouldBe true


            it::getLeftHandSide shouldBe child<ASTAmbiguousNameExpr> {
                it::getImage shouldBe "a"
            }

            it::getRightHandSide shouldBe child<ASTNumericLiteral> {}
        }

        "a = b = 3" should matchExpr<ASTAssignmentExpression> {
            it::getOperator shouldBe AssignmentOperator.EQ
            it::isCompound shouldBe false

            it::getLeftHandSide shouldBe child<ASTAmbiguousNameExpr> {
                it::getImage shouldBe "a"
            }

            it::getRightHandSide shouldBe child<ASTAssignmentExpression> {
                it::getOperator shouldBe AssignmentOperator.EQ
                it::isCompound shouldBe false

                it::getLeftHandSide shouldBe child<ASTAmbiguousNameExpr> {
                    it::getImage shouldBe "b"
                }

                it::getRightHandSide shouldBe child<ASTNumericLiteral> {}
            }
        }

    }

    parserTest("Class instance creation") {

        "new Foo(a)" should matchExpr<ASTConstructorCall> {

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"
            }

            it::getArguments shouldBe child {

                child<ASTAmbiguousNameExpr> { }
            }
        }

        "new <Bar> Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getExplicitTypeArguments shouldBePresent child {
                unspecifiedChild()
            }

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                it::getTypeArguments shouldBePresent child {
                    unspecifiedChild()
                }
            }

            it::getArguments shouldBe child {}
        }

        "new @Lol Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getExplicitTypeArguments.shouldBeEmpty()

            child<ASTAnnotation>(ignoreChildren = true) {}

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                it::getTypeArguments shouldBePresent child {
                    unspecifiedChild()
                }
            }

            it::getArguments shouldBe child {}
        }
    }


    parserTest("Qualified class instance creation") {

        "a.g.c.new Foo(a)" should matchExpr<ASTConstructorCall> {

            it::getLhsExpression shouldBePresent child<ASTAmbiguousNameExpr> {
                it::getImage shouldBe "a.g.c"
            }

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"
            }

            it::getArguments shouldBe child {

                child<ASTAmbiguousNameExpr> { }
            }
        }

        "new O().new <Bar> Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getLhsExpression shouldBePresent child<ASTConstructorCall> {

                it::getTypeNode shouldBe child {
                    it::getTypeImage shouldBe "O"
                }

                it::getArguments shouldBe child {}
            }

            it::getExplicitTypeArguments shouldBePresent child {
                unspecifiedChild()
            }

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                it::getTypeArguments shouldBePresent child {
                    unspecifiedChild()
                }
            }

            it::getArguments shouldBe child {}
        }

        "method().new @Lol Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getLhsExpression shouldBePresent child<ASTMethodCall> {
                it::getMethodName shouldBe "method"
                it::getArguments shouldBe child {}
            }

            it::getExplicitTypeArguments.shouldBeEmpty()

            child<ASTAnnotation>(ignoreChildren = true) {}

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                it::getTypeArguments shouldBePresent child {
                    unspecifiedChild()
                }
            }

            it::getArguments shouldBe child {}
        }
    }

    parserTest("Array creation") {

        "new int[2][]" should matchExpr<ASTArrayAllocation> {

            it::getElementTypeNode shouldBe child<ASTPrimitiveType> {
                it::getModelConstant shouldBe ASTPrimitiveType.PrimitiveType.INT
                it::getTypeImage shouldBe "int"
            }

            it::getArrayDims shouldBe child {
                it::getArrayDepth shouldBe 2

                child<ASTNumericLiteral> {}
            }
        }

        "new @Foo int[3][2]" should matchExpr<ASTArrayAllocation> {
            child<ASTAnnotation> {
                it::getAnnotationName shouldBe "Foo"

                child<ASTMarkerAnnotation> {
                    it::getAnnotationName shouldBe "Foo"

                    child<ASTName> {
                        it::getNameDeclaration shouldBe null
                    }
                }
            }

            it::getElementTypeNode shouldBe child<ASTPrimitiveType> {
                it::isBoolean shouldBe false
                it::getModelConstant shouldBe ASTPrimitiveType.PrimitiveType.INT
                it::getTypeImage shouldBe "int"
            }

            it::getArrayDims shouldBe child {
                it::isArray shouldBe true
                it::getArrayDepth shouldBe 2

                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 3
                }
                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 2
                }
            }
        }

        "(new int[3])[2]" should matchExpr<ASTArrayAccess> {
            child<ASTParenthesizedExpression> {

                it::getWrappedExpression shouldBe child<ASTArrayAllocation> {

                    it::getElementTypeNode shouldBe child<ASTPrimitiveType> {
                        it::getTypeImage shouldBe "int"
                    }

                    it::getArrayDims shouldBe child {
                        it::isArray shouldBe true
                        it::getArrayDepth shouldBe 1

                        child<ASTNumericLiteral> {}
                    }
                }
            }
            child<ASTNumericLiteral> {}
        }

        "new Foo[0]" should matchExpr<ASTArrayAllocation> {
            it::getElementTypeNode shouldBe child<ASTClassOrInterfaceType> {
                it::isAnonymousClass shouldBe false
                it::isReferenceToClassSameCompilationUnit shouldBe true
                it::getTypeImage shouldBe "Foo"
            }

            it::getArrayDims shouldBe child {
                it::isArray shouldBe true
                it::getArrayDepth shouldBe 1

                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 0
                }
            }
        }


        "new Foo[] { f, g }" should matchExpr<ASTArrayAllocation> {

            it::getElementTypeNode shouldBe child<ASTClassOrInterfaceType> {
                it::isAnonymousClass shouldBe false
                it::isReferenceToClassSameCompilationUnit shouldBe true
                it::getTypeImage shouldBe "Foo"
            }

            it::getArrayDims shouldBe child {
                it::isArray shouldBe true
                it::getArrayDepth shouldBe 1

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

            it::getElementTypeNode shouldBe child<ASTPrimitiveType> {
                it::isBoolean shouldBe false
                it::getModelConstant shouldBe ASTPrimitiveType.PrimitiveType.INT
                it::getTypeImage shouldBe "int"
            }

            it::getArrayDims shouldBe child {
                it::isArray shouldBe true
                it::getArrayDepth shouldBe 2

                child<ASTArrayInitializer> {
                    child<ASTVariableInitializer> {
                        child<ASTArrayInitializer> {
                            child<ASTVariableInitializer> {
                                child<ASTNumericLiteral> {
                                    it::getValueAsInt shouldBe 1
                                }
                            }
                        }
                    }
                    child<ASTVariableInitializer> {
                        child<ASTArrayInitializer> {
                            child<ASTVariableInitializer> {
                                child<ASTNumericLiteral> {
                                    it::getValueAsInt shouldBe 2
                                }
                            }
                        }
                    }
                }
            }
        }

        "new int[][] { { 1 , 2 }, null }" should matchExpr<ASTArrayAllocation> {

            it::getElementTypeNode shouldBe child<ASTPrimitiveType> {
                it::getTypeImage shouldBe "int"
            }

            it::getArrayDims shouldBe child {
                it::isArray shouldBe true
                it::getArrayDepth shouldBe 2

                child<ASTArrayInitializer> {
                    child<ASTVariableInitializer> {
                        child<ASTArrayInitializer> {
                            child<ASTVariableInitializer> {
                                child<ASTNumericLiteral> {
                                    it::getValueAsInt shouldBe 1
                                }
                            }
                            child<ASTVariableInitializer> {
                                child<ASTNumericLiteral> {
                                    it::getValueAsInt shouldBe 2
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