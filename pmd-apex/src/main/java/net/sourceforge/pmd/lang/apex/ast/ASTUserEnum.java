/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.UserEnum;

public class ASTUserEnum extends ApexRootNode<UserEnum> {

    public ASTUserEnum(UserEnum userEnum) {
        super(userEnum);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        String apexName = node.getDefiningType().getApexName();
        return apexName.substring(apexName.lastIndexOf('.') + 1);
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }
}
