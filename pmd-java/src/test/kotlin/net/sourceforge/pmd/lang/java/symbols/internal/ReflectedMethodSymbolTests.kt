/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldNotBe
import javasymbols.testdata.impls.Overloads

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ReflectedMethodSymbolTests : WordSpec({

    "A method symbol" should {

        // note that this is still limited because parameter symbols
        // don't yet have access to their type
        "not be equal to its overloads" {

            val clazz = classSym(Overloads::class.java)!!

            val overloads = clazz.getDeclaredMethods("anOverload")

            overloads[0] shouldNotBe overloads[1]
        }

    }

})
