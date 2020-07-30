/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A {@code throw} statement.
 *
 * <pre class="grammar">
 *
 * ThrowStatement ::= "throw" {@link ASTExpression Expression} ";"
 *
 * </pre>
 */
public final class ASTThrowStatement extends AbstractStatement implements ASTSwitchArrowRHS {

    ASTThrowStatement(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the expression for the thrown exception.
     */
    public ASTExpression getExpr() {
        return (ASTExpression) getFirstChild();
    }

    /**
     * Gets the image of the first ASTClassOrInterfaceType child or
     * <code>null</code> if none is found. Note that when the statement is
     * something like throw new Exception, this method returns 'Exception' and
     * if the throw statement is like throw e: this method returns 'e'. A
     * special case of returning <code>null</code> is when the throws is like
     * throw this.e or throw this.
     *
     * This is too specific
     *
     * <p>TODO - use symbol table (?)</p>
     *
     * @return the image of the first ASTClassOrInterfaceType node found or
     *     <code>null</code>
     * @deprecated This method is too specific and doesn't support all cases.
     *             It will be removed with PMD 7.
     */
    @Deprecated
    public final String getFirstClassOrInterfaceTypeImage() {
        final ASTClassOrInterfaceType t = getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        return t == null ? null : t.getImage();
    }
}
