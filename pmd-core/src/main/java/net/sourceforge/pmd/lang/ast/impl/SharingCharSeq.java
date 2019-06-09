/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import org.apache.commons.lang3.StringUtils;
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

    /** If this is null, then {@link #myStr} is never null. */
    private final char[] value;
    private final int offset;
    private final int count;

    private String myStr = null;

    public SharingCharSeq(String str) {
        this(null, -1, str.length());
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
        if (value == null) {
            return myStr.charAt(index);
        }

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
        if (myStr != null) {
            // shortcut
            return myStr.hashCode();
        }
        // don't compute the toString within the hashcode, hopefully
        // the chars differ early on

        int h = 0;
        for (int i = 0; i < count; ++i) {
            h = 31 * h + value[offset + i];
        }
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RichCharSequence)) {
            return false;
        }

        return StringUtils.equals(this, (RichCharSequence) o);
    }
}
