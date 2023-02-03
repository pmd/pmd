/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * A record pattern (Java 19 Preview and Java 20 Preview).
 *
 * <pre class="grammar">
 *
 * RecordPattern ::= {@linkplain ASTReferenceType ReferenceType} {@linkplain ASTComponentPatternList ComponentPatternList} [ {@linkplain ASTVariableDeclaratorId VariableDeclaratorId} ]
 *
 * </pre>
 *
 * @see ASTRecordDeclaration
 * @see <a href="https://openjdk.org/jeps/432">JEP 432: Record Patterns (Second Preview)</a>
*/
@Experimental
public final class ASTRecordPattern extends AbstractJavaNode implements ASTPattern {

    private int parenDepth;

    ASTRecordPattern(int id) {
        super(id);
    }

    ASTRecordPattern(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Gets the type against which the expression is tested.
     */
    public ASTReferenceType getTypeNode() {
        return getFirstChildOfType(ASTReferenceType.class);
    }

    /** Returns the declared variable. */
    public ASTVariableDeclaratorId getVarId() {
        return getFirstChildOfType(ASTVariableDeclaratorId.class);
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
