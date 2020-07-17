package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.shouldNotBe
import io.kotlintest.specs.AbstractWordSpec
import javasymbols.testdata.impls.GenericClass
import javasymbols.testdata.impls.GenericClassCopy

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class JTypeParameterSymbolTest : AbstractWordSpec({

    "A type parameter symbol" should {

        "not be equal to type parameter symbols declared on any other entity" {
            classSym(GenericClass::class.java)!!.typeParameters shouldNotBe classSym(GenericClassCopy::class.java)!!.typeParameters
        }

    }

})
