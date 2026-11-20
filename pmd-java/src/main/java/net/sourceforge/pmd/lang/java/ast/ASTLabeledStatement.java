/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.rule.xpath.DeprecatedAttribute;

/**
 * A wrapper around a statement that assigns it a label.
 *
 * <pre class="grammar">
 *
 * LabeledStatement ::= &lt;IDENTIFIER&gt; ":" {@link ASTStatement Statement}
 *
 * </pre>
 */
public final class ASTLabeledStatement extends AbstractStatement {

    private String label;

    ASTLabeledStatement(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * @deprecated Since 7.29.0. Use {@link #getLabel()} instead.
     */
    @Override
    @Deprecated
    @DeprecatedAttribute(replaceWith = "@Label")
    public String getImage() {
        return getLabel();
    }

    void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the name of the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returned the statement named by this label.
     */
    public ASTStatement getStatement() {
        return (ASTStatement) getChild(0);
    }

}
