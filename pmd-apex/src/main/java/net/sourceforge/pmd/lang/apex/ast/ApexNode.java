/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;

/**
 * Root interface implemented by all Apex nodes. Apex nodes wrap a tree
 * obtained from an external parser.
 *
 * @param <T> Type of the underlying Summit AST node (or Void)
 */
public interface ApexNode<T> extends GenericNode<ApexNode<?>> {

    /**
     * Accept the visitor.
     *
     * @deprecated Use {@link #acceptVisitor(AstVisitor, Object)}
     */
    @Deprecated
    @DeprecatedUntil700
    default Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return acceptVisitor(visitor, data);
    }


    boolean hasRealLoc();


    String getDefiningType();


    String getNamespace();


    @Override
    @NonNull ASTApexFile getRoot();
}
