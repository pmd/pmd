/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


/**
 * An unnamed pattern, a Java 22 language feature.
 *
 * <pre class="grammar">
 *
 * UnnamedPattern ::= "_"
 *
 * </pre>
 *
 * @see <a href="https://openjdk.org/jeps/456">JEP 456: Unnamed Variables &amp; Patterns</a> (Java 22)
*/
public final class ASTUnnamedPattern extends AbstractJavaPattern {

    ASTUnnamedPattern(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
