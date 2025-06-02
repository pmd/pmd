/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

/**
 * Contains a potentially empty list of nested Patterns for {@linkplain ASTRecordPattern RecordPattern}
 * (Java 21).
 *
 * <pre class="grammar">
 *
 * PatternList ::= "(" {@linkplain ASTPattern Pattern} ( "," {@linkplain ASTPattern pattern} ) ")"
 *
 * </pre>
 *
 * @see <a href="https://openjdk.org/jeps/440">JEP 440: Record Patterns</a>
 */
public final class ASTPatternList extends ASTList<ASTPattern> {
    ASTPatternList(int id) {
        super(id, ASTPattern.class);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
