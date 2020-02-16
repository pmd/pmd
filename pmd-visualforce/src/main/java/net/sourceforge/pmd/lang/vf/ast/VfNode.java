/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.lang.ast.Node;

public interface VfNode extends Node {

    /**
     * Accept the visitor. *
     */
    Object jjtAccept(VfParserVisitor visitor, Object data);


    /**
     * Accept the visitor. *
     *
     * @deprecated This method is not useful, the logic for combining
     *     children values should be present on the visitor, not the node
     */
    @Deprecated
    Object childrenAccept(VfParserVisitor visitor, Object data);


    @Override
    VfNode getParent();


    @Override
    VfNode getChild(int i);



    @Override
    Iterable<? extends VfNode> children();
}
