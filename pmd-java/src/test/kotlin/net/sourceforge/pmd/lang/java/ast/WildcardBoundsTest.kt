package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe

class WildcardBoundsTest : ParserTestSpec({

    parserTest("Simple grammar test") {

        "SomeClass<? extends Another>" should matchType<ASTClassOrInterfaceType> {

            it.typeArguments shouldBePresent child {
                child<ASTTypeArgument> {
                    child<ASTWildcardBounds> {
                        val ref = child<ASTClassOrInterfaceType> {
                            it.image shouldBe "Another"
                        }

                        it.typeBoundNode shouldBe ref
                    }
                }
            }

        }
    }

})





