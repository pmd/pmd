/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTNewMapLiteralExpression extends AbstractApexNode.Single<Node> {

    @Deprecated
    @InternalApi
    public ASTNewMapLiteralExpression(Node newMapLiteralExpression) {
        super(newMapLiteralExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
