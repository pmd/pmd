/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Marker interface for nodes that can appear on the top-level of a file.
 * In these contexts, they are children of the {@link ASTCompilationUnit CompilationUnit}
 * node. Note that both {@link ASTAnyTypeDeclaration AnyTypeDeclaration}
 * and {@link ASTEmptyDeclaration EmptyDeclaration} can appear also in
 * a {@linkplain ASTTypeBody type body}.
 *
 * <pre class="grammar">
 *
 * BodyDeclaration ::= {@link ASTAnyTypeDeclaration AnyTypeDeclaration}
 *                   | {@link ASTImportDeclaration ImportDeclaration}
 *                   | {@link ASTPackageDeclaration PackageDeclaration}
 *                   | {@link ASTEmptyDeclaration EmptyDeclaration}
 *
 * </pre>
 */
public interface ASTTopLevelDeclaration extends JavaNode {

}
