/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeNode;

public interface JspNode extends JjtreeNode<JspNode> {

    /**
     * @deprecated Use {@link #acceptVisitor(AstVisitor, Object)}
     */
    @Deprecated
    @DeprecatedUntil700
    default Object jjtAccept(JspParserVisitor visitor, Object data) {
        return acceptVisitor(visitor, data);
    }

}
