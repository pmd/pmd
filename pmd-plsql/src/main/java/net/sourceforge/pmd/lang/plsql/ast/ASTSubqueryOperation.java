/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTSubqueryOperation extends AbstractPLSQLNode {
    private boolean union;
    private boolean all;
    private boolean intersect;
    private boolean minus;

    ASTSubqueryOperation(int id) {
        super(id);
    }

    public boolean isAll() {
        return all;
    }

    void setAll(boolean all) {
        this.all = all;
    }

    public boolean isIntersect() {
        return intersect;
    }

    void setIntersect(boolean intersect) {
        this.intersect = intersect;
    }

    public boolean isMinus() {
        return minus;
    }

    void setMinus(boolean minus) {
        this.minus = minus;
    }

    public boolean isUnion() {
        return union;
    }

    void setUnion(boolean union) {
        this.union = union;
    }

    @Override
    protected <P, R> R acceptPlsqlVisitor(PLSQLVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
