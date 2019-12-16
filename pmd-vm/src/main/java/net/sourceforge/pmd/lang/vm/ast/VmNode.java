/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.ast;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.TokenBasedNode;

public interface VmNode extends Node, TokenBasedNode<Token> {
    /**
     * Accept the visitor. *
     */
    Object jjtAccept(VmParserVisitor visitor, Object data);

    /**
     * Accept the visitor. *
     */
    Object childrenAccept(VmParserVisitor visitor, Object data);

}
