/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.impl.GenericNode;

import apex.jorje.semantic.ast.AstNode;
import apex.jorje.services.Version;

/**
 * Root interface implemented by all Apex nodes. Apex nodes wrap a tree
 * obtained from an external parser (Jorje). The underlying AST node is
 * available with {@link #getNode()}.
 *
 * @param <T> Type of the underlying Jorje node
 */
public interface ApexRootNode<T extends AstNode> extends GenericNode<ApexRootNode<?>> {

    /**
     * Accept the visitor.
     */
    Object jjtAccept(ApexParserVisitor visitor, Object data);


    /**
     * Get the underlying AST node.
     * @deprecated the underlying AST node should not be available outside of the AST node.
     *      If information is needed from the underlying node, then PMD's AST node need to expose
     *      this information.
     */
    @Deprecated
    T getNode();


    boolean hasRealLoc();


    String getDefiningType();


    String getNamespace();


    @Override
    @NonNull ASTApexFile getRoot();

    /**
     * Gets the apex version this class has been compiled with.
     * Use {@link Version} to compare, e.g.
     * {@code node.getApexVersion() >= Version.V176.getExternal()}
     *
     * @return the apex version
     */
    default double getApexVersion() {
        return getRoot().getApexVersion();
    }
}
