/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.ast;

import net.sourceforge.pmd.lang.ast.Node;

public interface VmNode extends Node {
    /**
     * Accept the visitor. *
     */
    Object jjtAccept(VmParserVisitor visitor, Object data);


    /**
     * Accept the visitor. *
     *
     * @deprecated This method is not useful, the logic for combining
     *     children values should be present on the visitor, not the node
     */
    @Deprecated
    Object childrenAccept(VmParserVisitor visitor, Object data);


    @Override
    VmNode getChild(int index);


    @Override
    VmNode getParent();


    @Override
    Iterable<? extends VmNode> children();

}
