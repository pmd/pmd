/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.IntelliMarker
import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 */
class SpecialTypesTest : IntelliMarker, FunSpec({

    val ts = testTypeSystem
    test("Test null type") {
        shouldThrow<UnsupportedOperationException> {
            ts.NULL_TYPE.superTypeSet
        }
        ts.NULL_TYPE.isPrimitive shouldBe false
        ts.NULL_TYPE::isBottom shouldBe true
        ts.NULL_TYPE::isTop shouldBe false
        ts.UNKNOWN::isClassOrInterface shouldBe false
    }

    test("Test unknown type") {
        ts.UNKNOWN.isPrimitive shouldBe false
        ts.UNKNOWN::isClassOrInterface shouldBe false
        ts.UNKNOWN::isArray shouldBe false
        ts.UNKNOWN::isBottom shouldBe false
    }

})


