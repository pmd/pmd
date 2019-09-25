/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.IteratorUtil;


/**
 * Try statement node.
 *
 *
 * <pre class="grammar">
 *
 * TryStatement ::= "try" {@link ASTResourceList ResourceList}?
 *                  {@link ASTBlock Block}
 *                  {@link ASTCatchStatement CatchStatement}*
 *                  {@link ASTFinallyStatement FinallyStatement}?
 *
 * </pre>
 */
public final class ASTTryStatement extends AbstractJavaNode {

    ASTTryStatement(int id) {
        super(id);
    }

    ASTTryStatement(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    /**
     * Returns true if this node is a try-with-resources, in which case it
     * has a ResourceSpecification child node.
     */
    public boolean isTryWithResources() {
        return jjtGetChild(0) instanceof ASTResourceList;
    }

    @Nullable
    public ASTResourceList getResourceList() {
        return AstImplUtil.getChildAs(this, 0, ASTResourceList.class);
    }

    public List<ASTResource> getResources() {
        ASTResourceList list = getResourceList();
        return list == null ? Collections.emptyList() : IteratorUtil.toList(list.iterator());
    }


    /**
     * Returns the catch statement nodes of this try statement.
     * If there are none, returns an empty list.
     */
    public List<ASTCatchStatement> getCatchStatements() {
        return findChildrenOfType(ASTCatchStatement.class);
    }


    /**
     * Returns true if this try statement has a  {@code finally} statement,
     * in which case {@link #getFinally()} won't return {@code null}.
     */
    public boolean hasFinally() {
        return getFirstChildOfType(ASTFinallyStatement.class) != null;
    }


    /**
     * Returns the {@code finally} statement of this try statement, if any.
     *
     * @return The finally statement, or null if there is none
     */
    public ASTFinallyStatement getFinally() {
        return getFirstChildOfType(ASTFinallyStatement.class);
    }

}
