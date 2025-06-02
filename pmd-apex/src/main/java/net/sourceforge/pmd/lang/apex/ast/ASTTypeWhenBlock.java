/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.statement.SwitchStatement;

public final class ASTTypeWhenBlock extends AbstractApexNode.Single<SwitchStatement.WhenType> {

    ASTTypeWhenBlock(SwitchStatement.WhenType whenType) {
        super(whenType);
    }

    /**
     * Returns the when block's matching type name.
     *
     * This includes any type arguments.
     * If the type is a primitive, its case will be normalized.
     */
    public String getType() {
        return caseNormalizedTypeIfPrimitive(node.getType().asCodeString());
    }

    public String getName() {
        return node.getDowncast().getDeclarations().get(0).getId().getString();
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
