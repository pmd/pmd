/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.rule.xpath.DeprecatedAttribute;

/**
 * A break statement, that jumps to a named label (or exits the current loop).
 *
 * <pre class="grammar">
 *
 * BreakStatement ::= "break" &lt;IDENTIFIER&gt;? ";"
 *
 * </pre>
 */
public final class ASTBreakStatement extends AbstractStatement {

    private static final Function<Object, ASTStatement> BREAK_TARGET_MAPPER =
        NodeStream.asInstanceOf(ASTLoopStatement.class, ASTSwitchStatement.class);

    private String label;

    ASTBreakStatement(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the label, or null if there is none.
     */
    public @Nullable String getLabel() {
        return label;
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
     * Returns the statement that is the target of this break. This may be
     * a loop, or a switch statement, or a labeled statement. This may
     * return null if the code is invalid.
     */
    public ASTStatement getTarget() {
        String myLabel = this.getLabel();
        if (myLabel == null) {
            return ancestors().map(BREAK_TARGET_MAPPER).first();
        }
        return ancestors(ASTLabeledStatement.class)
            .filter(it -> it.getLabel().equals(myLabel))
            .first();
    }

}
