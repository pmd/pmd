/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.forAll
import net.sourceforge.pmd.lang.java.symbols.internal.forAllEqual

/**
 * @author Clément Fournier
 */
class BoxingTest : FunSpec({

    test("Test boxing is dual of unboxing") {

        testTypeSystem.allPrimitives.forEach { prim ->
            prim.shouldBeSameInstanceAs(prim.unbox())
            val box = prim.box()
            box.shouldBeInstanceOf<JClassType>()
            box shouldNotBe prim

            val unbox = box.unbox()
            prim.shouldBeSameInstanceAs(unbox)
        }
    }

    test("Test boxing reference type is identity conversion") {

        testTypeSystem.refTypeGen.forAllEqual {
            Pair(it, it.box())
        }

        testTypeSystem.refTypeGen.forAllEqual {
            Pair(it, it.unbox())
        }
    }

    test("Test box implementations preserve their nature") {

        testTypeSystem.allPrimitives.forEach { prim ->
            val box = prim.box()

            prim.shouldBeSameInstanceAs(prim.box().unbox())
            box.shouldBeSameInstanceAs(box.box())
            box.erasure.shouldBeSameInstanceAs(box)
            box.withTypeArguments(emptyList()).shouldBeSameInstanceAs(box)
            box.genericTypeDeclaration.shouldBeSameInstanceAs(box)

            shouldThrow<IllegalArgumentException> {
                box.withTypeArguments(listOf(box)) // (list of anything)
            }
        }
    }
})
