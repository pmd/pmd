/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * The template of a {@link ASTTemplateExpression}. This is a Java 21/22 Preview feature.
 *
 * <pre class="grammar">
 *
 * Template ::= ({@link ASTTemplateFragment TemplateFragment} {@link ASTExpression Expression}?)* {@link ASTTemplateFragment TemplateFragment}
 *
 * </pre>
 *
 * @see <a href="https://openjdk.org/jeps/430">JEP 430: String Templates (Preview)</a> (Java 21)
 * @see <a href="https://openjdk.org/jeps/459">JEP 459: String Templates (Second Preview)</a> (Java 22)
 */
@Experimental("String templates is a Java 21/22 Preview feature")
public final class ASTTemplate extends AbstractJavaNode {
    ASTTemplate(int i) {
        super(i);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
