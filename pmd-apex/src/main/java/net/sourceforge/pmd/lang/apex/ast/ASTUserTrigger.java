/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.declaration.TriggerDeclaration;

public class ASTUserTrigger extends ApexRootNode<TriggerDeclaration> {

    @Deprecated
    @InternalApi
    public ASTUserTrigger(TriggerDeclaration triggerDeclaration) {
        super(triggerDeclaration);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return getDefiningType();
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }

    public String getTargetName() {
        return node.getTarget().getString();
    }

    public List<TriggerUsage> getUsages() {
        return node.getCases().stream()
                .map(TriggerUsage::of)
                .sorted()
                .collect(Collectors.toList());
    }
}
