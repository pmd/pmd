/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.RootNode;

public class ASTCompilationUnit extends AbstractVFNode implements RootNode {
    @Deprecated
    @InternalApi
    public ASTCompilationUnit(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTCompilationUnit(VfParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(VfParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
