/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a type test on an object. This has a precedence greater
 * than equality expressions ({@link BinaryOp#EQ}, {@link BinaryOp#NE}),
 * and lower than shift expressions (e.g. {@link BinaryOp#RIGHT_SHIFT}).
 * This has the same precedence as relational expressions (e.g. {@link BinaryOp#LE}).
 *
 * TODO represent that with an InfixExpr too
 *
 * <pre class="grammar">
 *
 * InstanceOfExpression ::=  {@linkplain ASTExpression Expression} "instanceof" {@linkplain ASTType Type}
 *
 * </pre>
 */
public final class ASTInstanceOfExpression extends AbstractJavaExpr implements ASTExpression {

    ASTInstanceOfExpression(int id) {
        super(id);
    }


    ASTInstanceOfExpression(JavaParser p, int id) {
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

    /** Returns the expression whose type is being tested. */
    public ASTExpression getLhs() {
        return (ASTExpression) jjtGetChild(0);
    }

    /** Gets the type against which the expression is tested. */
    public ASTType getTypeNode() {
        return (ASTType) jjtGetChild(1);
    }

}
