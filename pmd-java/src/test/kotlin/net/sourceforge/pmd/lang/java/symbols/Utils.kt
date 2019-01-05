package net.sourceforge.pmd.lang.java.symbols

import io.kotlintest.fail
import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.haveSize
import io.kotlintest.should
import net.sourceforge.pmd.lang.java.ParserTstUtil
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit
import java.lang.reflect.Method
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.streams.toList

/** Testing utilities */

fun Class<*>.parse(): ASTCompilationUnit = ParserTstUtil.parseJavaDefaultVersion(this)

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

fun Class<*>.getMethodsByName(name: String): List<Method> = declaredMethods.asStream().filter { it.name == name }.toList()

fun <T> Array<T>.asStream(): Stream<T> = Arrays.stream(this)
fun <T> Iterator<T>.asStream(): Stream<T> =
        StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, 0), false)

fun <T> T.runIt(block: (T) -> Unit) = block(this) // kotlin.run uses a receiver, which I don't like

fun <T, K> List<T>.groupByUnique(keySelector: (T) -> K): Map<K, T> =
        groupBy(keySelector).mapValues { (_, vs) ->
            vs should haveSize(1)
            vs.first()
        }

