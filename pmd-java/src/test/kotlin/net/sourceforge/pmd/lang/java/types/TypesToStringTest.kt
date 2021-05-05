/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.apache.commons.lang3.reflect.TypeLiteral

/**
 * @author Clément Fournier
 */
class TypesToStringTest : FunSpec({

    val ts = testTypeSystem

    test("Test toString") {

        ts.arrayType(ts.INT).apply {
            toString() shouldBe "int[]"
        }
    }

    test("wildcards") {

        ts.wildcard(true, ts.OBJECT).apply {
            toString() shouldBe "?"
        }

        ts.wildcard(true, ts.CLONEABLE).apply {
            toString() shouldBe "? extends java.lang.Cloneable"
        }

        ts.wildcard(false, ts.CLONEABLE).apply {
            toString() shouldBe "? super java.lang.Cloneable"
        }
    }
})


