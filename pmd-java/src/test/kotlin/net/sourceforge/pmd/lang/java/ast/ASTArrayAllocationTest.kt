package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * Nodes that previously corresponded to ASTAllocationExpression.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTArrayAllocationTest : ParserTestSpec({

    parserTest("Array creation") {

        "new int[2][]" should matchExpr<ASTArrayAllocation> {

            it::getElementTypeNode shouldBe child<ASTPrimitiveType> {
                it::getModelConstant shouldBe ASTPrimitiveType.PrimitiveType.INT
                it::getTypeImage shouldBe "int"
            }

            it::getArrayDims shouldBe child {
                it::getArrayDepth shouldBe 2

                dimExpr {
                    int(2)
                }
                arrayDim()
            }
        }

        "new @Foo int[3][2]" should matchExpr<ASTArrayAllocation> {



            it::getElementTypeNode shouldBe child<ASTPrimitiveType> {
                it::getModelConstant shouldBe ASTPrimitiveType.PrimitiveType.INT
                it::getTypeImage shouldBe "int"

                it::getDeclaredAnnotations shouldBe listOf(annotation("Foo"))
            }

            it::getArrayDims shouldBe child {
                it::getArrayDepth shouldBe 2

                dimExpr {
                    int(3)
                }
                dimExpr {
                    int(2)
                }
            }
        }
        "new @Foo int @Bar [3][2]" should matchExpr<ASTArrayAllocation> {



            it::getElementTypeNode shouldBe child<ASTPrimitiveType> {
                it::getModelConstant shouldBe ASTPrimitiveType.PrimitiveType.INT
                it::getTypeImage shouldBe "int"

                it::getDeclaredAnnotations shouldBe listOf(annotation("Foo"))
            }

            it::getArrayDims shouldBe child {
                it::getArrayDepth shouldBe 2

                dimExpr {
                    it::getDeclaredAnnotations shouldBe listOf(annotation("Bar"))

                    int(3)
                }
                dimExpr {
                    int(2)
                }
            }
        }

        "(new int[3])[2]" should matchExpr<ASTArrayAccess> {
            parenthesized {

                child<ASTArrayAllocation> {

                    it::getElementTypeNode shouldBe child<ASTPrimitiveType> {
                        it::getTypeImage shouldBe "int"
                    }

                    it::getArrayDims shouldBe child {
                        it::getArrayDepth shouldBe 1

                        dimExpr {
                            int(3)
                        }
                    }
                }
            }
            it::getIndexExpression shouldBe int(2)
        }

        "new Foo[0]" should matchExpr<ASTArrayAllocation> {
            it::getElementTypeNode shouldBe child<ASTClassOrInterfaceType> {
                it::isAnonymousClass shouldBe false
                it::isReferenceToClassSameCompilationUnit shouldBe true
                it::getTypeImage shouldBe "Foo"
            }

            it::getArrayDims shouldBe child {
                it::getArrayDepth shouldBe 1

                dimExpr {
                    int(0)
                }
            }
        }

    }

    parserTest("With array initializer") {

        "new Foo[] { f, g }" should matchExpr<ASTArrayAllocation> {

            it::getArrayDepth shouldBe 1

            it::getElementTypeNode shouldBe child<ASTClassOrInterfaceType> {
                it::isAnonymousClass shouldBe false
                it::isReferenceToClassSameCompilationUnit shouldBe true
                it::getTypeImage shouldBe "Foo"
            }

            it::getArrayDims shouldBe child {
                it::getArrayDepth shouldBe 1
                arrayDim()
            }

            it::getArrayInitializer shouldBe child {
                variableAccess("f")
                variableAccess("g")
            }
        }

        "new int[][] { { 1 }, { 2 } }" should matchExpr<ASTArrayAllocation> {

            it::getElementTypeNode shouldBe child<ASTPrimitiveType> {
                it::getModelConstant shouldBe ASTPrimitiveType.PrimitiveType.INT
                it::getTypeImage shouldBe "int"
            }

            it::getArrayDims shouldBe child {
                it::getArrayDepth shouldBe 2

                arrayDim()
                arrayDim()
            }

            it::getArrayInitializer shouldBe child {
                child<ASTArrayInitializer> {
                    int(1)
                }

                child<ASTArrayInitializer> {
                    int(2)
                }
            }
        }

        "new int[][] { { 1 , 2 }, null }" should matchExpr<ASTArrayAllocation> {

            it::getElementTypeNode shouldBe child<ASTPrimitiveType> {
                it::getTypeImage shouldBe "int"
            }

            it::getArrayDims shouldBe child {
                it::getArrayDepth shouldBe 2

                arrayDim()
                arrayDim()
            }

            it::getArrayInitializer shouldBe child {
                child<ASTArrayInitializer> {
                    int(1)
                    int(2)
                }
                child<ASTNullLiteral> {}
            }
        }
    }
})
