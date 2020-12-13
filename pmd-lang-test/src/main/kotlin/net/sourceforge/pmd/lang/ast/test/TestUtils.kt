/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.test

import io.kotest.matchers.Matcher
import io.kotest.matchers.equalityMatcher
import io.kotest.matchers.should
import net.sourceforge.pmd.Report
import net.sourceforge.pmd.RuleViolation
import net.sourceforge.pmd.lang.ast.Node
import kotlin.reflect.KCallable
import kotlin.reflect.jvm.isAccessible
import kotlin.test.assertEquals

/**
 * Extension to add the name of a property to error messages.
 *
 * @see [shouldBe].
 */
infix fun <N, V : N> KCallable<N>.shouldEqual(expected: V?) =
        assertWrapper(this, expected) { n, v ->
            // using shouldBe would perform numeric conversion
            // eg (3.0 shouldBe 3L) passes, even though (3.0 != 3L)
            // equalityMatcher doesn't do this conversion
            n.should(equalityMatcher(v) as Matcher<N>)
        }

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
 * If this conflicts with [io.kotest.matchers.shouldBe], use the equivalent [shouldEqual]
 *
 */
infix fun <N, V : N> KCallable<N>.shouldBe(expected: V?) = this.shouldEqual(expected)

infix fun <T> KCallable<T>.shouldMatch(expected: T.() -> Unit) = assertWrapper(this, expected) { n, v -> n should v }


inline fun <reified T> Any?.shouldBeA(f: (T) -> Unit = {}): T {
    if (this is T) {
        f(this)
        return this
    } else throw AssertionError("Expected an instance of ${T::class.java}, got $this")
}

operator fun <T> List<T>.component6() = get(5)
operator fun <T> List<T>.component7() = get(6)
operator fun <T> List<T>.component8() = get(7)
operator fun <T> List<T>.component9() = get(8)
operator fun <T> List<T>.component10() = get(9)
operator fun <T> List<T>.component11() = get(10)


/** Assert number of violations. */
fun assertSize(report: Report, size: Int): List<RuleViolation> {
    assertEquals(size, report.violations.size, message = "Wrong number of violations!")
    return report.violations
}

/** Assert number of suppressed violations. */
fun assertSuppressed(report: Report, size: Int): List<Report.SuppressedViolation> {
    assertEquals(size, report.suppressedViolations.size, message = "Wrong number of suppressed violations!")
    return report.suppressedViolations
}

/** Checks the coordinates of this node. */
fun Node.assertPosition(bline: Int, bcol: Int, eline: Int, ecol: Int) {
    reportLocation.apply {
        this::getBeginLine shouldBe bline
        this::getBeginColumn shouldBe bcol
        this::getEndLine shouldBe eline
        this::getEndColumn shouldBe ecol
    }
}
