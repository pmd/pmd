/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.math.BigInteger;
import java.util.Locale;


/**
 * A numeric literal of any type (double, int, long, float, etc).
 */
public final class ASTNumericLiteral extends AbstractJavaTypeNode implements ASTLiteral {

    /**
     * True if this is an integral literal, ie int OR long,
     * false if this is a floating-point literal, ie float OR double.
     */
    private boolean isIntegral;


    ASTNumericLiteral(int id) {
        super(id);
    }


    ASTNumericLiteral(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    void setIntLiteral() {
        this.isIntegral = true;
    }


    void setFloatLiteral() {
        this.isIntegral = false;
    }


    @Override
    public boolean isIntLiteral() {
       return isIntegral && !isLongLiteral();
    }

    // TODO all of this can be done once in jjtCloseNodeScope


    @Override
    public boolean isLongLiteral() {
        if (isIntegral) {
            String image = getImage();
            char lastChar = image.charAt(image.length() - 1);
            return lastChar == 'l' || lastChar == 'L';
        }
        return false;
    }


    @Override
    public boolean isFloatLiteral() {
        if (!isIntegral) {
            String image = getImage();
            char lastChar = image.charAt(image.length() - 1);
            return lastChar == 'f' || lastChar == 'F';
        }
        return false;
    }


    @Override
    public boolean isDoubleLiteral() {
        return !isIntegral && !isFloatLiteral();
    }


    private String stripIntValue() {
        String image = getImage().toLowerCase(Locale.ROOT).replaceAll("_++", "");

        // literals never have a sign.

        char last = image.charAt(image.length() - 1);
        // This method is only called if this is an int,
        // in which case the 'd' and 'f' suffixes can only
        // be hex digits, which we must not remove
        if (last == 'l') {
            image = image.substring(0, image.length() - 1);
        }

        // ignore base prefix if any
        if (image.charAt(0) == '0' && image.length() > 1) {
            if (image.charAt(1) == 'x' || image.charAt(1) == 'b') {
                image = image.substring(2);
            } else {
                image = image.substring(1);
            }
        }

        return image;
    }


    private String stripFloatValue() {
        // This method is only called if this is a floating point literal.
        // there can't be any 'l' suffix that the double parser doesn't support,
        // so it's enough to just remove underscores
        return getImage().replaceAll("_++", "");
    }

    private int getIntBase() {
        final String image = getImage().toLowerCase(Locale.ROOT);
        if (image.startsWith("0x")) {
            return 16;
        }
        if (image.startsWith("0b")) {
            return 2;
        }
        if (image.startsWith("0") && image.length() > 1) {
            return 8;
        }
        return 10;
    }

    // From 7.0.x, these methods always return a meaningful number, the
    // closest we can find.
    // In 6.0.x, eg getValueAsInt was giving up when this was a double.

    public int getValueAsInt() {
        if (isIntegral) {
            // the downcast allows to parse 0x80000000+ numbers as negative instead of a NumberFormatException
            return (int) getValueAsLong();
        } else {
            return (int) getValueAsDouble();
        }
    }


    public long getValueAsLong() {
        if (isIntegral) {
            // Using BigInteger to allow parsing 0x8000000000000000+ numbers as negative instead of a NumberFormatException
            BigInteger bigInt = new BigInteger(stripIntValue(), getIntBase());
            return bigInt.longValue();
        } else {
            return (long) getValueAsDouble();
        }
    }


    public float getValueAsFloat() {
        return isIntegral ? (float) getValueAsLong() : (float) getValueAsDouble();
    }


    public double getValueAsDouble() {
        return isIntegral ? (double) getValueAsLong() : Double.parseDouble(stripFloatValue());
    }

}
