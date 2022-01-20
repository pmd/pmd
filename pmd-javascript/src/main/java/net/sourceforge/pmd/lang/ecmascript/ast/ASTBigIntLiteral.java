/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.math.BigInteger;

import org.mozilla.javascript.ast.BigIntLiteral;

public final class ASTBigIntLiteral extends AbstractEcmascriptNode<BigIntLiteral> {

    ASTBigIntLiteral(BigIntLiteral bigIntLiteral) {
        super(bigIntLiteral);
        super.setImage(bigIntLiteral.getValue());
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public BigInteger getNumber() {
        return node.getBigInt();
    }
}
