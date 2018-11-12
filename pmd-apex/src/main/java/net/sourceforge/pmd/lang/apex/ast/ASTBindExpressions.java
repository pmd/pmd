/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.BindExpressions;

public class ASTBindExpressions extends AbstractApexNode<BindExpressions> {

    public ASTBindExpressions(BindExpressions bindExpressions) {
        super(bindExpressions);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
