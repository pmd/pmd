/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.NumberLiteral;

public final class ASTNumberLiteral extends AbstractEcmascriptNode<NumberLiteral> {
    ASTNumberLiteral(NumberLiteral numberLiteral) {
        super(numberLiteral);
        super.setImage(numberLiteral.getValue());
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getNormalizedImage() {
        String image = getImage();
        image = normalizeHexIntegerLiteral(image);
        image = image.replace('e', 'E');
        if (image.indexOf('.') == -1 && image.indexOf('E') == -1) {
            image = image + ".0";
        }
        return image;
    }

    private String normalizeHexIntegerLiteral(String image) {
        if (image.startsWith("0x") || image.startsWith("0X")) {
            return String.valueOf(Integer.parseInt(image.substring(2), 16));
        }
        return image;
    }

    public double getNumber() {
        return node.getNumber();
    }
}
