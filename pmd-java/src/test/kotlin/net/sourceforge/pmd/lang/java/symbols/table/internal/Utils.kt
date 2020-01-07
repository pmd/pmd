/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import net.sourceforge.pmd.lang.java.symbols.internal.testSymResolver

internal fun testResolveHelper(packageName: String, jdkVersion: Int = 11) =
        SymbolTableResolveHelper(packageName, testSymResolver, jdkVersion)
