/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import net.sourceforge.pmd.annotation.Experimental;

/**
 * An unnamed pattern, a Java 21 Preview language feature.
 *
 * <pre class="grammar">
 *
 * UnnamedPattern ::= "_"
 *
 * </pre>
 *
 * @see <a href="https://openjdk.org/jeps/443">JEP 443: Unnamed patterns and variables (Preview)</a> (Java 21)
*/
@Experimental
public final class ASTUnnamedPattern extends AbstractJavaNode implements ASTPattern {

    ASTUnnamedPattern(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public int getParenthesisDepth() {
        return 0;
    }
}
