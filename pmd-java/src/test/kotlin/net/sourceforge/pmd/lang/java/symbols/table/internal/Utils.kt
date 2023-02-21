/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.JavaParsingHelper.TestCheckLogger
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId
import net.sourceforge.pmd.lang.java.ast.JavaNode
import net.sourceforge.pmd.lang.java.ast.JavaVersion
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.testSymResolver
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable
import net.sourceforge.pmd.lang.java.types.JTypeMirror
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger
import net.sourceforge.pmd.lang.java.types.testTypeSystem

inline fun <reified T : JVariableSymbol> JSymbolTable.shouldResolveVarTo(simpleName: String, expected: JVariableSymbol): T {
        val resolved = variables().resolveFirst(simpleName)
                ?: throw AssertionError("Unresolved variable $simpleName, expected $expected")
        return resolved.symbol.shouldBeA<T> {
                it shouldBe expected
        }
}

infix fun JavaNode.shouldResolveToField(fieldId: ASTVariableDeclaratorId): JFieldSymbol =
        symbolTable.shouldResolveVarTo(fieldId.name, fieldId.symbol as JFieldSymbol)


infix fun JavaNode.shouldResolveToLocal(localId: ASTVariableDeclaratorId): JLocalVariableSymbol =
        symbolTable.shouldResolveVarTo(localId.name, localId.symbol as JLocalVariableSymbol)


inline fun <reified T : JVariableSymbol> JSymbolTable.shouldResolveVarTo(simpleName: String): T =
        variables().resolveFirst(simpleName)!!.symbol.shouldBeA<T>()


inline fun <reified T : JTypeMirror> JSymbolTable.shouldResolveTypeTo(simpleName: String, expected: T) =
        types().resolveFirst(simpleName).shouldBeA<T>().shouldBe(expected)

inline fun <reified T : JTypeMirror> JSymbolTable.shouldResolveTypeTo(simpleName: String): T =
        types().resolveFirst(simpleName).shouldBeA<T>()
