package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.shouldNotBe
import io.kotlintest.specs.AbstractWordSpec
import javasymbols.testdata.impls.Overloads

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ReflectedMethodSymbolTests : AbstractWordSpec({

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
