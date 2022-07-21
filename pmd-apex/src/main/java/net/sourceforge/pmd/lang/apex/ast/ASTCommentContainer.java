/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.Experimental;

/**
 * Interface for nodes that can contain comments. Because comments are for the most part lost, the tree builder only
 * captures whether the node did contain comments of any sort in the source code and not the actual contents of those
 * comments. This is useful for rules which need to know whether a node did contain comments.
 */
@Experimental
public interface ASTCommentContainer<T extends Node> extends ApexNode<T> {

    boolean getContainsComment();
}
