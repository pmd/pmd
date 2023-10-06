/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * This is a Java 21 Preview feature.
 *
 * <pre class="grammar">
 *
 * TemplateFragment ::= StringTemplateBegin|StringTemplateMid|StringTemplateEnd
 *                      |TextBlockTemplateBegin|TextBlockTemplateMid|TextBlockTemplateEnd
 *
 * </pre>
 *
 * @see <a href="https://openjdk.org/jeps/430">JEP 430: String Templates (Preview)</a>
 */
@Experimental
public final class ASTTemplateFragment extends AbstractJavaNode {
    ASTTemplateFragment(int i) {
        super(i);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
