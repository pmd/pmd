/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a "foreach"-loop on an {@link Iterable}.
 *
 * <pre class="grammar">
 *
 * ForeachStatement ::= "for" "(" ( {@linkplain ASTLocalVariableDeclaration LocalVariableDeclaration} | {@linkplain ASTRecordPattern RecordPattern} ) ":" {@linkplain ASTExpression Expression} ")" {@linkplain ASTStatement Statement}
 *
 * </pre>
 *
 * <p>Note: Using a {@linkplain ASTRecordPattern RecordPattern} in an enhanced for statement is a Java 20 Preview feature</p>
 *
 * @see <a href="https://openjdk.org/jeps/432">JEP 432: Record Patterns (Second Preview)</a>
 */
public final class ASTForeachStatement extends AbstractStatement implements InternalInterfaces.VariableIdOwner, ASTLoopStatement {

    ASTForeachStatement(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    @Override
    @NonNull
    public ASTVariableDeclaratorId getVarId() {
        // in case of destructuring record patterns, there might be multiple vars
        return getFirstChild().descendants(ASTVariableDeclaratorId.class).first();
    }

    /**
     * Returns the expression that evaluates to the {@link Iterable}
     * being looped upon.
     */
    @NonNull
    public ASTExpression getIterableExpr() {
        return getFirstChildOfType(ASTExpression.class);
    }


}
