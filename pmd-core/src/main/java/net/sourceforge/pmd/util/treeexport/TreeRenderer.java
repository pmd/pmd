/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.io.IOException;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * An object that can export a tree to an external text format.
 *
 * @see XmlTreeRenderer
 */
@Experimental
public interface TreeRenderer {


    /**
     * Appends the subtree rooted at the given node on the provided
     * output writer. The implementation is free to filter out some
     * nodes from the subtree.
     *
     * @param node Node to render
     * @param out  Object onto which the output is appended
     *
     * @throws IOException If an IO error occurs while appending to the output
     */
    void renderSubtree(Node node, Appendable out) throws IOException;


}
