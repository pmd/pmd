/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe

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





