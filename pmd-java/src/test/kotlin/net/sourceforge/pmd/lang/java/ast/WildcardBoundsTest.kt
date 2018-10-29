package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

// prototype using a junit syntax

class WildcardBoundsTest : FunSpec({

    parserTest("Simple grammar test") {

        "SomeClass<? extends Another>" should matchType<ASTWildcardBounds> {

            val ref = child<ASTReferenceType> {
                child<ASTClassOrInterfaceType> {
                    it.image shouldBe "Another"
                }
            }

            it.typeBoundNode shouldBe ref
        }
    }

})





