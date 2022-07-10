/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * Contains a potentially empty list of nested Patterns for {@linkplain ASTRecordPattern RecordPattern} (JDK 19).
 *
 * <pre class="grammar">
 *
 * ComponentPatternList ::= "(" {@linkplain ASTPattern Pattern} ( "," {@linkplain ASTPattern pattern} ) ")"
 *
 * </pre>
 *
 * @see <a href="https://openjdk.org/jeps/405">JEP 405: Record Patterns (Preview)</a>
 */
@Experimental
public final class ASTComponentPatternList extends ASTList<ASTPattern> {
    ASTComponentPatternList(int id) {
        super(id, ASTPattern.class);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
