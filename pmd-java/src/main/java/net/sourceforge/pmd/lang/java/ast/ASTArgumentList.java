/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;


/**
 * The argument list of a {@linkplain ASTMethodCall method}, {@linkplain ASTConstructorCall constructor call},
 * or {@linkplain ASTExplicitConstructorInvocation explicit constructor invocation}.
 *
 * <pre class="grammar">
 *
 * ArgumentList ::= "(" ( {@link ASTExpression Expression} ( "," {@link ASTExpression Expression})* )? ")"
 *
 * </pre>
 */
public final class ASTArgumentList extends AbstractJavaNode implements Iterable<ASTExpression> {

    ASTArgumentList(int id) {
        super(id);
    }

    ASTArgumentList(JavaParser p, int id) {
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
     * Returns the number of arguments of this list.
     */
    public int getArgumentCount() {
        return jjtGetNumChildren();
    }

    @Override
    public Iterator<ASTExpression> iterator() {
        return new NodeChildrenIterator<>(this, ASTExpression.class);
    }
}
