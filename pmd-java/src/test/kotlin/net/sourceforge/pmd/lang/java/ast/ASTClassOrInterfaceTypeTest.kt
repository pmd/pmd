package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTClassOrInterfaceTypeTest : FunSpec({


    testGroup("Test non-recursive ClassOrInterfaceTypes") {

        "java.util.List" should matchType<ASTClassOrInterfaceType> {
            it.typeImage shouldBe "java.util.List"
        }

        "java.util.List<F>" should matchType<ASTClassOrInterfaceType> {


            child<ASTTypeArguments> {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it.typeImage shouldBe "F"
                    }
                }
            }
        }
    }


})