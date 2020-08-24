/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTOutOfLineConstraint extends AbstractPLSQLNode {
    private ConstraintType type;

    ASTOutOfLineConstraint(int id) {
        super(id);
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

    public boolean isForeignKey() {
        return type == ConstraintType.FOREIGN;
    }

    public boolean isCheck() {
        return type == ConstraintType.CHECK;
    }

    @Override
    protected <P, R> R acceptPlsqlVisitor(PLSQLVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
