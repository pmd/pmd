/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;


import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.input.CharSequenceReader;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * View on a string which doesn't copy the array for subsequence operations.
 * This view is immutable. Since it uses a string internally it benefits from
 * Java 9's compacting feature, it also can efficiently created from a StringBuilder.
 */
public final class Chars implements CharSequence {

    public static final Chars EMPTY = wrap("");

    private final String str;
    private final int start;
    private final int len;

    private Chars(String str, int start, int len) {
        this.str = str;
        this.start = start;
        this.len = len;
    }

    private int idx(int off) {
        return this.start + off;
    }


    /**
     * Wraps the given char sequence.
     */
    public static Chars wrap(CharSequence chars) {
        if (chars instanceof Chars) {
            return (Chars) chars;
        }
        return new Chars(chars.toString(), 0, chars.length());
    }

    /**
     * Write all characters of this buffer into the given writer.
     */
    public void writeFully(Writer writer) throws IOException {
        writer.write(str, start, length());
    }

    /**
     * Reads 'len' characters from index 'from' into the given array at 'off'.
     */
    public void getChars(int from, char @NonNull [] cbuf, int off, int len) {
        if (len == 0) {
            return;
        }
        int start = idx(from);
        str.getChars(start, start + len, cbuf, off);
    }

    /**
     * Appends the character range identified by offset and length into
     * the string builder.
     */
    public void appendChars(StringBuilder sb, int off, int len) {
        if (len == 0) {
            return;
        }
        int idx = idx(off);
        sb.append(str, idx, idx + len);
    }

    public int indexOf(String s, int fromIndex) {
        return str.indexOf(s, idx(fromIndex));
    }

    public boolean startsWith(String prefix, int fromIndex) {
        return str.startsWith(prefix, idx(fromIndex));
    }

    /**
     * Returns a new reader for the whole contents of this char sequence.
     */
    public Reader newReader() {
        return new CharSequenceReader(this);
    }

    @Override
    public int length() {
        return len;
    }

    @Override
    public char charAt(int index) {
        return str.charAt(idx(index));
    }

    @Override
    public Chars subSequence(int start, int end) {
        return slice(start, end - start);
    }

    /**
     * Like {@link #subSequence(int, int)} but with offset + length instead
     * of start + end.
     */
    public Chars slice(int off, int len) {
        if (off < 0 || len < 0 || (off + len) > length()) {
            throw new IndexOutOfBoundsException(
                "Cannot cut " + start + ".." + (off + len) + " (length " + length() + ")"
            );
        }
        if (len == 0) {
            return EMPTY;
        }
        return new Chars(str, idx(off), len);
    }

    @Override
    public String toString() {
        // this already avoids the copy if start == 0 && len == str.length()
        return str.substring(start, start + len);
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
        if (isFullString()) {
            return str.hashCode(); // hashcode is cached on strings
        }
        int h = 0;
        for (int i = start, end = start + len; i < end; i++) {
            h = h * 31 + str.charAt(i);
        }
        return h;
    }

    private boolean isFullString() {
        return start == 0 && len == str.length();
    }
}
