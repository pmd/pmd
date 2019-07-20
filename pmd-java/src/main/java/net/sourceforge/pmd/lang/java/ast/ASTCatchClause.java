/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.ArrayList;
import java.util.List;


/**
 * A "catch" clause of a {@linkplain ASTTryStatement try statement}.
 *
 * <pre class="grammar">
 *
 * CatchClause ::= "catch" "(" {@link ASTFormalParameter FormalParameter} ")" {@link ASTBlock Block}
 *
 * </pre>
 */
public final class ASTCatchClause extends AbstractJavaNode {
    ASTCatchClause(int id) {
        super(id);
    }

    ASTCatchClause(JavaParser p, int id) {
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
     * Returns true if this node is a multi-catch statement,
     * that is, it catches several unrelated exception types
     * at the same time. Such a block can be declared like the
     * following for example:
     *
     * <p>{@code catch (IllegalStateException | IllegalArgumentException e) {}}
     *
     * @return True if this node is a multi-catch statement
     */
    public boolean isMulticatchStatement() {
        return getFormal().isMultiCatch();
    }

    /**
     * Returns the {@linkplain ASTCatchParameter CatchParameter} node.
     */
    public ASTCatchParameter getFormal() {
        return (ASTCatchParameter) jjtGetChild(0);
    }

    /**
     * Returns the ID of the declared variable.
     */
    public ASTVariableDeclaratorId getVariableId() {
        return getFormal().getVariableId();
    }

    /**
     * Returns the Block node of this catch branch.
     */
    public ASTBlock getBlock() {
        return (ASTBlock) getLastChild();
    }

    /**
     * Returns the list of type nodes denoting the exception types
     * caught by this catch block. The returned list has at least
     * one element.
     */
    public List<ASTType> getCaughtExceptionTypeNodes() {
        // maybe cache the list
        return getFormal().getTypeNode().asList();
    }


    /**
     * Returns the list of exception types caught by this catch block.
     * Any of these can be null, if they couldn't be resolved. This can
     * happen if the auxclasspath is not correctly set.
     */
    @SuppressWarnings("unchecked")
    public List<Class<? extends Exception>> getCaughtExceptionTypes() {
        List<Class<? extends Exception>> result = new ArrayList<>();
        for (ASTType type : getCaughtExceptionTypeNodes()) {
            result.add((Class<? extends Exception>) type.getType());
        }
        return result;
    }

    /**
     * Returns exception name caught by this catch block.
     */
    public String getExceptionName() {
        return getVariableId().getVariableName();
    }

}
