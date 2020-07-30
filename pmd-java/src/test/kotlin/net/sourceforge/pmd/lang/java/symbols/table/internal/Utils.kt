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
import net.sourceforge.pmd.lang.java.symbols.internal.testSymFactory
import net.sourceforge.pmd.lang.java.symbols.internal.testSymResolver
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable

internal fun testProcessor(jdkVersion: JavaVersion = JavaVersion.J13, logger: TestCheckLogger = TestCheckLogger()) =
        JavaAstProcessor.create(testSymResolver, testSymFactory, jdkVersion.pmdVersion, logger)

inline fun <reified T : JVariableSymbol> JSymbolTable.shouldResolveVarTo(simpleName: String, expected: JVariableSymbol): T =
        variables().resolveFirst(simpleName).shouldBeA<T> {
            it shouldBe expected
        }

infix fun JavaNode.shouldResolveToField(fieldId: ASTVariableDeclaratorId): JFieldSymbol =
        symbolTable.shouldResolveVarTo(fieldId.variableName, fieldId.symbol as JFieldSymbol)


infix fun JavaNode.shouldResolveToLocal(localId: ASTVariableDeclaratorId): JLocalVariableSymbol =
        symbolTable.shouldResolveVarTo(localId.variableName, localId.symbol as JLocalVariableSymbol)


inline fun <reified T : JVariableSymbol> JSymbolTable.shouldResolveVarTo(simpleName: String): T =
        variables().resolveFirst(simpleName).shouldBeA<T>()


inline fun <reified T : JTypeDeclSymbol> JSymbolTable.shouldResolveTypeTo(simpleName: String, expected: T) =
        types().resolveFirst(simpleName).shouldBeA<T>().shouldBe(expected)

inline fun <reified T : JTypeDeclSymbol> JSymbolTable.shouldResolveTypeTo(simpleName: String): T =
        types().resolveFirst(simpleName).shouldBeA<T>()
