/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.condition.StandardCondition;

public class ASTStandardCondition extends AbstractApexNode<StandardCondition> {

    @Deprecated
    @InternalApi
    public ASTStandardCondition(StandardCondition standardCondition) {
        super(standardCondition);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
