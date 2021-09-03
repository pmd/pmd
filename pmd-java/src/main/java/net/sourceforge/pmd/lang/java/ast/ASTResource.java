/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * A resource of a {@linkplain ASTTryStatement try-with-resources}. This contains another
 * node that represents the resource, according to the grammar below.
 *
 * <p>In the case of concise try-with resources, the subexpressions are
 * required to be only field accesses or variable references to compile.
 *
 * <pre class="grammar">
 *
 * Resource ::= {@link ASTLocalVariableDeclaration LocalVariableDeclaration}
 *            | {@link ASTPrimaryExpression PrimaryExpression}
 *
 * </pre>
 */
public final class ASTResource extends AbstractJavaNode {

    ASTResource(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns true if this appears as an expression, and not as a
     * local variable declaration.
     */
    public boolean isConciseResource() {
        return getChild(0) instanceof ASTExpression;
    }

    /**
     * Gets the name with which the resource can be accessed in the
     * body of the try statement. If this is a {@linkplain #isConciseResource() concise resource},
     * then returns the sequence of names that identifies the expression.
     * If this has a local variable declaration, then returns the name
     * of the variable.
     */
    public String getStableName() {
        if (isConciseResource()) {
            ASTExpression expr = getInitializer();
            StringBuilder builder = new StringBuilder();
            while (expr instanceof ASTFieldAccess) {
                ASTFieldAccess fa = (ASTFieldAccess) expr;
                builder.insert(0, "." + fa.getName());
                expr = fa.getQualifier();
            }
            // the last one may be ambiguous, or a variable reference
            // the only common interface we have to get their name is
            // unfortunately Node::getImage

            if (expr != null) {
                builder.insert(0, expr.getImage());
            }
            return builder.toString();
        } else {
            return asLocalVariableDeclaration().iterator().next().getName();
        }
    }

    @Nullable
    public ASTLocalVariableDeclaration asLocalVariableDeclaration() {
        return AstImplUtil.getChildAs(this, 0, ASTLocalVariableDeclaration.class);
    }


    /**
     * Returns the initializer of the expression.
     * If this is a concise resource, then returns that expression.
     * If this is a local variable declaration, returns the initializer of
     * the variable.
     */
    public ASTExpression getInitializer() {
        Node c = getChild(0);
        if (c instanceof ASTExpression) {
            return (ASTExpression) c;
        } else {
            return ((ASTLocalVariableDeclaration) c).iterator().next().getInitializer();
        }
    }

}
