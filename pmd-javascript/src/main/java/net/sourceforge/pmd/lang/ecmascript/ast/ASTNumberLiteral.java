/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.NumberLiteral;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTNumberLiteral extends AbstractEcmascriptNode<NumberLiteral> {
    @Deprecated
    @InternalApi
    public ASTNumberLiteral(NumberLiteral numberLiteral) {
        super(numberLiteral);
        super.setImage(numberLiteral.getValue());
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getNormalizedImage() {
        String image = getImage();
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
}
