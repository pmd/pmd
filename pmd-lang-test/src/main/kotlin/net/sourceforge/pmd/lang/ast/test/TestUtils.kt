/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.test

import io.kotlintest.should
import kotlin.reflect.KCallable
import kotlin.reflect.jvm.isAccessible
import io.kotlintest.shouldBe as ktShouldBe

/**
 * Extension to add the name of a property to error messages.
 *
 * @see [shouldBe].
 */
infix fun <N, V : N> KCallable<N>.shouldEqual(expected: V?) =
        assertWrapper(this, expected) { n, v -> n ktShouldBe v }

private fun <N, V> assertWrapper(callable: KCallable<N>, right: V, asserter: (N, V) -> Unit) {

    fun formatName() = "::" + callable.name.removePrefix("get").decapitalize()

    val value: N = try {
        callable.isAccessible = true
        callable.call()
    } catch (e: Exception) {
        throw RuntimeException("Couldn't fetch value for property ${formatName()}", e)
    }

    try {
        asserter(value, right)
    } catch (e: AssertionError) {

        if (e.message?.contains("expected:") == true) {
            // the exception has no path, let's add one
            throw AssertionError(e.message!!.replace("expected:", "expected property ${formatName()} to be"))
        }

        throw e
    }
}

/**
 * Extension to add the name of the property to error messages.
 * Use with double colon syntax, eg `it::isIntegerLiteral shouldBe true`.
 * For properties synthesized from Java getters starting with "get", you
 * have to use the name of the getter instead of that of the generated
 * property (with the get prefix).
 *
 * If this conflicts with [io.kotlintest.shouldBe], use the equivalent [shouldEqual]
 *
 */
infix fun <N, V : N> KCallable<N>.shouldBe(expected: V?) = this.shouldEqual(expected)

infix fun <T> KCallable<T>.shouldMatch(expected: T.() -> Unit) = assertWrapper(this, expected) { n, v -> n should v }

