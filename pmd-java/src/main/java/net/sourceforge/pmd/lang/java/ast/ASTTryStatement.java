/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;


/**
 * Try statement node.
 *
 *
 * <pre class="grammar">
 *
 * TryStatement ::= "try" {@link ASTResourceList ResourceList}?
 *                  {@link ASTBlock Block}
 *                  {@link ASTCatchClause CatchClause}*
 *                  {@link ASTFinallyClause FinallyClause}?
 *
 * </pre>
 */
public final class ASTTryStatement extends AbstractStatement {

    ASTTryStatement(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns true if this node is a try-with-resources, in which case it
     * has a ResourceSpecification child node.
     */
    public boolean isTryWithResources() {
        return getChild(0) instanceof ASTResourceList;
    }

    /**
     * Returns the node for the resource list. This is null if this is
     * not a try-with-resources.
     */
    @Nullable
    public ASTResourceList getResources() {
        return AstImplUtil.getChildAs(this, 0, ASTResourceList.class);
    }


    /**
     * Returns the body of this try statement.
     */
    public ASTBlock getBody() {
        return children(ASTBlock.class).first();
    }

    /**
     * Returns the catch statement nodes of this try statement.
     * If there are none, returns an empty list.
     */
    public NodeStream<ASTCatchClause> getCatchClauses() {
        return children(ASTCatchClause.class);
    }


    /**
     * Returns the {@code finally} clause of this try statement, if any.
     *
     * @return The finally statement, or null if there is none
     */
    @Nullable
    public ASTFinallyClause getFinallyClause() {
        return getFirstChildOfType(ASTFinallyClause.class);
    }

}
