/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

public final class ASTJspScriptlet extends AbstractContentNode {

    ASTJspScriptlet(int id) {
        super(id);
    }

    @Override
    public <P, R> R acceptVisitor(JspVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
