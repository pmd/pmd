/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A guard for refining a switch case in {@link ASTSwitchLabel}s.
 * This is a Java 21 language feature.
 *
 * <pre class="grammar">
 *
 * SwitchLabel := "case" {@linkplain ASTPattern Pattern} Guard?
 * Guard ::= "when" {@linkplain ASTExpression Expression}
 *
 * </pre>
 *
 * @see <a href="https://openjdk.org/jeps/441">JEP 441: Pattern Matching for switch</a>
*/
public final class ASTGuard extends AbstractJavaNode {

    ASTGuard(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ASTExpression getGuard() {
        return (ASTExpression) getChild(0);
    }
}
