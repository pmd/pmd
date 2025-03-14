/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.math.BigDecimal;

import org.mozilla.javascript.ast.NumberLiteral;

public final class ASTNumberLiteral extends AbstractEcmascriptNode<NumberLiteral> {
    ASTNumberLiteral(NumberLiteral numberLiteral) {
        super(numberLiteral);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getNormalizedImage() {
        String image = getValue();
        image = image.replaceAll("_", "");
        image = normalizeHexIntegerLiteral(image);
        image = normalizeBinaryLiteral(image);
        image = normalizeOctalLiteral(image);
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

    private String normalizeBinaryLiteral(String image) {
        if (image.startsWith("0b") || image.startsWith("0B")) {
            return String.valueOf(Integer.parseInt(image.substring(2), 2));
        }
        return image;
    }

    private String normalizeOctalLiteral(String image) {
        if (image.startsWith("0o") || image.startsWith("0O")) {
            return String.valueOf(Integer.parseInt(image.substring(2), 8));
        }
        return image;
    }

    public double getNumber() {
        return node.getNumber();
    }

    public String getValue() {
        return node.getValue();
    }

    /**
     * Checks if this number literal cannot be represented exactly without loss as
     * a JavaScript Number. The number is either too big or uses too many decimal places.
     * @return {@code true} if the literal is inaccurate.
     * @since 7.4.0
     */
    public boolean isInaccurate() {
        BigDecimal bigDecimal = new BigDecimal(getNormalizedImage());
        BigDecimal converted = BigDecimal.valueOf(bigDecimal.doubleValue());
        return bigDecimal.compareTo(converted) != 0;
    }
}
