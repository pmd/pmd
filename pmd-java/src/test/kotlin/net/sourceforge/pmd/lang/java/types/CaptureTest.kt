/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.types.TypeConversion.*

/**
 * @author Clément Fournier
 */
class CaptureTest : FunSpec({

    with(TypeDslOf(testTypeSystem)) {
        with(gen) {

            test("Capture is a noop on raw types") {

                capture(t_List) shouldBe t_List

            }

            test("Capture merges declared bounds and bounds of wildcard") {

                val t_Scratch = javaParser.parseSomeClass("class Scratch<T extends java.util.List<T>> {}")

                val matcher = captureMatcher(`?`)

                capture(t_Scratch[`?`]) shouldBe t_Scratch[matcher]

                matcher.also {
                    it.isCaptured shouldBe true
                    it.isCaptureOf(`?`) shouldBe true
                    it.upperBound shouldBe t_List[matcher]
                    it.lowerBound shouldBe ts.NULL_TYPE
                }
            }

            test("Capture merges declared bounds and bounds of wildcard (self referential bound)") {

                val t_Scratch = javaParser.parseSomeClass("class Scratch<T extends Scratch<T>> {}")

                val matcher = captureMatcher(`?`)

                capture(t_Scratch[`?`]) shouldBe t_Scratch[matcher]

                matcher.also {
                    it.isCaptured shouldBe true
                    it.isCaptureOf(`?`) shouldBe true
                    it.upperBound shouldBe t_Scratch[matcher]
                    it.lowerBound shouldBe ts.NULL_TYPE
                }
            }

        }
    }


})
