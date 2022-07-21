/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTBridgeMethodCreator extends AbstractApexNode<Node> {

    @Deprecated
    @InternalApi
    public ASTBridgeMethodCreator(Node bridgeMethodCreator) {
        super(bridgeMethodCreator);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
