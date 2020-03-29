/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.JavaParsingHelper.TestCheckLogger
import net.sourceforge.pmd.lang.java.ast.JavaVersion
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.testSymFactory
import net.sourceforge.pmd.lang.java.symbols.internal.testSymResolver
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult

internal fun testProcessor(jdkVersion: JavaVersion = JavaVersion.J13, logger: TestCheckLogger = TestCheckLogger()) =
        JavaAstProcessor.create(testSymResolver, testSymFactory, jdkVersion.pmdVersion, logger)

internal fun testResolveHelper(packageName: String, jdkVersion: JavaVersion = JavaVersion.J13, logger: TestCheckLogger = TestCheckLogger()) =
        SymbolTableHelper(packageName, testProcessor(jdkVersion, logger))


inline fun <reified T : JVariableSymbol> JSymbolTable.shouldResolveVarTo(simpleName: String,
                                                                         assertions: ResolveResult<T>.() -> Unit): T =
        resolveAssertWrapperInternalUglyName(resolveValueName(simpleName), simpleName, assertions)


inline fun <reified T : JTypeDeclSymbol> JSymbolTable.shouldResolveTypeTo(simpleName: String,
                                                                          assertions: ResolveResult<T>.() -> Unit): T =
        resolveAssertWrapperInternalUglyName(resolveTypeName(simpleName), simpleName, assertions)

@PublishedApi
internal inline fun <reified T> JSymbolTable.resolveAssertWrapperInternalUglyName(result: ResolveResult<*>?,
                                                                                  simpleName: String,
                                                                                  assertions: ResolveResult<T>.() -> Unit): T {

    assert(result != null) { "Could not resolve $simpleName inside $this" }
    return result!!.shouldBeA<ResolveResult<T>> {
        it.result.shouldBeA<T>()
        it.assertions()
    }.result
}
