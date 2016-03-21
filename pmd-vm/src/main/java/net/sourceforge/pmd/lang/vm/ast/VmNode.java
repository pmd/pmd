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
     */
    Object childrenAccept(VmParserVisitor visitor, Object data);

}
