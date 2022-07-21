/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTVariableExpression extends AbstractApexNode<Node> {

    @Deprecated
    @InternalApi
    public ASTVariableExpression(Node variableExpression) {
        super(variableExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        /*
        if (node.getIdentifier() != null) {
            return node.getIdentifier().getValue();
        }
         */
        // TODO(b/239648780)
        return null;
    }
}
