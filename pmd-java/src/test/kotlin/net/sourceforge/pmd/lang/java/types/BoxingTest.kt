/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types

import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.matchers.types.shouldBeSameInstanceAs
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.AbstractFunSpec
import net.sourceforge.pmd.lang.java.symbols.internal.forAllEqual

/**
 * @author Clément Fournier
 */
class BoxingTest : AbstractFunSpec({

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

        RefTypeGen.forAllEqual {
            Pair(it, it.box())
        }

        RefTypeGen.forAllEqual {
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
