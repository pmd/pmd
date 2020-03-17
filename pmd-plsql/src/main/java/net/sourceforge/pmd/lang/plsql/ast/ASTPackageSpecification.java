/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTPackageSpecification extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode
        implements OracleObject {
    @Deprecated
    @InternalApi
    public ASTPackageSpecification(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTPackageSpecification(PLSQLParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Gets the name of the Oracle Object.
     *
     * @return a String representing the name of the Oracle Object
     */
    @Override
    public String getObjectName() {
        return this.getImage();
    }
}
