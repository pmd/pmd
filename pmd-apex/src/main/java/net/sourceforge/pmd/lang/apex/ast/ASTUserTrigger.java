/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.declaration.TypeDeclaration;

public class ASTUserTrigger extends ApexRootNode<TypeDeclaration> {

    @Deprecated
    @InternalApi
    public ASTUserTrigger(TypeDeclaration userTrigger) {
        super(userTrigger);
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
        // return node.getTargetName().stream().map(Identifier::getValue).collect(Collectors.joining("."));
        // TODO(b/239648780)
        return null;
    }

    public List<TriggerUsage> getUsages() {
        /*
        return node.getUsages().stream()
                .map(TriggerUsage::of)
                .sorted()
                .collect(Collectors.toList());
         */
        // TODO(b/239648780)
        return null;
    }
}
