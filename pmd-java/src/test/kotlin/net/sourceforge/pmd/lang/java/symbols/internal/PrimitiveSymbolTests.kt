/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol.PRIMITIVE_PACKAGE
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.types.testTypeSystem

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class PrimitiveSymbolTests : WordSpec({

    fun primitives(): List<JClassSymbol> = testTypeSystem.allPrimitives.map { it.symbol!! }

    "A primitive symbol" should {

        "have no fields" {
            primitives().forEach {
                it.declaredFields.shouldBeEmpty()
            }
        }

        "have no methods" {
            primitives().forEach {
                it.declaredMethods.shouldBeEmpty()
            }
        }

        "have no constructors" {
            primitives().forEach {
                it.constructors.shouldBeEmpty()
            }
        }

        "have no superclass" {
            primitives().forEach {
                it::getSuperclass shouldBe null
            }
        }

        "have no superInterfaces" {
            primitives().forEach {
                it::getSuperInterfaces shouldBe emptyList()
            }
        }


        "reflect its package name properly" {
            primitives().forEach {
                it::getPackageName shouldBe PRIMITIVE_PACKAGE
            }
        }

    }

})
