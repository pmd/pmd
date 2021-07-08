/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import apex.jorje.data.Identifier;
import apex.jorje.semantic.ast.compilation.UserTrigger;

public final class ASTUserTrigger extends BaseApexClass<UserTrigger> {

    ASTUserTrigger(UserTrigger userTrigger) {
        super(userTrigger);
    }

    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
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
