/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTThrowStatement extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTThrowStatement(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTThrowStatement(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Gets the image of the first ASTClassOrInterfaceType child or
     * <code>null</code> if none is found. Note that when the statement is
     * something like throw new Exception, this method returns 'Exception' and
     * if the throw statement is like throw e: this method returns 'e'. A
     * special case of returning <code>null</code> is when the throws is like
     * throw this.e or throw this.
     *
     * <p>TODO - use symbol table (?)</p>
     *
     * @return the image of the first ASTClassOrInterfaceType node found or
     *     <code>null</code>
     */
    public final String getFirstClassOrInterfaceTypeImage() {
        final ASTClassOrInterfaceType t = getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        return t == null ? null : t.getImage();
    }
}
