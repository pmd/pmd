package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTArrayTypeTest : FunSpec({

    testGroup("Test array types") {

        "ArrayTypes[][][]" should matchType<ASTArrayType> {

        }

        "ArrayTypes[][][] c = new ArrayTypes[][][] { new ArrayTypes[1][2] };"

    }

})