/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeNode;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.types.TypeSystem;


/**
 * Root interface for all Nodes of the Java AST.
 */
public interface JavaNode extends JjtreeNode<JavaNode> {

    /**
     * Returns the node representing the type declaration this node is
     * found in. The type of that node is the type of the {@code this}
     * expression.
     *
     * <p>This returns null for nodes that aren't enclosed in a type declaration.
     * This includes {@linkplain ASTPackageDeclaration PackageDeclaration},
     * This includes {@linkplain ASTImportDeclaration ImportDeclaration},
     * {@linkplain ASTModuleDeclaration ModuleDeclaration},
     * {@linkplain ASTCompilationUnit CompilationUnit}, and top-level
     * {@linkplain ASTTypeDeclaration TypeDeclaration}s.
     */
    default ASTTypeDeclaration getEnclosingType() {
        return ancestors(ASTTypeDeclaration.class).first();
    }


    @Override
    @NonNull ASTCompilationUnit getRoot();

    /**
     * Returns the symbol table for the program point represented by
     * this node.
     */
    @NonNull
    JSymbolTable getSymbolTable();

    /**
     * Returns the type system with which this node was created. This is
     * the object responsible for representing types in the compilation
     * unit.
     */
    TypeSystem getTypeSystem();

}
