/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public class ASTInlineConstraint extends AbstractPLSQLNode {
    private ConstraintType type;

    ASTInlineConstraint(int id) {
        super(id);
    }

    ASTInlineConstraint(PLSQLParser p, int id) {
        super(p, id);
    }

    void setType(ConstraintType type) {
        this.type = type;
    }

    public ConstraintType getType() {
        return type;
    }

    public boolean isUnique() {
        return type == ConstraintType.UNIQUE;
    }

    public boolean isPrimaryKey() {
        return type == ConstraintType.PRIMARY;
    }

    public boolean isCheck() {
        return type == ConstraintType.CHECK;
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
