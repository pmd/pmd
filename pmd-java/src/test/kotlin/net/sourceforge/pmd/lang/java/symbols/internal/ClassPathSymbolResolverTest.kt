/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.shouldBe
import io.kotlintest.specs.AbstractFunSpec
import javasymbols.testdata.impls.GenericClass
import javasymbols.testdata.impls.SomeInnerClasses
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ClasspathSymbolResolver

/**
 * @author Clément Fournier
 */
class ClassPathSymbolResolverTest : AbstractFunSpec({


    val cl = Thread.currentThread().contextClassLoader
    val resolver = ClasspathSymbolResolver(cl, testSymFactory)

    test("Test outer class") {

        resolver.resolveClassFromCanonicalName(GenericClass::class.java.canonicalName) shouldBe classSym(GenericClass::class.java)
    }

    test("Test inner class") {

        resolver.resolveClassFromCanonicalName("javasymbols.testdata.impls.SomeInnerClasses.Inner")
                .shouldBe(classSym(SomeInnerClasses.Inner::class.java))
    }

    test("Test inner class with dollar") {

        resolver.resolveClassFromCanonicalName("javasymbols.testdata.impls.SomeInnerClasses\$Inner")
                .shouldBe(classSym(SomeInnerClasses.Inner::class.java))
    }


})
