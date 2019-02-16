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

            it.elementType shouldBe child<ASTClassOrInterfaceType> {
                it.image shouldBe "ArrayTypes"
            }

            it.dimensions shouldBe child {

                child<ASTArrayTypeDim> {}
                child<ASTArrayTypeDim> {}
                child<ASTArrayTypeDim> {}
            }
        }
    }

    parserTest("Multi-Dim Array allocation") {
        "new ArrayTypes[][][] { }" should matchExpr<ASTAllocationExpression> {

            // not an array type
            child<ASTClassOrInterfaceType> {
                it.image shouldBe "ArrayTypes"
            }

            child<ASTArrayDimsAndInits> {
                child<ASTArrayInitializer> { }
            }
        }
    }

})