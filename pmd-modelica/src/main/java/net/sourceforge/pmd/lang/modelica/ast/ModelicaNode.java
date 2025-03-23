/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeNode;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaScope;

/**
 * Public interface for all Modelica AST nodes.
 */
public interface ModelicaNode extends JjtreeNode<ModelicaNode> {

    /**
     * Returns the lexical scope this node is contained in.
     */
    ModelicaScope getContainingScope();

    /**
     * Returns the most specific lexical scope naturally associated with this node.
     *
     * @return the scope defined by this node itself or the same as {@link #getContainingScope()} otherwise
     */
    ModelicaScope getMostSpecificScope();

}
