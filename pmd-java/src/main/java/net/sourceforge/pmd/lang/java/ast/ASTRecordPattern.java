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
public final class ASTRecordPattern extends AbstractJavaPattern {

    ASTRecordPattern(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Return the type of the record.
     */
    public ASTReferenceType getTypeNode() {
        return firstChild(ASTReferenceType.class);
    }


    /**
     * Return the patterns for each record component.
     *
     * @since 7.3.0
     */
    public ASTPatternList getComponentPatterns() {
        return firstChild(ASTPatternList.class);
    }


    /**
     * Returns the declared variable.
     *
     * @deprecated This method was added here by mistake. Record patterns don't declare a pattern variable
     * for the whole pattern, but rather for individual record components, which can be accessed via
     * {@link #getComponentPatterns()}.
     */
    @Deprecated
    public ASTVariableId getVarId() {
        return firstChild(ASTVariableId.class);
    }
}
