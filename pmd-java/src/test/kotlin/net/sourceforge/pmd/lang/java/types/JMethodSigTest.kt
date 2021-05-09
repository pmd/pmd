/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.ast.ProcessorTestSpec

/**
 */
class JMethodSigTest : ProcessorTestSpec({

    parserTest("Test erasure of generic method") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            package p;
            class Foo<A,B extends Number> {
                <T, E extends RuntimeException>
                 T method(A a, B b) throws E {}
            }"""
        )

        val (a, b, t, e) = acu.typeVariables()
        spy.shouldBeOk {
            val m = acu.declaredMethodSignatures()[0].also {
                it.formalParameters shouldBe listOf(a, b)
                it.returnType shouldBe t
                it.typeParameters shouldBe listOf(t, e)
                it.thrownExceptions shouldBe listOf(e)

                it.toString() shouldBe "p.Foo<A, B extends java.lang.Number>.<T, E extends java.lang.RuntimeException> method(A, B extends java.lang.Number) -> T"
            }

            m.erasure.also {
                it.formalParameters shouldBe listOf(a.erasure, b.erasure)
                it.returnType shouldBe t.erasure
                it.typeParameters shouldBe emptyList()
                it.thrownExceptions shouldBe listOf(e.erasure)

                a.erasure shouldBe ts.OBJECT
                b.erasure shouldBe java.lang.Number::class.java.decl
                e.erasure shouldBe java.lang.RuntimeException::class.java.decl

                it.toString() shouldBe "p.Foo.method(java.lang.Object, java.lang.Number) -> java.lang.Object"
            }
        }
    }
})


