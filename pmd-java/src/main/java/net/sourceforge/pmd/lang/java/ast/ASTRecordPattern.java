/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


/**
 * A record pattern, a Java 21 language feature.
 *
 * <pre class="grammar">
 *
 * RecordPattern ::= {@linkplain ASTReferenceType ReferenceType} {@linkplain ASTPatternList PatternList}
 *
 * </pre>
 *
 * @see ASTRecordDeclaration
 * @see <a href="https://openjdk.org/jeps/440">JEP 440: Record Patterns</a> (Java 21)
*/
public final class ASTRecordPattern extends AbstractJavaNode implements ASTPattern {

    ASTRecordPattern(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Gets the type against which the expression is tested.
     */
    public ASTReferenceType getTypeNode() {
        return firstChild(ASTReferenceType.class);
    }

    /** Returns the declared variable. */
    public ASTVariableId getVarId() {
        return firstChild(ASTVariableId.class);
    }
}
