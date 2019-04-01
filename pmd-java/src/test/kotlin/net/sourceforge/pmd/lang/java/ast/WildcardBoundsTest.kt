package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe

class WildcardBoundsTest : ParserTestSpec({

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





