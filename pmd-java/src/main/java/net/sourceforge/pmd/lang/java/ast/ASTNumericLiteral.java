/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.math.BigInteger;
import java.util.Locale;

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


    ASTNumericLiteral(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @NonNull
    @Override
    public Object getConstValue() {
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
            BigInteger bigInt = new BigInteger(stripIntValue(), getBase());
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
