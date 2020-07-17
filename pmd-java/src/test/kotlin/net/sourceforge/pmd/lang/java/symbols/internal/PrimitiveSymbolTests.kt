package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.specs.AbstractWordSpec
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol.PRIMITIVE_PACKAGE

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class PrimitiveSymbolTests : AbstractWordSpec({



    "A primitive symbol" should {

        "have no fields" {
            PrimitiveSymGen.constants().forEach {
                it.declaredFields.shouldBeEmpty()
            }
        }

        "have no methods" {
            PrimitiveSymGen.constants().forEach {
                it.declaredMethods.shouldBeEmpty()
            }
        }

        "have no constructors" {
            PrimitiveSymGen.constants().forEach {
                it.constructors.shouldBeEmpty()
            }
        }

        "have no superclass" {
            PrimitiveSymGen.constants().forEach {
                it::getSuperclass shouldBe null
            }
        }

        "have no superInterfaces" {
            PrimitiveSymGen.constants().forEach {
                it::getSuperInterfaces shouldBe emptyList()
            }
        }


        "reflect its package name properly" {
            PrimitiveSymGen.constants().forEach {
                it::getPackageName shouldBe PRIMITIVE_PACKAGE
            }
        }

    }

})
