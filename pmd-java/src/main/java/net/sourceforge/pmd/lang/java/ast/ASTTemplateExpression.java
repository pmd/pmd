/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;

/**
 * This is a Java 21 Preview feature.
 *
 * <pre class="grammar">
 *
 * TemplateExpression ::= ({@link ASTVariableAccess VariableAccess} | {@link ASTFieldAccess FieldAccess})
 *                        ({@link ASTTemplate Template} | {@link ASTStringLiteral StringLiteral})
 *
 * </pre>
 *
 * @see <a href="https://openjdk.org/jeps/430">JEP 430: String Templates (Preview)</a>
 */
@Experimental
public final class ASTTemplateExpression extends AbstractJavaExpr {
    ASTTemplateExpression(int i) {
        super(i);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ASTExpression getTemplateProcessor() {
        return (ASTExpression) getChild(0);
    }

    public JavaNode getTemplateArgument() {
        return getChild(1);
    }

    public boolean isStringTemplate() {
        String name;
        if (getTemplateProcessor() instanceof ASTNamedReferenceExpr) {
            name = ((ASTNamedReferenceExpr) getTemplateProcessor()).getName();
        } else {
            name = getTemplateProcessor().getFirstToken().getImage();
        }
        return "STR".equals(name);
    }
}
