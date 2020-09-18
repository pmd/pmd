/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * The "finally" clause of a {@linkplain ASTTryStatement try statement}.
 *
 *
 * <pre class="grammar">
 *
 * FinallyClause ::= "finally" {@link ASTBlock Block}
 *
 * </pre>
 */
public final class ASTFinallyClause extends AbstractJavaNode {

    ASTFinallyClause(int id) {
        super(id);
    }


    @Override
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the body of this finally clause.
     */
    public ASTBlock getBody() {
        return (ASTBlock) getChild(0);
    }
}
