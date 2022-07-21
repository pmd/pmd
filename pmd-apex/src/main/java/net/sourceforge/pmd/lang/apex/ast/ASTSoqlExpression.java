/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTSoqlExpression extends AbstractApexNode<Node> {

    @Deprecated
    @InternalApi
    public ASTSoqlExpression(Node soqlExpression) {
        super(soqlExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getQuery() {
        // return node.getRawQuery();
        // TODO(b/239648780)
        return null;
    }

    public String getCanonicalQuery() {
        // return node.getCanonicalQuery();
        // TODO(b/239648780)
        return null;
    }
}
