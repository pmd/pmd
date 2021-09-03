/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeNode;

public interface VmNode extends JjtreeNode<VmNode> {

    /**
     * Accept the visitor.
     *
     * @deprecated Use {@link #acceptVisitor(AstVisitor, Object)}
     */
    @Deprecated
    @DeprecatedUntil700
    default Object jjtAccept(VmParserVisitor visitor, Object data) {
        return acceptVisitor(visitor, data);
    }

}
