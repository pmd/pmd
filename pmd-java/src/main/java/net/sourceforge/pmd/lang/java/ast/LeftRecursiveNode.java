/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Marker interface for left-recursive nodes. Those nodes are injected
 * with children after jjtOpen is called, which means their text bounds
 * need to be adapted in {@link AbstractJavaNode#jjtClose()}.
 *
 * <p>This is only relevant to node construction and is package private.
 *
 * @author Clément Fournier
 */
interface LeftRecursiveNode {
    // no methods should ever be added
}
