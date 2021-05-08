/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forExactly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import javasymbols.testdata.Enums
import javasymbols.testdata.NestedClasses
import javasymbols.testdata.Statics
import javasymbols.testdata.impls.GenericClass
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.types.testTypeSystem
import org.objectweb.asm.Opcodes
import kotlin.test.assertSame

/**
 * @author Clément Fournier
 */
class AsmLoaderTest : FunSpec({


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

    test("Nested class test") {

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


    test("Nested class modifiers") {

        val outerName = Statics::class.java.name

        val inner = symLoader.resolveClassFromBinaryName("$outerName\$ProtectedStatic")!!

        inner.modifiers shouldBe (Opcodes.ACC_PROTECTED or Opcodes.ACC_STATIC)
    }

    test("Inner class constructors reflect no parameter for the enclosing instance") {

        val outerName = NestedClasses::class.java.name

        val inner = symLoader.resolveClassFromBinaryName("$outerName\$Inner")!!

        inner.modifiers shouldBe Opcodes.ACC_PUBLIC

        inner.constructors.forExactly(1) {
            it.formalParameters.shouldHaveSize(0) // even if there's a synthetic parameter
        }
        inner.constructors.forExactly(1) {
            it.formalParameters.shouldHaveSize(1) // the one with a generic signature
        }
    }

    test("Canonical name") {

        val outerName = Statics::class.java.name

        val inner = symLoader.resolveClassFromCanonicalName("$outerName.ProtectedStatic")!!
        val second = symLoader.resolveClassFromBinaryName("$outerName\$ProtectedStatic")!!

        assertSame(inner, second)
    }

    test("Unresolved class should have object as superclass") {

        val inner = symLoader.resolveFromInternalNameCannotFail("does/not/exist")!!
        val second = symLoader.resolveFromInternalNameCannotFail("does/not/exist")!!

        assertSame(inner, second)

        inner.superclass shouldBe ts.OBJECT.symbol
    }

    test("Enum constants") {

        val outerName = Enums::class.java.name

        val emptyEnum = symLoader.resolveClassFromBinaryName("$outerName\$Empty")!!
        emptyEnum::getEnumConstants shouldBe emptySet()

        val withConstants = symLoader.resolveClassFromBinaryName("$outerName\$SomeConstants")!!
        withConstants::getEnumConstants shouldBe setOf("A", "B")

        val notAnEnum = symLoader.resolveClassFromBinaryName(outerName)!!
        notAnEnum::getEnumConstants shouldBe null
    }
})
