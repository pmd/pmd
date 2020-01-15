/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;

/**
 * The result of resolution by a symbol table. This is provided as a way
 * to extend the usefulness of symbol tables at minimal cost later on. For
 * example, {@link #getSymbolTable()} can be used to check for hidden or
 * shadowed declarations.
 */
public interface ResolveResult<T> {


    /**
     * Returns the result of the search.
     */
    @NonNull T getResult();


    /**
     * Returns the node in the compilation unit that brings the
     * {@linkplain #getResult() result} in scope.
     *
     * <p>Examples:
     * <ul>
     * <li>If a type is imported via an import declaration, this returns
     * the relevant {@link ASTImportDeclaration}
     * <li>If a field is declared in the current compilation unit, this
     * returns the field declaration (the {@link ASTVariableDeclaratorId})
     * <li>If a field or class is inherited from a superclass on the
     * enclosing class, this returns the enclosing {@link ASTAnyTypeDeclaration}
     * <li>If a type is implicitly imported from {@code java.lang}, this
     * returns the {@link ASTCompilationUnit}.
     * </ul>
     *
     */
    @NonNull JavaNode getContributor();


    /**
     * Returns the symbol table that found this declaration.
     */
    @NonNull JSymbolTable getSymbolTable();


}
