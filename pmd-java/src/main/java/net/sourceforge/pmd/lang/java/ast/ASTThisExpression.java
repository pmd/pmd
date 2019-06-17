/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * The "this" expression. Related to the {@link ASTSuperExpression "super"} pseudo-expression.
 *
 * <pre class="grammar">
 *
 * ThisExpression ::= "this"
 *                  | {@link ASTClassOrInterfaceType TypeName} "." "this"
 *
 * </pre>
 */
public final class ASTThisExpression extends AbstractJavaTypeNode implements ASTPrimaryExpression, LeftRecursiveNode {

    ASTThisExpression(int id) {
        super(id);
    }


    ASTThisExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Nullable
    public ASTClassOrInterfaceType getQualifier() {
        return jjtGetNumChildren() > 0 ? (ASTClassOrInterfaceType) jjtGetChild(0) : null;
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


}
