/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTOuterJoinType extends AbstractPLSQLNode {
    private Type type;

    ASTOuterJoinType(int id) {
        super(id);
    }

    void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String getImage() {
        return String.valueOf(type);
    }

    public boolean isLeft() {
        return type == Type.LEFT;
    }

    public boolean isRight() {
        return type == Type.RIGHT;
    }

    public boolean isFull() {
        return type == Type.FULL;
    }

    @Override
    protected <P, R> R acceptPlsqlVisitor(PLSQLVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public enum Type { FULL, LEFT, RIGHT }
}
