/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.math.BigInteger;

import org.mozilla.javascript.ast.BigIntLiteral;

public final class ASTBigIntLiteral extends AbstractEcmascriptNode<BigIntLiteral> {

    ASTBigIntLiteral(BigIntLiteral bigIntLiteral) {
        super(bigIntLiteral);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public BigInteger getNumber() {
        return node.getBigInt();
    }

    public String getValue() {
        return node.getValue();
    }
}
