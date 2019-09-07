/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import apex.jorje.data.Identifier;
import apex.jorje.semantic.ast.compilation.UserTrigger;

public class ASTUserTrigger extends ApexRootNode<UserTrigger> {

    public ASTUserTrigger(UserTrigger userTrigger) {
        super(userTrigger);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getDefiningType().getApexName();
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }

    public String getTargetName() {
        return node.getTargetName().stream().map(Identifier::getValue).collect(Collectors.joining("."));
    }

    public List<TriggerUsage> getUsages() {
        return node.getUsages().stream()
                .map(TriggerUsage::of)
                .sorted()
                .collect(Collectors.toList());
    }
}
