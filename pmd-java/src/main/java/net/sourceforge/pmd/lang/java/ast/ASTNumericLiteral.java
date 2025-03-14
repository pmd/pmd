/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType;


/**
 * A numeric literal of any type (double, int, long, float, etc).
 */
public final class ASTNumericLiteral extends AbstractLiteral implements ASTLiteral {

    /**
     * True if this is an integral literal, ie int OR long,
     * false if this is a floating-point literal, ie float OR double.
     */
    private boolean isIntegral;
    private boolean is64bits;
    private long longValue;
    private double doubleValue;


    ASTNumericLiteral(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public Chars getLiteralText() {
        return super.getLiteralText();
    }

    @Override
    public @NonNull Number getConstValue() {
        return (Number) super.getConstValue();
    }

    @Override
    protected @NonNull Number buildConstValue() {
        // don't use ternaries, the compiler messes up autoboxing.
        if (isIntegral()) {
            if (isIntLiteral()) {
                return getValueAsInt();
            }
            return getValueAsLong();
        } else {
            if (isFloatLiteral()) {
                return getValueAsFloat();
            }
            return getValueAsDouble();
        }
    }

    @Override
    public @NonNull JPrimitiveType getTypeMirror() {
        return (JPrimitiveType) super.getTypeMirror();
    }

    void setIntLiteral() {
        this.isIntegral = true;
    }


    void setFloatLiteral() {
        this.isIntegral = false;
    }

    @Override
    public void jjtClose() {
        super.jjtClose();

        Chars image = getLiteralText();
        char lastChar = image.charAt(image.length() - 1);
        if (isIntegral) {
            is64bits = lastChar == 'l' || lastChar == 'L';
            longValue = parseIntegralValue(image);
            doubleValue = (double) longValue;
        } else {
            is64bits = !(lastChar == 'f' || lastChar == 'F');
            doubleValue = Double.parseDouble(StringUtils.remove(image.toString(), '_'));
            longValue = (long) doubleValue;
        }
    }


    public boolean isIntLiteral() {
        return isIntegral && !is64bits;
    }

    public boolean isLongLiteral() {
        return isIntegral && is64bits;
    }

    public boolean isFloatLiteral() {
        return !isIntegral && !is64bits;
    }

    public boolean isDoubleLiteral() {
        return !isIntegral && is64bits;
    }


    /**
     * Returns true if this is an integral literal, ie either a long or
     * an integer literal. Otherwise, this is a floating point literal.
     */
    public boolean isIntegral() {
        return isIntegral;
    }

    /**
     * Returns the base of the literal, eg 8 for an octal literal,
     * 10 for a decimal literal, etc. By convention this returns 10
     * for the literal {@code 0} (which can really be any base).
     */
    public int getBase() {
        return getBase(getLiteralText(), isIntegral());
    }

    static int getBase(Chars image, boolean isIntegral) {
        if (image.length() > 1 && image.charAt(0) == '0') {
            switch (image.charAt(1)) {
            case 'x':
            case 'X':
                return 16;
            case 'b':
            case 'B':
                return 2;
            default:
                return isIntegral ? 8 : 10;
            }
        }
        return 10;
    }

    // From 7.0.x, these methods always return a meaningful number, the
    // closest we can find.
    // In 6.0.x, eg getValueAsInt was giving up when this was a double.

    public int getValueAsInt() {
        return (int) longValue;
    }


    public long getValueAsLong() {
        return longValue;
    }


    public float getValueAsFloat() {
        return (float) doubleValue;
    }


    public double getValueAsDouble() {
        return doubleValue;
    }

    /**
     * Parse an int or long literal into a long. This avoids creating
     * and discarding a BigInteger, and avoids exceptions if the literal
     * is malformed.
     *
     * <p>Invalid literals or overflows result in {@code 0L}.
     */
    static long parseIntegralValue(Chars image) {
        final int base = getBase(image, true);
        if (base == 8) {
            image = image.subSequence(1); // 0
        } else if (base != 10) {
            image = image.subSequence(2); // 0x / 0b
        }

        int length = image.length();
        char lastChar = image.charAt(length - 1);
        if (lastChar == 'l' || lastChar == 'L') {
            length--;
        }

        try {
            String literalImage = image.substring(0, length).replaceAll("_", "");
            return Long.parseUnsignedLong(literalImage, base);
        } catch (NumberFormatException e) {
            // invalid literal or overflow
            return 0L;
        }
    }
}
