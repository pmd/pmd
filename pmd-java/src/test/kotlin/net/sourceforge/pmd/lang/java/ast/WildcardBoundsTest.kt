package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe


class WildcardBoundsTest : ParserTestSpec({

    parserTest("Simple grammar test") {

        "SomeClass<? extends Another>" should matchType<ASTClassOrInterfaceType> {

            it::getTypeArguments shouldBe child {
                child<ASTWildcardType> {

                    it::getTypeBoundNode shouldBe child<ASTClassOrInterfaceType> {
                        it::getTypeImage shouldBe "Another"
                    }
                }
            }

        }
    }

})





