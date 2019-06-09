/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import java.util.regex.Pattern;

import org.apache.commons.lang3.CharSequenceUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * A {@link CharSequence} with convenience methods to make the interface
 * similar to a {@link String}. This is incentive to not convert the
 * sequence to a string when equivalent methods exist which would not
 * force the creation of a {@link String}.
 *
 * <p>Also, contrary to {@link CharSequence}, the contract of {@link Object#equals(Object)}
 * is refined to mean that any {@link RichCharSequence} must be equatable
 * to another {@link RichCharSequence} implementation, making this type
 * suitable for use as keys of a map for example.
 *
 */
public interface RichCharSequence extends CharSequence {

    @Override
    RichCharSequence subSequence(int start, int end);


    /** @see CharSequenceUtils#subSequence(CharSequence, int) */
    default RichCharSequence subSequence(int start) {
        return subSequence(start, length());
    }


    /** @see StringUtils#indexOf(CharSequence, int) */
    default int indexOf(int searchChar) {
        return StringUtils.indexOf(this, searchChar);
    }


    /** @see StringUtils#indexOf(CharSequence, int, int) */
    default int indexOf(int searchChar, int startPos) {
        return StringUtils.indexOf(this, searchChar, startPos);
    }


    /** @see StringUtils#lastIndexOf(CharSequence, int) */
    default int lastIndexOf(int searchChar) {
        return StringUtils.lastIndexOf(this, searchChar);
    }


    /** @see StringUtils#lastIndexOf(CharSequence, int, int) */
    default int lastIndexOf(int searchChar, int startPos) {
        return StringUtils.lastIndexOf(this, searchChar, startPos);
    }


    default boolean isEmpty() {
        return length() == 0;
    }


    /** @see StringUtils#isWhitespace(CharSequence) */
    default boolean isWhitespace() {
        return StringUtils.isWhitespace(toString());
    }


    /** @see StringUtils#contains(CharSequence, CharSequence) */
    default boolean contains(CharSequence sequence) {
        return StringUtils.contains(this, sequence);
    }


    /** @see StringUtils#contains(CharSequence, int) */
    default boolean contains(int searchChar) {
        return StringUtils.contains(this, searchChar);
    }


    /** @see StringUtils#containsIgnoreCase(CharSequence, CharSequence) */
    default boolean containsIgnoreCase(CharSequence sequence) {
        return StringUtils.containsIgnoreCase(this, sequence);
    }


    /** @see StringUtils#endsWith(CharSequence, CharSequence) */
    default boolean endsWith(CharSequence suffix) {
        return StringUtils.endsWith(this, suffix);
    }


    /** @see StringUtils#startsWith(CharSequence, CharSequence) */
    default boolean startsWith(CharSequence prefix) {
        return StringUtils.startsWith(this, prefix);
    }

    /** @see Pattern#matches(String, CharSequence) */
    default boolean matches(String regex) {
        return Pattern.matches(regex, this);
    }

    /**
     * Compares this sequence to the argument, following the contract
     * of {@link StringUtils#equals(CharSequence, CharSequence)}.
     *
     * <p>Note that {@link Object#equals(Object)} only considers {@link RichCharSequence}
     * of the same type, for symmetry, which for example would fail with
     * {@link String}.
     */
    default boolean equalsSeq(CharSequence other) {
        return StringUtils.equals(this, other);
    }

    /** @see StringUtils#equalsIgnoreCase(CharSequence, CharSequence)*/
    default boolean equalsIgnoreCase(CharSequence other) {
        return StringUtils.equalsIgnoreCase(this, other);
    }

    // TODO test
    //   there are surely off-by-ones somewhere

    /** Returns the column number at the given position. */
    default int getColumnNumberAt(int posExclusive) {
        int prevLf = lastIndexOf('\n', posExclusive);
        return prevLf < 0 ? posExclusive : length() - prevLf;
    }


    /** Returns the line number at the given position. */
    default int getLineNumberAt(int posExclusive) {
        if (posExclusive >= length()) {
            throw new IndexOutOfBoundsException();
        }
        int l = 1;
        for (int i = 0; i < posExclusive; i++) {
            char c = charAt(i);
            if (c == '\n') {
                l++;
            }
        }
        return l;
    }


}
