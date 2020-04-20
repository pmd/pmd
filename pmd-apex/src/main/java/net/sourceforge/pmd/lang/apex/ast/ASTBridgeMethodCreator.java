/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.member.bridge.BridgeMethodCreator;

public final class ASTBridgeMethodCreator extends AbstractApexNode<BridgeMethodCreator> {

    ASTBridgeMethodCreator(BridgeMethodCreator bridgeMethodCreator) {
        super(bridgeMethodCreator);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
