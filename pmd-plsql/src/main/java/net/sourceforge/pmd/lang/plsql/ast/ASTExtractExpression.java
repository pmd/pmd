/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;

public final class ASTExtractExpression extends AbstractPLSQLNode {

    private boolean xml;

    @InternalApi
    @Deprecated
    public ASTExtractExpression(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTExtractExpression(PLSQLParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
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
            return getFirstChildOfType(ASTStringLiteral.class).getString();
        }
        return "";
    }

    public String getNamespace() {
        if (xml) {
            List<ASTStringLiteral> literals = findChildrenOfType(ASTStringLiteral.class);
            if (literals.size() == 2) {
                return literals.get(1).getString();
            }
        }
        return "";
    }
}
