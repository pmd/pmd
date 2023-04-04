/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forExactly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import javasymbols.testdata.Enums
import javasymbols.testdata.NestedClasses
import javasymbols.testdata.Statics
import javasymbols.testdata.deep.AClassWithLocals
import javasymbols.testdata.deep.`Another$ClassWith$Dollar`
import javasymbols.testdata.deep.OuterWithoutDollar
import javasymbols.testdata.impls.GenericClass
import net.sourceforge.pmd.lang.ast.test.IntelliMarker
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.types.testTypeSystem
import org.objectweb.asm.Opcodes
import kotlin.test.assertSame

/**
 * @author Clément Fournier
 */
class AsmLoaderTest : IntelliMarker, FunSpec({


    // TODO tests:
    //   self-referential typevar bound : class C<T extends C<T>>
    //   self-referential superclass : class C extends Sup<C> implements I<Sup<C>>
    //   access flags
    //   method reference with static ctdecl & zero formal parameters (asInstanceMethod)

    val contextClasspath = Classpath {  Thread.currentThread().contextClassLoader.getResource(it) }

    test("First ever ASM test") {

        val symLoader = AsmSymbolResolver(testTypeSystem, contextClasspath)

        val loaded = symLoader.resolveClassFromBinaryName("javasymbols.testdata.StaticNameCollision")!!

        loaded::getSimpleName shouldBe "StaticNameCollision"
        loaded::getBinaryName shouldBe "javasymbols.testdata.StaticNameCollision"
        loaded::getTypeParameters shouldBe emptyList()
        loaded::getTypeParameterCount shouldBe 0

    }

    val ts = testTypeSystem

    /**
     * Note: we don't share a single resolver between all tests because that
     * could cause issues because of the order of classloading (the order of test execution).
     */
    fun symLoader() = AsmSymbolResolver(ts, contextClasspath)

    test("Generic class") {

        val loaded = symLoader().resolveClassFromBinaryName(GenericClass::class.java.name)!!

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

        val symLoader = symLoader()
        val outer = symLoader.resolveClassFromBinaryName(outerName)!!
        val inner = symLoader.resolveClassFromBinaryName("$outerName\$Inner")!!
        val iinner = symLoader.resolveClassFromBinaryName("$outerName\$Inner\$IInner")!!


        outer.declaredClasses.shouldContainExactlyInAnyOrder(inner)
        inner.declaredClasses.shouldContainExactlyInAnyOrder(iinner)
        iinner.declaredClasses.shouldBeEmpty()

        iinner.enclosingClass.shouldBeSameInstanceAs(inner)
        inner.enclosingClass.shouldBeSameInstanceAs(outer)
        outer.enclosingClass.shouldBeNull()

        iinner.enclosingMethod.shouldBeNull()
        inner.enclosingMethod.shouldBeNull()
        outer.enclosingMethod.shouldBeNull()
    }


    test("Nested class modifiers") {

        val outerName = Statics::class.java.name

        val inner = symLoader().resolveClassFromBinaryName("$outerName\$ProtectedStatic")!!

        inner.modifiers shouldBe (Opcodes.ACC_PROTECTED or Opcodes.ACC_STATIC)
    }

    test("Inner names with dollars") {

        val outerName = `Another$ClassWith$Dollar`::class.java.name

        val inner = symLoader().resolveClassFromBinaryName("$outerName\$AnInner\$ClassWithDollar")!!

        inner.simpleName shouldBe "AnInner\$ClassWithDollar"
        inner.canonicalName shouldBe "javasymbols.testdata.deep.Another\$ClassWith\$Dollar.AnInner\$ClassWithDollar"
    }

    test("Regular inner name not populated") {
        // this test doesn't reproduce the broken behaviour, it looks like
        // this is somewhat compiler-dependent what InnerClasses attribute
        // are in the class file...

        val outerName = OuterWithoutDollar::class.java.name

        val outer = symLoader().resolveClassFromBinaryName(outerName)!!
        val inner = outer.getDeclaredClass("Inner")!!

        inner.simpleName shouldBe "Inner"
        inner.canonicalName shouldBe "$outerName.Inner"
    }

    test("Local classes") {

        val outerClass = AClassWithLocals::class.java
        val outerName = AClassWithLocals::class.java.name

        val symLoader = symLoader()
        val outer = symLoader.resolveClassFromBinaryName(outerName).shouldNotBeNull()

        outer.simpleName shouldBe outerClass.simpleName
        outer.canonicalName shouldBe outerClass.canonicalName!!

        outer.declaredClasses.shouldBeEmpty()

        symLoader.resolveClassFromBinaryName("$outerName$0").shouldBeNull()
        symLoader.resolveClassFromBinaryName("$outerName$0Local").shouldBeNull()
    }

    test("Deeper inner names with dollars") {

        val outerName = `Another$ClassWith$Dollar`::class.java.name

        val symLoader = symLoader()
        val deeper = symLoader.resolveClassFromBinaryName("$outerName\$AnInner\$ClassWithDollar\$ADeeper\$ClassWithDollar")!!

        deeper.simpleName shouldBe "ADeeper\$ClassWithDollar"
        deeper.canonicalName shouldBe "javasymbols.testdata.deep.Another\$ClassWith\$Dollar.AnInner\$ClassWithDollar.ADeeper\$ClassWithDollar"

        symLoader.resolveClassFromCanonicalName(deeper.canonicalName!!) shouldBeSameInstanceAs deeper
    }

    test("Simple name that looks like an anonymous name but isn't") {

        val klass = `Another$ClassWith$Dollar`.`DollarsAndNumbers$0`::class.java
        val binaryName = klass.name

        val symLoader = symLoader()
        val deeper = symLoader.resolveClassFromBinaryName(binaryName).shouldNotBeNull()

        deeper.simpleName shouldBe "DollarsAndNumbers\$0"
        deeper.canonicalName shouldBe klass.canonicalName!!
        deeper.binaryName shouldBe binaryName

        symLoader.resolveClassFromCanonicalName(deeper.canonicalName!!) shouldBeSameInstanceAs deeper
    }

    test("Inner class constructors reflect no parameter for the enclosing instance") {

        val outerName = NestedClasses::class.java.name

        val inner = symLoader().resolveClassFromBinaryName("$outerName\$Inner")!!

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

        val symLoader = symLoader()
        val inner = symLoader.resolveClassFromCanonicalName("$outerName.ProtectedStatic")!!
        val second = symLoader.resolveClassFromBinaryName("$outerName\$ProtectedStatic")!!

        assertSame(inner, second)
    }

    test("Unresolved class should have object as superclass") {

        val symLoader = symLoader()
        val inner = symLoader.resolveFromInternalNameCannotFail("does/not/exist")!!
        val second = symLoader.resolveFromInternalNameCannotFail("does/not/exist")!!

        assertSame(inner, second)

        inner.superclass shouldBe ts.OBJECT.symbol
    }

    test("Enum constants") {

        val outerName = Enums::class.java.name

        val emptyEnum = symLoader().resolveClassFromBinaryName("$outerName\$Empty")!!
        emptyEnum::getEnumConstants shouldBe emptyList()

        val withConstants = symLoader().resolveClassFromBinaryName("$outerName\$SomeConstants")!!
        withConstants.enumConstants.map { it.simpleName } shouldBe listOf("A", "B")

        val notAnEnum = symLoader().resolveClassFromBinaryName(outerName)!!
        notAnEnum::getEnumConstants shouldBe emptyList()
    }
})
