/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.IntelliMarker
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind

/**
 */
class JPrimitiveTypeTest : IntelliMarker, FunSpec({

    val ts = testTypeSystem
    with(TypeDslOf(ts)) {
        test("Test isPrimitive") {
            ts.allPrimitives.forEach { prim ->
                prim.isPrimitive shouldBe true
                PrimitiveTypeKind.values()
                    .forEach { kind ->
                        prim.isPrimitive(kind) shouldBe (prim.kind == kind)
                    }
            }
        }
        test("Test annotated primitive") {
            val annotated = `@A` on int
            annotated.typeAnnotations shouldContain `@A`.annot
            annotated.superTypeSet shouldBe setOf(int, double, float, long)
            annotated.superTypeSet.forEach {
                it.typeAnnotations.shouldBeEmpty()
            }
        }
    }

})


