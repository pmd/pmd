/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.JavaNode
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.testSymResolver
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult

internal fun testResolveHelper(packageName: String, jdkVersion: Int = 11, logger: TestCheckLogger = TestCheckLogger()) =
        SymbolTableHelper(packageName, testSymResolver, logger)

class TestCheckLogger : SemanticChecksLogger {

    private val accumulator = mutableMapOf<String, Pair<JavaNode, Array<out Any>>>()

    override fun warning(location: JavaNode, message: String, args: Array<Any>) {
        accumulator[message] = (location to args)
    }

}


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
