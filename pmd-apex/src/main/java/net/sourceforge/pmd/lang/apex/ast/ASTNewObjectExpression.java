/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTNewObjectExpression extends AbstractApexNode<Node> {

    @Deprecated
    @InternalApi
    public ASTNewObjectExpression(Node newObjectExpression) {
        super(newObjectExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getType() {
        // return String.valueOf(node.getTypeRef());
        // TODO(b/239648780)
        return null;
    }
}
