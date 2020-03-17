/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.RootNode;

public class ASTCompilationUnit extends AbstractJspNode implements RootNode {
    @InternalApi
    @Deprecated
    public ASTCompilationUnit(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTCompilationUnit(JspParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
