/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.rule.xpath.DeprecatedAttribute;

/**
 * A continue statement, that jumps to the next iteration of an enclosing loop.
 *
 * <pre class="grammar">
 *
 * ContinueStatement ::= "continue" &lt;IDENTIFIER&gt;? ";"
 *
 * </pre>
 */
public final class ASTContinueStatement extends AbstractStatement {

    private static final Function<Object, ASTLoopStatement> CONTINUE_TARGET_MAPPER =
        NodeStream.asInstanceOf(ASTLoopStatement.class);

    private String label;

    ASTContinueStatement(int id) {
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
     * Returns the label, or null if there is none.
     */
    public @Nullable String getLabel() {
        return label;
    }

    /**
     * Returns the statement that is the target of this break. This can
     * be a loop, or an {@link ASTLabeledStatement}.
     */
    public ASTStatement getTarget() {
        String myLabel = this.getLabel();
        if (myLabel == null) {
            return ancestors().map(CONTINUE_TARGET_MAPPER).first();
        }
        return ancestors(ASTLabeledStatement.class)
            .filter(it -> it.getLabel().equals(myLabel))
            .first();
    }

}
