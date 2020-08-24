/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTFormalParameter extends AbstractPLSQLNode {

    private boolean in;
    private boolean out;
    private boolean nocopy;

    ASTFormalParameter(int id) {
        super(id);
    }

    public boolean isIn() {
        return this.in;
    }

    void setIn(boolean in) {
        this.in = in;
    }

    public boolean isOut() {
        return this.out;
    }

    void setOut(boolean out) {
        this.out = out;
    }

    public boolean isNoCopy() {
        return this.nocopy;
    }

    void setNoCopy(boolean nocopy) {
        this.nocopy = nocopy;
    }

    @Override
    protected <P, R> R acceptPlsqlVisitor(PLSQLVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ASTDatatype getTypeNode() {
        for (int i = 0; i < getNumChildren(); i++) {
            if (getChild(i) instanceof ASTDatatype) {
                return (ASTDatatype) getChild(i);
            }
        }
        throw new IllegalStateException("ASTType not found");
    }
}
