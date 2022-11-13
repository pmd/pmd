/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.SoqlOrSoslBinding;

public class ASTBindExpressions extends AbstractApexNode.Single<SoqlOrSoslBinding> {

    ASTBindExpressions(SoqlOrSoslBinding bindExpressions) {
        super(bindExpressions);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
