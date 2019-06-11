/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A {@link CharSequence} implemented with the (value, offset, count) representation,
 * meaning it shares the underlying char array to allocate sub-sequences.
 * This is advantageous to represent a tokenized file, as we share the
 * char array for the whole file and only change the bounds for each token.
 *
 * <p>{@link Object#toString()} may be called to get a reference to a string
 * that doesn't hold a strong reference to the underlying char array,
 * hence not preventing its garbage collection.
 *
 * @author Clément Fournier
 */
public final class SharingCharSeq implements RichCharSequence {

    private static final SharingCharSeq EMPTY = new SharingCharSeq(new char[0], 0, 0);

    /** If this is null, then {@link #myStr} is never null. */
    private final char[] value;
    private final int offset;
    private final int count;

    @Nullable
    private String myStr;

    public SharingCharSeq(@NonNull String str) {
        this.myStr = Objects.requireNonNull(str, "String value cannot be null!");
        this.offset = 0;
        this.count = str.length();
        this.value = str.toCharArray();
    }


    // this is for a subsequence
    private SharingCharSeq(char[] value, int offset, int count) {
        this.value = value;
        this.offset = offset;
        this.count = count;
        this.myStr = null;
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

        int len = end - start;
        return len == 0 ? EMPTY : new SharingCharSeq(value, offset + start, len);
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
        if (!(o instanceof RichCharSequence)) {
            return false;
        }

        return StringUtils.equals(this, (RichCharSequence) o);
    }
}
