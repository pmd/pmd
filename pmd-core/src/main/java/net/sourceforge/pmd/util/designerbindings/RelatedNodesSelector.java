/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.designerbindings;

import java.util.List;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Provides a way for the designer to highlight related nodes upon selection,
 * eg those nodes referring to the same variable or method.
 *
 * <p>This API only published to bind to the designer for now, it should
 * not be used by rule code. The criterion for selecting nodes to highlight
 * is subject to change at the implementation's discretion. In particular it's
 * not necessarily the usages of a variable.
 *
 * <p>The binary API is <b>unstable</b> until at least 7.0, meaning the only
 * place this can be implemented safely is within PMD's own codebase.
 *
 * @author Clément Fournier
 * @since 6.20.0
 */
@Experimental
public interface RelatedNodesSelector {


    /**
     * Returns a list of nodes that should be highlighted when selecting
     * the given node. This is eg the nodes that correspond to usages of
     * a variable declared by the given node. If the node cannot be handled
     * by this resolver or is otherwise uninteresting, then returns an empty list.
     *
     * @param node A node, with no guarantee about type. Implementations
     *             should check their casts.
     *
     * @return A list of nodes to highlight
     */
    List<Node> getHighlightedNodesWhenSelecting(Node node);


}
