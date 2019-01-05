package net.sourceforge.pmd.lang.java.symbols

import io.kotlintest.fail
import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.haveSize
import io.kotlintest.should
import java.lang.reflect.Method
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.toList

/** Testing utilities */


/** Asserts that [this] is of type T, executes the given [assertions] on it, and returns it. */
inline fun <reified T> Any?.shouldBeA(noinline assertions: (T) -> Unit = {}): T =
        (this as? T)?.also(assertions)
        ?: fail("Wrong type, expected ${T::class.simpleName}, actual ${this?.javaClass?.simpleName}")

fun <T> Optional<T>.shouldBePresent(): T = orElseGet { fail("Optional should have been present!") }

fun <T> Optional<T>.shouldBeEmpty() {
    if (isPresent) fail("Optional should have been present!")
}

fun Stream<*>.shouldBeEmpty() = collect(Collectors.toList()) should beEmpty()

fun Stream<*>.shouldHaveSize(i: Int) = collect(Collectors.toList()) should haveSize(i)

fun Class<*>.getMethodsByName(name: String): List<Method> = declaredMethods.stream().filter { it.name == name }.toList()

fun <T> Array<T>.stream() = Arrays.stream(this)