/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * Groups a variable ID and its initializer if it exists.
 * May be found as a child of {@linkplain ASTFieldDeclaration field declarations} and
 * {@linkplain ASTLocalVariableDeclaration local variable declarations}.
 *
 * <p>The {@linkplain #getInitializer() initializer} is the only place
 * {@linkplain ASTArrayInitializer array initializer expressions} can be found.
 *
 * <pre class="grammar">
 *
 * VariableDeclarator ::= {@linkplain ASTVariableDeclaratorId VariableDeclaratorId} ( "=" {@linkplain ASTExpression Expression} )?
 *
 * </pre>
 */
public class ASTVariableDeclarator extends AbstractJavaNode implements InternalInterfaces.VariableIdOwner {

    ASTVariableDeclarator(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the name of the declared variable.
     */
    public String getName() {
        return getVarId().getName();
    }


    /**
     * Returns the id of the declared variable.
     */
    @Override
    @NonNull
    public ASTVariableDeclaratorId getVarId() {
        return (ASTVariableDeclaratorId) getChild(0);
    }


    /**
     * Returns true if the declared variable is initialized.
     * Otherwise, {@link #getInitializer()} returns null.
     */
    public boolean hasInitializer() {
        return getLastChild() instanceof ASTExpression;
    }


    /**
     * Returns the initializer, of the variable, or null if it doesn't exist.
     */
    @Nullable
    public ASTExpression getInitializer() {
        return hasInitializer() ? (ASTExpression) getLastChild() : null;
    }


}
