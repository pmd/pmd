/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTJspDirectiveAttribute extends AbstractJspNode {

    private String name;
    private String value;

    @InternalApi
    @Deprecated
    public ASTJspDirectiveAttribute(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTJspDirectiveAttribute(JspParser p, int id) {
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

    public String getValue() {
        return value;
    }

    @InternalApi
    @Deprecated
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
