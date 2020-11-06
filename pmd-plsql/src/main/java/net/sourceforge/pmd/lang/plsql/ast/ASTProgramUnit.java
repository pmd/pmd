/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.dfa.DFAGraphMethod;

public class ASTProgramUnit extends AbstractPLSQLNode implements ExecutableCode, OracleObject, DFAGraphMethod {
    @Deprecated
    @InternalApi
    public ASTProgramUnit(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTProgramUnit(PLSQLParser p, int id) {
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
        ASTMethodDeclarator md = getFirstChildOfType(ASTMethodDeclarator.class);
        if (md != null) {
            return md.getImage();
        }
        return null;
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
        // This _IS_ a schema-level Program Unit
        if (null == this.getParent()) {
            return this.getImage();
        } else {
            return this.getImage();
        }
    }
}
