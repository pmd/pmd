/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Classes of this interface must be used when it is desired to make one or more operations to the AST.
 */
public interface RuleViolationFix {

    /**
     * Apply one or more operations to a node.
     * @param node the node in the AST on which to apply operations
     */
    void applyFixesToNode(Node node);
}
