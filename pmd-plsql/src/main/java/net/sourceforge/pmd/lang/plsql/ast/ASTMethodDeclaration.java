/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTMethodDeclaration extends AbstractPLSQLNode implements ExecutableCode {
    @Deprecated
    @InternalApi
    public ASTMethodDeclaration(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTMethodDeclaration(PLSQLParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Gets the name of the method.
     *
     * @return a String representing the name of the method
     */
    @Override
    public String getMethodName() {
        ASTMethodDeclarator md = getFirstDescendantOfType(ASTMethodDeclarator.class);
        if (md != null) {
            return md.getImage();
        }
        return null;
    }
}
