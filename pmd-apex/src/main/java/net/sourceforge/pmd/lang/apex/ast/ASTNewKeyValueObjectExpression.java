/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTNewKeyValueObjectExpression extends AbstractApexNode<Node> {

    @Deprecated
    @InternalApi
    public ASTNewKeyValueObjectExpression(Node node) {
        super(node);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getType() {
        // return node.getTypeRef().getNames().get(0).getValue();
        // TODO(b/239648780)
        return null;
    }

    public int getParameterCount() {
        // return node.getParameters().size();
        // TODO(b/239648780)
        return 0;
    }
}
