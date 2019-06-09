/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A {@link CharSequence} implemented with the (value, offset, count) representation,
 * meaning it shares the underlying char array to allocate sub-sequences.
 * This is advantageous to represent a tokenized file, as we share the
 * char array for the whole file and only change the bounds for each token.
 *
 * @author Clément Fournier
 */
public final class SharingCharSeq implements RichCharSequence {

    private static final SharingCharSeq EMPTY = new SharingCharSeq(new char[0], 0, 0);

    private final char[] value;
    private final int offset;
    private final int count;

    private String myStr = null;

    public SharingCharSeq(String str) {
        this(str.toCharArray(), 0, str.length());
        myStr = str;
    }

    public SharingCharSeq(char[] value, int offset, int count) {
        this.value = value;
        this.offset = offset;
        this.count = count;
    }


    @Override
    public int length() {
        return count;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index + " not in [0," + count + "[");
        }

        return value[offset + index];
    }

    @Override
    public RichCharSequence subSequence(int start, int end) {
        if (start < 0 || end > count || start > end) {
            throw new IndexOutOfBoundsException("Invalid range: [" + start + "," + end + "[ not in [0," + count + "[");
        }
        return start == end ? EMPTY : new SharingCharSeq(value, offset + start, end - start);
    }

    @Override
    public RichCharSequence subSequence(int start) {
        return subSequence(start, count);
    }

    @NonNull
    @Override
    public String toString() {
        String str = myStr;
        if (str == null) {
            str = new String(value, offset, count);
            myStr = str;
        }
        return str;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SharingCharSeq that = (SharingCharSeq) o;
        return Objects.equals(toString(), that.toString());
    }
}
