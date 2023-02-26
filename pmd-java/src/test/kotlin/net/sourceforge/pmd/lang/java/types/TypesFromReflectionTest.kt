/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.types

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.JavaParsingHelper
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import org.apache.commons.lang3.reflect.TypeLiteral
import org.junit.jupiter.api.Assertions.*
import java.lang.NullPointerException

class TypesFromReflectionTest : FunSpec({


    test("Test reflect parsing") {

        // on the left this uses fromReflect, on the right the manual construction methods

        with(TypeDslOf(testTypeSystem)) {
            mirrorOf<String>()                                   shouldBe String::class.raw
            mirrorOf<GenericKlass<String>>()                     shouldBe GenericKlass::class[String::class]
            mirrorOf<Array<GenericKlass<String>>>()              shouldBe GenericKlass::class[String::class].toArray(1)
            mirrorOf<Array<Array<GenericKlass<in String>>>>()    shouldBe GenericKlass::class[`?` `super` String::class].toArray(2)
        }
    }

    test("test nested class") {
        assertReflects(MutableMap.MutableEntry::class.java, "java.util.Map.Entry")
    }

    test("testPrimitiveArray") {
        assertReflects(IntArray::class.java, "int[ ]")
    }

    test("testNestedClassArray") {
        val c = TypesFromReflection.loadSymbol(LOADER, "java.util.Map.Entry[ ]")
        // since java 12: Class#arrayType
        val klass = java.lang.reflect.Array.newInstance(MutableMap.MutableEntry::class.java, 0)::class.java
        assertReflects(klass, c)
    }

    test("testInvalidName") {
        shouldThrow<java.lang.IllegalArgumentException> {
            TypesFromReflection.loadSymbol(LOADER, "java.util.Map ]")
        }
    }

    test("testInvalidName2") {
        shouldThrow<java.lang.IllegalArgumentException> {
            TypesFromReflection.loadSymbol(LOADER, "[]")
        }
    }

    test("testNullName") {
        shouldThrow<NullPointerException> {
            TypesFromReflection.loadSymbol(LOADER, null)
        }
    }
}) {

    companion object {
        private val LOADER = JavaParsingHelper.TEST_TYPE_SYSTEM

        fun assertReflects(expected: Class<*>?, request: String?) {
            val c = TypesFromReflection.loadSymbol(LOADER, request)
            assertReflects(expected, c)
        }

        fun assertReflects(expected: Class<*>?, actual: JClassSymbol?) {
            if (expected == null) {
                assertNull(actual)
                return
            }
            assertNotNull(actual, "Expected $expected")
            assertEquals(expected.isAnnotation, actual!!.isAnnotation, "Annot")
            assertEquals(expected.isArray, actual.isArray, "Array")
            assertEquals(expected.modifiers.toLong(), actual.modifiers.toLong(), "Modifiers")
            if (actual.isArray) {
                assertReflects(expected.componentType, actual.arrayComponent as JClassSymbol?)
                // don't test names, the spec of Class::getName and JClassSymbol::getBinaryName
                // differ for arrays
                return
            }
            assertEquals(expected.name, actual.binaryName, "Binary name")
            assertEquals(expected.canonicalName, actual.canonicalName, "Canonical name")
            assertReflects(expected.enclosingClass, actual.enclosingClass)
        }


        private class GenericKlass<T>

        /**
         * Note: [T] must not contain type variables.
         */
        private inline fun <reified T> mirrorOf(): JTypeMirror =
            TypesFromReflection.fromReflect(
                testTypeSystem,
                object : TypeLiteral<T>() {}.type,
                LexicalScope.EMPTY,
                Substitution.EMPTY
            )!!
    }
}
