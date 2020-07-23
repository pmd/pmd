/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.asm

import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.shouldBe
import io.kotlintest.specs.AbstractFunSpec
import javasymbols.testdata.NestedClasses
import javasymbols.testdata.impls.GenericClass
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.types.testTypeSystem

/**
 * @author Clément Fournier
 */
class AsmLoaderTest : AbstractFunSpec({


    // TODO tests:
    //   self-referential typevar bound : class C<T extends C<T>>
    //   self-referential superclass : class C extends Sup<C> implements I<Sup<C>>
    //   methods with synthetic parameters (ctors of inner classes)
    //   access flags

    //   method reference with static ctdecl & zero formal parameters (asInstanceMethod)


    test("First ever ASM test") {

        val symLoader = AsmSymbolResolver(testTypeSystem, Thread.currentThread().contextClassLoader)

        val loaded = symLoader.resolveClassFromBinaryName("javasymbols.testdata.StaticNameCollision")!!

        loaded::getSimpleName shouldBe "StaticNameCollision"
        loaded::getBinaryName shouldBe "javasymbols.testdata.StaticNameCollision"
        loaded::getTypeParameters shouldBe emptyList()
        loaded::getTypeParameterCount shouldBe 0

    }

    val ts = testTypeSystem
    val symLoader = AsmSymbolResolver(ts, Thread.currentThread().contextClassLoader)

    test("Generic class") {

        val loaded = symLoader.resolveClassFromBinaryName(GenericClass::class.java.name)!!

        loaded::getTypeParameterCount shouldBe 2
        loaded.typeParameters.let { tparams ->
            tparams[0].let {
                it::getName shouldBe "T"
                it::getUpperBound shouldBe ts.OBJECT
                it::getLowerBound shouldBe ts.NULL_TYPE
            }

            tparams[1].let {
                it::getName shouldBe "F"
                it::getUpperBound shouldBe tparams[0]
                it::getLowerBound shouldBe ts.NULL_TYPE
            }
        }
    }

    test("f:Nested class test") {

        val outerName = NestedClasses::class.java.name

        val outer = symLoader.resolveClassFromBinaryName(outerName)!!
        val inner = symLoader.resolveClassFromBinaryName("$outerName\$Inner")!!
        val iinner = symLoader.resolveClassFromBinaryName("$outerName\$Inner\$IInner")!!


        outer.declaredClasses.shouldContainExactlyInAnyOrder(inner)
        inner.declaredClasses.shouldContainExactlyInAnyOrder(iinner)
        iinner.declaredClasses.shouldBeEmpty()

        iinner.enclosingClass.shouldBe(inner)
        inner.enclosingClass.shouldBe(outer)
        outer.enclosingClass.shouldBe(null)

        iinner.enclosingMethod.shouldBe(null)
        inner.enclosingMethod.shouldBe(null)
        outer.enclosingMethod.shouldBe(null)
    }
})
