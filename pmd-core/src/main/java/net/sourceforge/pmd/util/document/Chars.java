/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;


import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Wraps a char array. {@link #subSequence(int, int) subsequence} does
 * not copy the array. Instances may be {@link #isReadOnly() read-only}.
 * This is easier to use than {@link CharBuffer}, and more predictable.
 */
public final class Chars implements CharSequence {

    private final char[] arr;
    private final int start;
    private final int len;
    private final boolean readOnly;

    private Chars(char[] arr, int start, int len, boolean readOnly) {
        this.arr = arr;
        this.start = start;
        this.len = len;
        this.readOnly = readOnly;
    }

    public Chars(CharSequence cs, int start, int len, boolean readOnly) {
        this.readOnly = readOnly;
        this.start = 0;
        this.len = len;
        this.arr = new char[len];
        cs.toString().getChars(start, start + len, arr, 0);
    }

    /**
     * Wraps the given char array without copying it. The caller should
     * take care that the original array doesn't leak.
     */
    public static Chars wrap(char[] chars, boolean readOnly) {
        return new Chars(chars, 0, chars.length, readOnly);
    }

    /**
     * Wraps the given char sequence (dumps its characters into a new array).
     */
    public static Chars wrap(CharSequence chars, boolean readOnly) {
        if (chars instanceof Chars && readOnly && ((Chars) chars).readOnly) {
            return (Chars) chars;
        }
        return new Chars(chars, 0, chars.length(), readOnly);
    }

    /**
     * Returns a char buffer that is readonly, with the same contents
     * as this one.
     */
    public Chars toReadOnly() {
        return isReadOnly() ? this : copy(true);
    }

    /**
     * Returns a mutable char buffer with the same contents as this one.
     * This always copies the internal array.
     */
    public Chars mutableCopy() {
        return copy(false);
    }

    private Chars copy(boolean readOnly) {
        char[] chars = new char[length()];
        System.arraycopy(this.arr, start, chars, 0, length());
        return new Chars(chars, 0, length(), readOnly);
    }

    /**
     * Write all characters of this buffer into the given writer.
     */
    public void writeFully(Writer writer) throws IOException {
        writer.write(arr, start, length());
    }

    /**
     * Reads 'len' characters from index 'from' into the given array at 'off'.
     */
    public void getChars(int from, char[] cbuf, int off, int len) {
        System.arraycopy(arr, idx(from), cbuf, off, len);
    }

    /**
     * Set the character at index 'off' to 'c'.
     *
     * @throws UnsupportedOperationException If this buffer is read only
     */
    public void set(int off, char c) {
        if (isReadOnly()) {
            throw new UnsupportedOperationException("Read only buffer");
        }
        Validate.validIndex(this, off);
        arr[idx(off)] = c;
    }


    /**
     * A read-only buffer does not support the {@link #set(int, char) set}
     * operation. {@link #toReadOnly()} will not copy the char array.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    private int idx(int off) {
        return this.start + off;
    }

    public Reader newReader() {
        return new CharArrayReader(arr, start, length());
    }


    @Override
    public int length() {
        return len;
    }

    @Override
    public char charAt(int index) {
        Validate.validIndex(this, index);
        return arr[idx(index)];
    }

    @Override
    public Chars subSequence(int start, int end) {
        Validate.validIndex(this, start);
        Validate.validIndex(this, end);
        return new Chars(arr, idx(start), end - start, isReadOnly());
    }

    @Override
    public String toString() {
        return new String(arr, start, len);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Chars chars = (Chars) o;
        return StringUtils.equals(this, chars);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = start, end = idx(len); i < end; i++) {
            result += arr[i] * 31;
        }
        return result;
    }
}
