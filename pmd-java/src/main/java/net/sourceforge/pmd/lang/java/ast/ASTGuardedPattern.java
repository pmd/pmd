/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * A guarded pattern (JDK17 Preview). This can be found in {@link ASTSwitchLabel}s.
 *
 * <pre class="grammar">
 *
 * GuardedPattern ::= {@linkplain ASTPattern Pattern} "&amp;&amp;" {@linkplain ASTExpression Expression}
 *
 * </pre>
 *
 * @see <a href="https://openjdk.java.net/jeps/406">JEP 406: Pattern Matching for switch (Preview)</a>
 *
 * @deprecated This is not used with java 19 preview anymore. Only valid for java 18 preview.
 */
@Experimental
@Deprecated
public final class ASTGuardedPattern extends AbstractJavaNode implements ASTPattern {

    private int parenDepth;

    ASTGuardedPattern(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /** Returns the guarded pattern. */
    public ASTPattern getPattern() {
        return (ASTPattern) getChild(0);
    }

    /** Returns the boolean expression guarding the pattern. */
    public ASTExpression getGuard() {
        return (ASTExpression) getChild(1);
    }

    void bumpParenDepth() {
        parenDepth++;
    }

    @Override
    @Experimental
    public int getParenthesisDepth() {
        return parenDepth;
    }
}
