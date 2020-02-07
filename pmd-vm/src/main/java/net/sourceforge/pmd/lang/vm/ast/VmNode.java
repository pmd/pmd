/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.ast;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.TextAvailableNode;

public interface VmNode extends TextAvailableNode {
    /**
     * Accept the visitor. *
     */
    Object jjtAccept(VmParserVisitor visitor, Object data);


    @Override
    VmNode getChild(int index);

    @Override
    VmNode getParent();

    @Override
    NodeStream<? extends VmNode> children();

}
