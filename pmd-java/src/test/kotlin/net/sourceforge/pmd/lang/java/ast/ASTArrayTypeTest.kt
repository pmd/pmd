package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

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

            it::getDimensions shouldBe child<ASTArrayTypeDims> {

                child<ASTArrayTypeDim> {}
                child<ASTArrayTypeDim> {}
                child<ASTArrayTypeDim> {}
            }
        }
    }

    parserTest("Multi-Dim Array allocation") {
        "new ArrayTypes[][][] { }" should matchExpr<ASTArrayAllocation> {

            // not an array type
            child<ASTClassOrInterfaceType> {
                it::getTypeImage shouldBe "ArrayTypes"
                it::getImage shouldBe "ArrayTypes"
            }

            child<ASTArrayDimsAndInits> {
                child<ASTArrayInitializer> { }
            }
        }
    }

})