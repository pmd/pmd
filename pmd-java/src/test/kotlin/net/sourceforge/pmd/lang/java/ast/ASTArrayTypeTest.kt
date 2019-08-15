package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe


/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTArrayTypeTest : ParserTestSpec({

    parserTest("Multi-Dim Array") {
        "ArrayTypes[][][]" should matchType<ASTArrayType> {

            it::getElementType shouldBe child<ASTClassOrInterfaceType> {
                it::getTypeImage shouldBe "ArrayTypes"
                it::getImage shouldBe "ArrayTypes"
            }

            it::getDimensions shouldBe child<ASTArrayDimensions> {

                child<ASTArrayTypeDim> {}
                child<ASTArrayTypeDim> {}
                child<ASTArrayTypeDim> {}
            }
        }
    }

    parserTest("Annotated array type") {
        "ArrayTypes[][] @A []" should matchType<ASTArrayType> {

            it::getElementType shouldBe child<ASTClassOrInterfaceType> {
                it::getTypeImage shouldBe "ArrayTypes"
                it::getImage shouldBe "ArrayTypes"
            }

            it::getDeclaredAnnotations shouldBe fromChild<ASTArrayDimensions, List<ASTAnnotation>> {

                child<ASTArrayTypeDim> {}
                child<ASTArrayTypeDim> {}
                fromChild<ASTArrayTypeDim, List<ASTAnnotation>> {

                    val lst = listOf(annotation("A"))

                    it::getDeclaredAnnotations shouldBe lst

                    lst
                }
            }
        }
    }

    parserTest("Multi-Dim Array allocation") {
        "new ArrayTypes[][][] { }" should matchExpr<ASTArrayAllocation> {

            child<ASTArrayType> {

                classType("ArrayTypes")
                it::getDimensions shouldBe child {
                    unspecifiedChildren(3)
                }
            }
            child<ASTArrayInitializer> { }
        }
    }

})
