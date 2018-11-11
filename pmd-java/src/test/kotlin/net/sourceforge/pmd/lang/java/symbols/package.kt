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
import net.sourceforge.pmd.lang.java.ast.JScopeResolver
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

/** Testing utilities */

object DefaultAnalysisConfiguration : AstAnalysisConfiguration {
    override fun getTypeResolutionClassLoader(): ClassLoader = ParserTstUtil::class.java.classLoader

    override fun getLanguageVersion(): LanguageVersion = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("11")
}

fun Class<*>.parse(): ASTCompilationUnit {
    val acu = ParserTstUtil.parseJavaDefaultVersion(this)
    JScopeResolver(DefaultAnalysisConfiguration).visit(acu, DefaultAnalysisConfiguration)

    return acu
}

inline fun <reified T> Any?.shouldBeA(noinline assertions: (T) -> Unit = {}): T {
    when (this) {
        is T -> assertions(this)
        else -> fail("Wrong type, expected ${T::class.simpleName}, actual ${this?.javaClass?.simpleName}")
    }
    return this
}

fun <T> Optional<T>.shouldBePresent(): T {
    return when {
        isPresent -> get()
        else -> fail("Optional should have been present!")
    }
}


fun <T> Optional<T>.shouldBeEmpty() {
    if (isPresent) fail("Optional should have been present!")
}

fun Stream<*>.shouldBeEmpty() = collect(Collectors.toList()) should beEmpty()


fun Stream<*>.shouldHaveSize(i: Int) = collect(Collectors.toList()) should haveSize(i)