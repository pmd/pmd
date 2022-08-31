/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

public final class ASTEmptyReferenceExpression extends AbstractApexNode.Empty {
    
    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getDefiningType() {
        return null;
    }
}
