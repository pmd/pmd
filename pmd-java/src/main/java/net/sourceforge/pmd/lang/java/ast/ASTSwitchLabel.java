/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.NodeStream;


/**
 * Represents either a {@code case} or {@code default} label inside
 * a {@linkplain ASTSwitchStatement switch statement} or {@linkplain ASTSwitchExpression expression}.
 * Since Java 12, labels may have several expressions.
 *
 * <pre class="grammar">
 *
 * SwitchLabel ::=  "case" {@linkplain ASTExpression Expression} ("," {@linkplain ASTExpression Expression} )*
 *                | "default"
 *
 * </pre>
 */
public final class ASTSwitchLabel extends AbstractJavaNode implements Iterable<ASTExpression> {

    private boolean isDefault;


    ASTSwitchLabel(int id) {
        super(id);
    }


    void setDefault() {
        isDefault = true;
    }

    /** Returns true if this is the {@code default} label. */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Returns the expressions of this label, or an empty list if this
     * is the default label.
     */
    public NodeStream<ASTExpression> getExprList() {
        return children(ASTExpression.class);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public Iterator<ASTExpression> iterator() {
        return children(ASTExpression.class).iterator();
    }
}
