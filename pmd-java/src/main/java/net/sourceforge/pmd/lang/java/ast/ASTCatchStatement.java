/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Catch statement node.
 * <pre>
 *      "catch" "(" FormalParameter ")" Block
 * </pre>
 */
public class ASTCatchStatement extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTCatchStatement(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTCatchStatement(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
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
        return getCaughtExceptionTypeNodes().size() > 1; // the list is parsed multiple times...
    }


    /**
     * Returns the Block node of this catch branch.
     *
     * @deprecated Use {@link #getBody()}
     */
    @Deprecated
    public ASTBlock getBlock() {
        return getFirstChildOfType(ASTBlock.class);
    }


    /**
     * Returns the body of this catch clause.
     */
    public ASTBlock getBody() {
        return getFirstChildOfType(ASTBlock.class);
    }

    /**
     * Returns the list of type nodes denoting the exception types
     * caught by this catch block. The returned list has at least
     * one element.
     */
    public List<ASTType> getCaughtExceptionTypeNodes() {
        // maybe cache the list
        return getFirstChildOfType(ASTFormalParameter.class).findChildrenOfType(ASTType.class);
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
        return getFirstDescendantOfType(ASTVariableDeclaratorId.class).getImage();
    }

}
