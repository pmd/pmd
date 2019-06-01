/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTResource extends ASTFormalParameter {

    @InternalApi
    @Deprecated
    public ASTResource(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTResource(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    // TODO Should we deprecate all methods from ASTFormalParameter?

}
