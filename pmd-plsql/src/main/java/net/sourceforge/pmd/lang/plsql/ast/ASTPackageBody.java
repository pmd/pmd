/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public class ASTPackageBody extends AbstractPLSQLNode implements OracleObject {

    ASTPackageBody(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptPlsqlVisitor(PlsqlVisitor<? super P, ? extends R> visitor, P data) {
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
