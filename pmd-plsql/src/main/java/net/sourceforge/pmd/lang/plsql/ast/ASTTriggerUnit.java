/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.dfa.DFAGraphMethod;

public class ASTTriggerUnit extends AbstractPLSQLNode implements ExecutableCode, OracleObject, DFAGraphMethod {
    @Deprecated
    @InternalApi
    public ASTTriggerUnit(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTTriggerUnit(PLSQLParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
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

    @Override
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
