/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTJspScriptlet extends AbstractJspNode {
    @InternalApi
    @Deprecated
    public ASTJspScriptlet(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTJspScriptlet(JspParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
