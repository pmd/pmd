/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTTriggerTimingPointSection extends AbstractPLSQLNode implements ExecutableCode {

    ASTTriggerTimingPointSection(int id) {
        super(id);
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * return executable's name.
     *
     * @return
     */
    @Override
    public String getMethodName() {
        return getImage();
    }

    public String getName() {
        return getMethodName();
    }
}
