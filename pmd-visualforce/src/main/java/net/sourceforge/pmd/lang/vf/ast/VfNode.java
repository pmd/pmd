/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeNode;

public interface VfNode extends JjtreeNode<VfNode> {

    /**
     * Accept the visitor.
     */
    Object jjtAccept(VfParserVisitor visitor, Object data);

}
