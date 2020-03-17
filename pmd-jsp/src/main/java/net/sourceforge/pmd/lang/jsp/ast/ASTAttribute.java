/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTAttribute extends AbstractJspNode {

    private String name;

    @InternalApi
    @Deprecated
    public ASTAttribute(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTAttribute(JspParser p, int id) {
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

    /**
     * @return boolean - true if the element has a namespace-prefix, false
     *         otherwise
     */
    public boolean isHasNamespacePrefix() {
        return name.indexOf(':') >= 0;
    }

    /**
     * @return String - the part of the name that is before the (first) colon
     *         (":")
     */
    public String getNamespacePrefix() {
        int colonIndex = name.indexOf(':');
        return colonIndex >= 0 ? name.substring(0, colonIndex) : "";
    }

    /**
     * @return String - The part of the name that is after the first colon
     *         (":"). If the name does not contain a colon, the full name is
     *         returned.
     */
    public String getLocalName() {
        int colonIndex = name.indexOf(':');
        return colonIndex >= 0 ? name.substring(colonIndex + 1) : name;
    }

    @Override
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
