/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

public final class ASTExtractExpression extends AbstractPLSQLNode {
    private boolean xml;

    ASTExtractExpression(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptPlsqlVisitor(PlsqlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    void setXml() {
        xml = true;
    }

    public boolean isXml() {
        return xml;
    }

    public String getXPath() {
        if (xml) {
            return firstChild(ASTStringLiteral.class).getString();
        }
        return "";
    }

    public String getNamespace() {
        if (xml) {
            List<ASTStringLiteral> literals = children(ASTStringLiteral.class).toList();
            if (literals.size() == 2) {
                return literals.get(1).getString();
            }
        }
        return "";
    }
}
