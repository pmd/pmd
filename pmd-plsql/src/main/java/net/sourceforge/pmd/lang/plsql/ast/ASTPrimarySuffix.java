/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTPrimarySuffix extends AbstractPLSQLNode {
    private boolean isArguments;
    private boolean isArrayDereference;

    ASTPrimarySuffix(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptPlsqlVisitor(PlsqlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    void setIsArrayDereference() {
        isArrayDereference = true;
    }

    public boolean isArrayDereference() {
        return isArrayDereference;
    }

    void setIsArguments() {
        this.isArguments = true;
    }

    public boolean isArguments() {
        return this.isArguments;
    }

    /**
     * Get the number of arguments for this primary suffix. One should call
     * {@link #isArguments()} to see if there are arguments. If this method is
     * called when there are no arguments it returns <code>-1</code>.
     *
     * @return A non-negative argument number when there are arguments,
     *         <code>-1</code> otherwise.
     */
    public int getArgumentCount() {
        if (!this.isArguments()) {
            return -1;
        }
        return ((ASTArguments) getChild(getNumChildren() - 1)).getArgumentCount();
    }
}
