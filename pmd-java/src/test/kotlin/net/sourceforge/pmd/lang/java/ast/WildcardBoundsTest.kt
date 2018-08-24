package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.should
import io.kotlintest.shouldBe
import org.junit.Test

// prototype using a junit syntax

class WildcardBoundsTest {

    @Test
    fun testTypeBounds() = junitParserTest {
        "SomeClass<? extends Another>" should matchType<ASTWildcardBounds> {

            val ref = child<ASTReferenceType> {
                child<ASTClassOrInterfaceType> {
                    it.image shouldBe "Another"
                }
            }

            it.typeBoundNode shouldBe ref
        }
    }
}





