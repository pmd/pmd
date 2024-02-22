/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.impl.GenericNode;

import apex.jorje.semantic.ast.AstNode;

/**
 * Root interface implemented by all Apex nodes. Apex nodes wrap a tree
 * obtained from an external parser (Jorje).
 *
 * @param <T> Type of the underlying Jorje node
 */
public interface ApexNode<T extends AstNode> extends GenericNode<ApexNode<?>> {
    boolean hasRealLoc();


    String getDefiningType();


    String getNamespace();


    @Override
    @NonNull ASTApexFile getRoot();
}
