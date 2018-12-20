package net.sourceforge.pmd.lang.java.symbols

import io.kotlintest.fail
import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.haveSize
import io.kotlintest.should
import net.sourceforge.pmd.lang.LanguageRegistry
import net.sourceforge.pmd.lang.LanguageVersion
import net.sourceforge.pmd.lang.ast.AstAnalysisConfiguration
import net.sourceforge.pmd.lang.java.JavaLanguageModule
import net.sourceforge.pmd.lang.java.ParserTstUtil
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit
import net.sourceforge.pmd.lang.java.ast.SymbolTableResolver
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

/** Testing utilities */

object DefaultAnalysisConfiguration : AstAnalysisConfiguration {
    override fun getTypeResolutionClassLoader(): ClassLoader = ParserTstUtil::class.java.classLoader

    override fun getLanguageVersion(): LanguageVersion = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).defaultVersion
}

fun Class<*>.parse(): ASTCompilationUnit =
        ParserTstUtil.parseJavaDefaultVersion(this).also { acu ->
            SymbolTableResolver(DefaultAnalysisConfiguration, acu).visit(acu, DefaultAnalysisConfiguration)
        }

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