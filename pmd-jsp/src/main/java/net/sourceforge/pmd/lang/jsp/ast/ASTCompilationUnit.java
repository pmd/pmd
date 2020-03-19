/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.ast.RootNode;

public final class ASTCompilationUnit extends AbstractJspNode implements RootNode {

    ASTCompilationUnit(int id) {
        super(id);
    }

    @Override
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
