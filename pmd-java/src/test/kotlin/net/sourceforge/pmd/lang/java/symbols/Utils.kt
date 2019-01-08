package net.sourceforge.pmd.lang.java.symbols

import io.kotlintest.matchers.haveSize
import io.kotlintest.should
import net.sourceforge.pmd.lang.java.ParserTstUtil
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit

/** Testing utilities */

fun Class<*>.getAst(): ASTCompilationUnit = ParserTstUtil.parseJavaDefaultVersion(this)

fun <T, K> List<T>.groupByUnique(keySelector: (T) -> K): Map<K, T> =
        groupBy(keySelector).mapValues { (_, vs) ->
            vs should haveSize(1)
            vs.first()
        }