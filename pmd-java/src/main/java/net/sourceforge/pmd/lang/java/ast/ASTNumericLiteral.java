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
    public @NonNull Number getConstValue() {
        // don't use ternaries, the compiler messes up autoboxing.
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
        Chars image = getText();
        char lastChar = image.charAt(image.length() - 1);
        if (isIntegral) {
            is64bits = lastChar == 'l' || lastChar == 'L';
            longValue = parseIntegerValue(image);
            doubleValue = (double) longValue;
        } else {
            is64bits = !(lastChar == 'f' || lastChar == 'F');
            doubleValue = Double.parseDouble(StringUtils.remove(image.toString(), '_'));
            longValue = (long) doubleValue;
        }
    }


    @Override
    public boolean isIntLiteral() {
        return isIntegral && !is64bits;
    }

    @Override
    public boolean isLongLiteral() {
        return isIntegral && is64bits;
    }


    @Override
    public boolean isFloatLiteral() {
        return !isIntegral && !is64bits;
    }


    @Override
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
        return getBase(getText());
    }

    static int getBase(Chars image) {
        if (image.length() > 1 && image.charAt(0) == '0') {
            switch (image.charAt(1)) {
            case 'x':
            case 'X':
                return 16;
            case 'b':
            case 'B':
                return 2;
            case '.':
                return 10;
            default:
                return 8;
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
     */
    private static long parseIntegerValue(Chars image) {
        final int base = getBase(image);
        if (base == 8) {
            image = image.subSequence(1); // 0
        } else if (base != 10) {
            image = image.subSequence(2); // 0x / 0b
        }

        long result = 0;
        int idx = image.length() - 1;
        long power = 1;
        char c = image.charAt(idx);
        if (c == 'l' || c == 'L') {
            idx--;
        }
        while (idx >= 0) {
            c = image.charAt(idx);
            idx--;
            if (c == '_') {
                continue;
            }
            final int digit;
            if ('0' <= c && c - '0' < base) {
                digit = c - '0';
            } else if (base == 16 && 'a' <= c && c <= 'f') {
                digit = 10 + c - 'a';
            } else if (base == 16 && 'A' <= c && c <= 'F') {
                digit = 10 + c - 'A';
            } else {
                // invalid literal
                return 0;
            }
            try {
                result = Math.addExact(result, Math.multiplyExact(power, (long) digit));
                if (idx >= 0) { // otherwise might cause overflow for nothing
                    power = Math.multiplyExact(power, (long) base);
                }
            } catch (ArithmeticException overflow) {
                return Long.MAX_VALUE; // invalid literal
            }

        }
        return result;
    }
}
