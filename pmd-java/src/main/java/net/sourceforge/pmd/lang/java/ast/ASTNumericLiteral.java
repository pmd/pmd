/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.math.BigInteger;
import java.util.Locale;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

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
    private boolean is64BitsWide;
    private Number value;


    ASTNumericLiteral(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public @NonNull Number getConstValue() {
        return Objects.requireNonNull(value);
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
        // here, we know whether we're an integral literal or not.
        String image = getImage();
        char lastChar = image.charAt(image.length() - 1);
        if (isIntegral) {
            is64BitsWide = lastChar == 'l' || lastChar == 'L';
            // Using BigInteger to allow parsing 0x8000000000000000+ numbers as negative instead of a NumberFormatException
            BigInteger bigInt = new BigInteger(stripIntValue(), getBase());

            // tip: be careful of ternary expressions when playing with autoboxing
            if (is64BitsWide) {
                value = bigInt.longValue();
            } else {
                value = bigInt.intValue();
            }
        } else {
            is64BitsWide = lastChar == 'd' || lastChar == 'D';
            double d = Double.parseDouble(stripFloatValue());
            if (is64BitsWide) {
                value = d;
            } else {
                value = (float) d;
            }
        }
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
        final String image = getImage();
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


    /**
     * Returns whether this is a literal of type {@code int}.
     */
    public boolean isIntLiteral() {
        return isIntegral && !is64BitsWide;
    }

    /**
     * Returns whether this is a literal of type {@code long}.
     */
    public boolean isLongLiteral() {
        return isIntegral && is64BitsWide;
    }

    /**
     * Returns whether this is a literal of type {@code float}.
     */
    public boolean isFloatLiteral() {
        return !isIntegral && !is64BitsWide;
    }


    /**
     * Returns whether this is a literal of type {@code double}.
     */
    public boolean isDoubleLiteral() {
        return !isIntegral && is64BitsWide;
    }

    /**
     * Returns the int value closest to the actual value of this literal.
     */
    public int getValueAsInt() {
        return value.intValue();
    }


    /**
     * Returns the long value closest to the actual value of this literal.
     */
    public long getValueAsLong() {
        return value.longValue();
    }


    /**
     * Returns the float value closest to the actual value of this literal.
     */
    public float getValueAsFloat() {
        return value.floatValue();
    }


    /**
     * Returns the double value closest to the actual value of this literal.
     */
    public double getValueAsDouble() {
        return value.doubleValue();
    }

}
