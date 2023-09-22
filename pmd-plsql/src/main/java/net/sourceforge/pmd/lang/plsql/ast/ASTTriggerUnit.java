/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTTriggerUnit extends AbstractPLSQLNode implements ExecutableCode, OracleObject {

    ASTTriggerUnit(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptPlsqlVisitor(PlsqlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Gets the name of the trigger.
     *
     * @return a String representing the name of the trigger
     */
    @Override
    public String getMethodName() {
        return getImage();
    }

    public String getName() {
        return getMethodName();
    }

    /**
     * Gets the name of the Oracle Object.
     *
     * @return a String representing the name of the Oracle Object
     */
    @Override
    public String getObjectName() {
        return getImage();
    }
}
