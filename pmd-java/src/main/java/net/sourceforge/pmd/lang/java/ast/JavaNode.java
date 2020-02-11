/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeNode;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;


/**
 * Root interface for all Nodes of the Java AST.
 */
public interface JavaNode extends JjtreeNode<JavaNode>, ScopedNode {

    /**
     * Calls back the visitor's visit method corresponding to the runtime type of this Node.
     *
     * @param visitor Visitor to dispatch
     * @param data    Visit data
     *
     * @deprecated Use {@link #acceptVisitor(AstVisitor, Object)}
     */
    @Deprecated
    @DeprecatedUntil700
    default Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return acceptVisitor(visitor, data);
    }


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
     * {@linkplain ASTAnyTypeDeclaration AnyTypeDeclaration}s.
     */
    default ASTAnyTypeDeclaration getEnclosingType() {
        return getFirstParentOfType(ASTAnyTypeDeclaration.class);
    }



    /**
     * FIXME figure that out
     */
    Comment comment();


    @Override
    @NonNull ASTCompilationUnit getRoot();

    /**
     * Returns the symbol table for the program point represented by
     * this node.
     */
    @NonNull
    JSymbolTable getSymbolTable();

    TypeSystem getTypeSystem();

}
