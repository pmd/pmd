/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.impl.GenericNode;

/**
 * Root interface implemented by all Apex nodes. Apex nodes wrap a tree
 * obtained from an external parser.
 *
 * @param <T> Type of the underlying Summit AST node (or Void)
 */
public interface ApexNode<T> extends GenericNode<ApexNode<?>> {

    boolean hasRealLoc();


    String getDefiningType();


    @Override
    @NonNull ASTApexFile getRoot();
}
