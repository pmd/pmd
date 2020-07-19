/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A "catch" clause of a {@linkplain ASTTryStatement try statement}.
 *
 * <pre class="grammar">
 *
 * CatchClause ::= "catch" "(" {@link ASTCatchParameter CatchParameter} ")" {@link ASTBlock Block}
 *
 * </pre>
 */
public final class ASTCatchClause extends AbstractJavaNode {
    ASTCatchClause(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /** Returns the catch parameter. */
    public ASTCatchParameter getParameter() {
        return (ASTCatchParameter) getFirstChild();
    }


    /** Returns the body of this catch branch. */
    public ASTBlock getBody() {
        return getFirstChildOfType(ASTBlock.class);
    }

}
