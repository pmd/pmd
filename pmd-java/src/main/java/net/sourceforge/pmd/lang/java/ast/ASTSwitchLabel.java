/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.NodeStream;


/**
 * Represents either a {@code case} or {@code default} label inside
 * a {@linkplain ASTSwitchStatement switch statement} or {@linkplain ASTSwitchExpression expression}.
 * Since Java 14, labels may have several expressions.
 *
 * <pre class="grammar">
 *
 * SwitchLabel ::=  "case" {@linkplain ASTExpression Expression} ("," {@linkplain ASTExpression Expression} )*
 *                | "case" "null [ "," "default" ]
 *                | "case" ( {@linkplain ASTTypePattern TypePattern} | {@linkplain ASTRecordPattern RecordPattern} )
 *                | "default"
 *
 * </pre>
 *
 * <p>Note: case null and the case patterns are a Java 21 language feature</p>
 *
 * @see <a href="https://openjdk.org/jeps/441">JEP 441: Pattern Matching for switch</a>
 * @see <a href="https://openjdk.org/jeps/432">JEP 432: Record Patterns (Second Preview)</a>
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
    // todo `case default`
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Returns the expressions of this label, or an empty list if this
     * is the default label. This does neither contain {@linkplain  ASTTypePattern TypePatterns}
     * nor {@linkplain ASTRecordPattern RecordPatterns}. To check for this,
     * use {@link #isPatternLabel()}.
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

    /**
     * Checks whether this label tests a {@link ASTTypePattern} or a {@link ASTRecordPattern}.
     * @since 7.7.0
     */
    public boolean isPatternLabel() {
        return children(ASTPattern.class).nonEmpty();
    }
}
