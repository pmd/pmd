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
 * @param <T> placeholder
 */
public interface ApexNode<T> extends Node extends GenericNode<ApexNode<?>> {

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


    @Override
    Iterable<? extends ApexNode<?>> children();


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
