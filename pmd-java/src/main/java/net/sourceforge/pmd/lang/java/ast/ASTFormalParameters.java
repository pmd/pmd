/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * A list of {@linkplain ASTFormalParameter formal parameters} in a
 * method or constructor declaration. Some formal parameter lists may
 * feature a {@linkplain ASTReceiverParameter receiver parameter}. That
 * is not treated as a regular formal parameter, as it does not declare
 * a variable.
 *
 * <pre class="grammar">
 *
 * FormalParameters ::=  "(" ")"
 *                    |  "(" ({@link ASTReceiverParameter ReceiverParameter} | {@link ASTFormalParameter FormalParameter}) ("," {@link ASTFormalParameter FormalParameter})* ")"
 *
 * </pre>
 *
 */
public final class ASTFormalParameters extends AbstractJavaNode implements Iterable<ASTFormalParameter> {

    @InternalApi
    @Deprecated
    public ASTFormalParameters(int id) {
        super(id);
    }

    ASTFormalParameters(JavaParser p, int id) {
        super(p, id);
    }

    /**
     * Returns the number of formal parameters in this parameter list.
     * This excludes the receiver parameter, if any.
     */
    public int getParameterCount() {
        return getFirstChild() instanceof ASTReceiverParameter ? jjtGetNumChildren() : jjtGetNumChildren() - 1;
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
     * Returns the receiver parameter if it is present, otherwise returns
     * null.
     */
    @Nullable
    public ASTReceiverParameter getReceiverParameter() {
        return AstImplUtil.getChildAs(this, 0, ASTReceiverParameter.class);
    }

    @Override
    public Iterator<ASTFormalParameter> iterator() {
        return new NodeChildrenIterator<>(this, ASTFormalParameter.class);
    }
}
