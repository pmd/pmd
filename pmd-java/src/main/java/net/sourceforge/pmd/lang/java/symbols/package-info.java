/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * Symbols represent Java declarations on a more abstract level than the AST.
 * The {@linkplain net.sourceforge.pmd.lang.java.types.TypeSystem Java type resolver}
 * uses symbols to reason about types in the analysed program. Symbols
 * are otherwise used to perform reference resolution internally, and
 * reference searches ({@link net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId#getLocalUsages()}).
 * See also the main interfaces, listed below.
 *
 * @see net.sourceforge.pmd.lang.java.symbols.JElementSymbol
 * @see net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable
 * @see net.sourceforge.pmd.lang.java.symbols.SymbolResolver
 */
package net.sourceforge.pmd.lang.java.symbols;

