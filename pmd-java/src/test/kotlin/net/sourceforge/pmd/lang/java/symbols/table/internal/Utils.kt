/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import net.sourceforge.pmd.lang.java.ast.JavaNode
import net.sourceforge.pmd.lang.java.symbols.internal.testSymResolver

internal fun testResolveHelper(packageName: String, jdkVersion: Int = 11, logger: TestCheckLogger = TestCheckLogger()) =
        SymbolTableHelper(packageName, testSymResolver, logger)

class TestCheckLogger : SemanticChecksLogger {

    private val accumulator = mutableMapOf<String, Pair<JavaNode, Array<out Any>>>()

    override fun warning(location: JavaNode, message: String, args: Array<Any>) {
        accumulator[message] = (location to args)
    }

}
