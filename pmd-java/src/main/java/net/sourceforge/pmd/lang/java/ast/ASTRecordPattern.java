/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * A record pattern (JDK19). This can be found on
 * the right-hand side of an {@link ASTInstanceOfExpression InstanceOfExpression}.
 *
 * <pre class="grammar">
 *
 * RecordPattern ::= {@linkplain ASTReferenceType ReferenceType} {@linkplain ASTRecordStructurePattern RecordStructurePattern} [ {@linkplain ASTVariableDeclaratorId} VariableDeclaratorId ]
 *
 * </pre>
 *
 * @see <a href="https://openjdk.org/jeps/405">JEP 405: Record Patterns (Preview)</a>
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
