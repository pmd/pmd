/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttribute;

public class ASTPackageDeclaration extends AbstractJavaAnnotatableNode {

    @InternalApi
    @Deprecated
    public ASTPackageDeclaration(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTPackageDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * @deprecated Use {@link #getName()}
     */
    @Deprecated
    @DeprecatedAttribute(replaceWith = "@Name")
    public String getPackageNameImage() {
        return getName();
    }

    /**
     * Returns the name of the package.
     *
     * @since 6.30.0
     */
    public String getName() {
        return ((ASTName) getChild(this.getNumChildren() - 1)).getImage();
    }
}
