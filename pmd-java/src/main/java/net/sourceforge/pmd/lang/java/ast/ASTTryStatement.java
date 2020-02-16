/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Try statement node.
 * <pre>
 * TryStatement ::= "try" ( ResourceSpecification )? Block ( CatchStatement )* [ FinallyStatement ]
 * </pre>
 */
public class ASTTryStatement extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTTryStatement(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTTryStatement(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns true if this node is a try-with-resources, in which case it
     * has a ResourceSpecification child node.
     */
    public boolean isTryWithResources() {
        return getFirstChildOfType(ASTResourceSpecification.class) != null;
    }


    /**
     * Returns the body of this try statement.
     */
    public ASTBlock getBody() {
        return (ASTBlock) getChild(1);
    }

    /**
     * Returns the catch statement nodes of this try statement.
     * If there are none, returns an empty list.
     *
     * @deprecated Use {@link #getCatchClauses()}
     */
    @Deprecated
    public List<ASTCatchStatement> getCatchStatements() {
        return findChildrenOfType(ASTCatchStatement.class);
    }

    /**
     * Returns the catch clauses of this try statement.
     * If there are none, returns an empty list.
     */
    public List<ASTCatchStatement> getCatchClauses() {
        return findChildrenOfType(ASTCatchStatement.class);
    }


    /**
     * Returns true if this try statement has a  {@code finally} statement,
     * in which case {@link #getFinallyClause()} won't return {@code null}.
     *
     * @deprecated Check for nullity of {@link #getFinallyClause()}
     */
    @Deprecated
    public boolean hasFinally() {
        return getFirstChildOfType(ASTFinallyStatement.class) != null;
    }


    /**
     * Returns the {@code finally} statement of this try statement, if any.
     *
     * @return The finally statement, or null if there is none
     *
     * @deprecated Use {@link #getFinallyClause()}
     */
    public ASTFinallyStatement getFinally() {
        return getFirstChildOfType(ASTFinallyStatement.class);
    }

    /**
     * Returns the {@code finally} clause of this try statement, if any.
     *
     * @return The finally statement, or null if there is none
     */
    public ASTFinallyStatement getFinallyClause() {
        return getFirstChildOfType(ASTFinallyStatement.class);
    }

}
