/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import com.google.summit.ast.declaration.TriggerDeclaration;

public final class ASTUserTrigger extends BaseApexClass<TriggerDeclaration> {

    ASTUserTrigger(TriggerDeclaration triggerDeclaration) {
        super(triggerDeclaration);
    }

    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
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
