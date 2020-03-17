/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTJspDirective extends AbstractJspNode {

    /**
     * Name of the element-tag. Cannot be null.
     */
    private String name;

    @InternalApi
    @Deprecated
    public ASTJspDirective(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTJspDirective(JspParser p, int id) {
        super(p, id);
    }

    public String getName() {
        return name;
    }

    @InternalApi
    @Deprecated
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
