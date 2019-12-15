/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import javasymbols.testdata.impls.GenericClass
import javasymbols.testdata.impls.SomeInnerClasses
import net.sourceforge.pmd.lang.java.symbols.classSym
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ClasspathSymbolResolver
import net.sourceforge.pmd.lang.java.symbols.testSymFactory

/**
 * @author Clément Fournier
 */
class ClassPathSymbolResolverTest : FunSpec({


    test("Test outer class") {


        val cl = Thread.currentThread().contextClassLoader
        val resolver = ClasspathSymbolResolver(cl, testSymFactory)

        resolver.resolveClassFromCanonicalName(GenericClass::class.java.canonicalName) shouldBe classSym(GenericClass::class.java)
    }

    test("Test inner class") {


        val cl = Thread.currentThread().contextClassLoader
        val resolver = ClasspathSymbolResolver(cl, testSymFactory)



        resolver.resolveClassFromCanonicalName("javasymbols.testdata.impls.SomeInnerClasses.Inner")
                .shouldBe(classSym(SomeInnerClasses.Inner::class.java))
    }

})
