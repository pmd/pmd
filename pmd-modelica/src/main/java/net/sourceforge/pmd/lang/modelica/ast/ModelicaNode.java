/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaScope;

/**
 * Public interface for all Modelica AST nodes.
 */
public interface ModelicaNode extends Node {

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

    Object jjtAccept(ModelicaParserVisitor visitor, Object data);

    @Override
    ModelicaNode getParent();


    @Override
    ModelicaNode getChild(int index);


    @Override
    Iterable<? extends ModelicaNode> children();
}
