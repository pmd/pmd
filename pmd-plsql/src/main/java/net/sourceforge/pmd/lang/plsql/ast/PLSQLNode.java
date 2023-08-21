/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeNode;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

public interface PLSQLNode extends ScopedNode, JjtreeNode<PLSQLNode> {

    /**
     * Accept the visitor.
     *
     * @deprecated Use {@link #acceptVisitor(AstVisitor, Object)}
     */
    @Deprecated
    @DeprecatedUntil700
    default Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return acceptVisitor(visitor, data);
    }

    @Override
    Scope getScope();

    /**
     * Return node image converted to the normal Oracle form.
     *
     * <p>
     * Normally this is uppercase, unless the names is quoted ("name").
     * </p>
     */
    default String getCanonicalImage() {
        return PLSQLParserImpl.canonicalName(this.getImage());
    }


    /**
     * Convert arbitrary String to normal Oracle format, under assumption that
     * the passed image is an Oracle name.
     *
     * <p>
     * This a helper method for PLSQL classes dependent on SimpleNode, that
     * would otherwise have to import PLSQParser.
     * </p>
     *
     * @param image
     * @return
     */
    static String getCanonicalImage(String image) {
        return PLSQLParserImpl.canonicalName(image);
    }
}
