/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.member.Property;

public class ASTProperty extends AbstractApexNode<Property> {

    public ASTProperty(Property property) {
        super(property);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getTypeName() {
        if (node.getFieldInfo() != null) {
            return node.getFieldInfo().getType().getApexName();
        }
        return null;
    }
}
