/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.util.IteratorUtil.AbstractIterator;

/**
 * View on a string which doesn't copy the array for subsequence operations.
 * This view is immutable. Since it uses a string internally it benefits from
 * Java 9's compacting feature, it can also be efficiently created from
 * a StringBuilder. When confronted with an instance of this interface, please
 * don't create substrings unnecessarily. Both {@link #subSequence(int, int)}
 * and {@link #slice(int, int)} can cut out a subsequence without copying
 * the underlying byte array. The {@link Pattern} API also works perfectly
 * on arbitrary {@link CharSequence}s, not just on strings. Lastly some
 * methods here provided provide mediated access to the underlying string,
 * which for many use cases is much more optimal than using this CharSequence
 * directly, eg {@link #appendChars(StringBuilder)}, {@link #writeFully(Writer)}.
 *
 * @see Chars#wrap(CharSequence) Chars::wrap, the factory method
 */
public final class Chars implements CharSequence {

    public static final Chars EMPTY = wrap("");
    /**
     * Special sentinel used by {@link #lines()}.
     */
    private static final int NOT_TRIED = -2;

    /**
     * See {@link StringUtils#INDEX_NOT_FOUND}.
     */
    private static final int NOT_FOUND = -1;

    private final String str;
    private final int start;
    private final int len;

    private Chars(String str, int start, int len) {
        validateRangeWithAssert(start, len, str.length());
        this.str = str;
        this.start = start;
        this.len = len;
    }

    private int idx(int off) {
        return this.start + off;
    }


    /** Whether this slice is the empty string. */
    @SuppressWarnings("PMD.MissingOverride") // with Java 15, isEmpty() has been added to java.lang.CharSequence (#4291)
    public boolean isEmpty() {
        return len == 0;
    }


    /**
     * Wraps the given char sequence into a {@link Chars}. This may
     * call {@link CharSequence#toString()}. If the sequence is already
     * a {@link Chars}, returns it. This is the main factory method for
     * this class. You can eg pass a StringBuilder if you want.
     */
    public static Chars wrap(CharSequence chars) {
        if (chars instanceof Chars) {
            return (Chars) chars;
        }
        return new Chars(chars.toString(), 0, chars.length());
    }

    /**
     * Write all characters of this buffer into the given writer.
     *
     * @param writer A writer
     *
     * @throws NullPointerException If the writer is null
     */
    public void writeFully(@NonNull Writer writer) throws IOException {
        write(writer, 0, length());
    }

    /**
     * Write a range of characters to the given writer.
     *
     * @param writer A writer
     * @param start  Start offset in this CharSequence
     * @param count  Number of characters
     *
     * @throws IOException               If the writer throws
     * @throws IndexOutOfBoundsException See {@link Writer#write(int)}
     */
    public void write(@NonNull Writer writer, int start, int count) throws IOException {
        writer.write(str, idx(start), count);
    }

    /**
     * Copies 'count' characters from index 'srcBegin' into the given array,
     * starting at 'dstBegin'.
     *
     * @param srcBegin Start offset in this CharSequence
     * @param cbuf     Character array
     * @param count    Number of characters to copy
     * @param dstBegin Start index in the array
     *
     * @throws NullPointerException      If the array is null (may)
     * @throws IndexOutOfBoundsException See {@link String#getChars(int, int, char[], int)}
     */
    public void getChars(int srcBegin, char @NonNull [] cbuf, int dstBegin, int count) {
        validateRange(srcBegin, count, length());
        int start = idx(srcBegin);
        str.getChars(start, start + count, cbuf, dstBegin);
    }

    /**
     * Appends the character range identified by start and end offset into
     * the string builder. This is much more efficient than calling
     * {@link StringBuilder#append(CharSequence)} with this as the
     * parameter, especially on Java 9+.
     *
     * @param start Start index (inclusive)
     * @param end   End index (exclusive)
     *
     * @throws IndexOutOfBoundsException See {@link StringBuilder#append(CharSequence, int, int)}
     */
    public void appendChars(StringBuilder sb, int start, int end) {
        if (end == 0) {
            return;
        }
        sb.append(str, idx(start), idx(end));
    }

    /**
     * Append this character sequence on the given stringbuilder.
     * This is much more efficient than calling {@link StringBuilder#append(CharSequence)}
     * with this as the parameter, especially on Java 9+.
     *
     * @param sb String builder
     */
    public void appendChars(StringBuilder sb) {
        sb.append(str, start, start + len);
    }


    /**
     * Returns the characters of this charsequence encoded with the
     * given charset.
     */
    public ByteBuffer getBytes(Charset charset) {
        return charset.encode(CharBuffer.wrap(str, start, start + len));
    }

    /**
     * See {@link String#indexOf(String, int)}.
     */
    public int indexOf(String searched, int fromIndex) {
        // max index in the string at which the search string may start
        final int max = start + len - searched.length();

        if (fromIndex < 0 || max < start + fromIndex) {
            return NOT_FOUND;
        } else if (searched.isEmpty()) {
            return 0;
        }

        final char fst = searched.charAt(0);
        int strpos = str.indexOf(fst, idx(fromIndex));
        while (strpos != NOT_FOUND && strpos <= max) {
            if (str.startsWith(searched, strpos)) {
                return strpos - start;
            }
            strpos = str.indexOf(fst, strpos + 1);
        }
        return NOT_FOUND;
    }

    /**
     * See {@link String#indexOf(int, int)}.
     */
    public int indexOf(int ch, int fromIndex) {
        if (fromIndex < 0 || fromIndex >= len) {
            return NOT_FOUND;
        }
        // we want to avoid searching too far in the string
        // so we don't use String#indexOf, as it would be looking
        // in the rest of the file too, which in the worst case is
        // horrible

        int max = start + len;
        for (int i = start + fromIndex; i < max; i++) {
            char c = str.charAt(i);
            if (c == ch) {
                return i - start;
            }
        }
        return NOT_FOUND;
    }

    /**
     * See {@link String#lastIndexOf(int, int)}.
     */
    public int lastIndexOf(int ch, int fromIndex) {
        if (fromIndex < 0 || fromIndex >= len) {
            return NOT_FOUND;
        }
        // we want to avoid searching too far in the string
        // so we don't use String#indexOf, as it would be looking
        // in the rest of the file too, which in the worst case is
        // horrible

        for (int i = start + fromIndex; i >= start; i--) {
            char c = str.charAt(i);
            if (c == ch) {
                return i - start;
            }
        }
        return NOT_FOUND;
    }

    /**
     * See {@link String#startsWith(String, int)}.
     */
    public boolean startsWith(String prefix, int fromIndex) {
        if (fromIndex < 0 || fromIndex + prefix.length() > len) {
            return false;
        }
        return str.startsWith(prefix, idx(fromIndex));
    }

    /**
     * See {@link String#startsWith(String)}.
     */
    public boolean startsWith(String prefix) {
        return startsWith(prefix, 0);
    }


    public boolean startsWith(char prefix, int fromIndex) {
        if (fromIndex < 0 || fromIndex + 1 > len) {
            return false;
        }
        return str.charAt(idx(fromIndex)) == prefix;
    }

    /**
     * See {@link String#endsWith(String)}.
     */
    public boolean endsWith(String suffix) {
        return startsWith(suffix, length() - suffix.length());
    }

    /**
     * Returns a subsequence which does not start with control characters ({@code <= 32}).
     * This is consistent with {@link String#trim()}.
     */
    public Chars trimStart() {
        int i = start;
        int maxIdx = start + len;
        while (i < maxIdx && str.charAt(i) <= 32) {
            i++;
        }
        i -= start;
        return slice(i, len - i);
    }

    /**
     * Returns a subsequence which does not end with control characters ({@code <= 32}).
     * This is consistent with {@link String#trim()}.
     */
    public Chars trimEnd() {
        int i = start + len;
        while (i > start && str.charAt(i - 1) <= 32) {
            i--;
        }
        return slice(0, i - start);
    }

    /**
     * Like {@link String#trim()}.
     */
    public Chars trim() {
        return trimStart().trimEnd();
    }

    /**
     * Remove trailing and leading blank lines. The resulting string
     * does not end with a line terminator.
     */
    public Chars trimBlankLines() {
        int offsetOfFirstNonBlankChar = length();
        for (int i = 0; i < length(); i++) {
            if (!Character.isWhitespace(charAt(i))) {
                offsetOfFirstNonBlankChar = i;
                break;
            }
        }
        int offsetOfLastNonBlankChar = 0;
        for (int i = length() - 1; i > offsetOfFirstNonBlankChar; i--) {
            if (!Character.isWhitespace(charAt(i))) {
                offsetOfLastNonBlankChar = i;
                break;
            }
        }

        // look backwards before the first non-blank char
        int cutFromInclusive = lastIndexOf('\n', offsetOfFirstNonBlankChar);
        // If firstNonBlankLineStart == -1, ie we're on the first line,
        // we want to start at zero: then we add 1 to get 0
        // If firstNonBlankLineStart >= 0, then it's the index of the
        // \n, we want to cut right after that, so we add 1.
        cutFromInclusive += 1;

        // look forwards after the last non-blank char
        int cutUntilExclusive = indexOf('\n', offsetOfLastNonBlankChar);
        if (cutUntilExclusive == StringUtils.INDEX_NOT_FOUND) {
            cutUntilExclusive = length();
        }

        return subSequence(cutFromInclusive, cutUntilExclusive);
    }

    /**
     * Remove the suffix if it is present, otherwise returns this.
     */
    public Chars removeSuffix(String charSeq) {
        int trimmedLen = length() - charSeq.length();
        if (startsWith(charSeq, trimmedLen)) {
            return slice(0, trimmedLen);
        }
        return this;
    }

    /**
     * Remove the prefix if it is present, otherwise returns this.
     */
    public Chars removePrefix(String charSeq) {
        if (startsWith(charSeq)) {
            return subSequence(charSeq.length(), length());
        }
        return this;
    }


    /**
     * Returns true if this char sequence is logically equal to the
     * parameter. This means they're equal character-by-character. This
     * is more general than {@link #equals(Object)}, which will only answer
     * true if the parameter is a {@link Chars}.
     *
     * @param cs         Another char sequence
     * @param ignoreCase Whether to ignore case
     *
     * @return True if both sequences are logically equal
     */
    public boolean contentEquals(CharSequence cs, boolean ignoreCase) {
        if (cs instanceof Chars) {
            Chars chars2 = (Chars) cs;
            return len == chars2.len && str.regionMatches(ignoreCase, start, chars2.str, chars2.start, len);
        } else {
            return length() == cs.length() && str.regionMatches(ignoreCase, start, cs.toString(), 0, len);
        }
    }

    /**
     * Like {@link #contentEquals(CharSequence, boolean)}, considering
     * case distinctions.
     *
     * @param cs A char sequence
     *
     * @return True if both sequences are logically equal, considering case
     */
    public boolean contentEquals(CharSequence cs) {
        return contentEquals(cs, false);
    }

    @Override
    public int length() {
        return len;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= len) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return str.charAt(idx(index));
    }

    @Override
    public Chars subSequence(int start, int end) {
        return slice(start, end - start);
    }

    /**
     * Returns the subsequence that starts at the given offset and ends
     * at the end of this string. Similar to {@link String#substring(int)}.
     */
    public Chars subSequence(int start) {
        return slice(start, len - start);
    }

    /**
     * Slice a region of text.
     *
     * @param region A region
     *
     * @return A Chars instance
     *
     * @throws IndexOutOfBoundsException If the region is not a valid range
     */
    public Chars slice(TextRegion region) {
        return slice(region.getStartOffset(), region.getLength());
    }

    /**
     * Like {@link #subSequence(int, int)} but with offset + length instead
     * of start + end.
     *
     * @param off Start of the slice ({@code 0 <= off < this.length()})
     * @param len Length of the slice ({@code 0 <= len <= this.length() - off})
     *
     * @return A Chars instance
     *
     * @throws IndexOutOfBoundsException If the parameters are not a valid range
     */
    public Chars slice(int off, int len) {
        validateRange(off, len, this.len);
        if (len == 0) {
            return EMPTY;
        } else if (off == 0 && len == this.len) {
            return this;
        }
        return new Chars(str, idx(off), len);
    }

    /**
     * Returns the substring between the given offsets.
     * given length.
     *
     * <p>Note: Unlike slice or subSequence, this method will create a
     * new String which involves copying the backing char array. Don't
     * use it unnecessarily.
     *
     * @param start Start offset ({@code 0 <= start < this.length()})
     * @param end   End offset ({@code start <= end <= this.length()})
     *
     * @return A substring
     *
     * @throws IndexOutOfBoundsException If the parameters are not a valid range
     * @see String#substring(int, int)
     */
    public String substring(int start, int end) {
        validateRange(start, end - start, this.len);
        return str.substring(idx(start), idx(end));
    }

    private static void validateRangeWithAssert(int off, int len, int bound) {
        assert len >= 0 && off >= 0 && off + len <= bound : invalidRange(off, len, bound);
    }

    private static void validateRange(int off, int len, int bound) {
        if (len < 0 || off < 0 || off + len > bound) {
            throw new IndexOutOfBoundsException(invalidRange(off, len, bound));
        }
    }

    private static String invalidRange(int off, int len, int bound) {
        return "Invalid range [" + off + ", " + (off + len) + "[ (length " + len + ") in string of length " + bound;
    }

    @Override
    public @NonNull String toString() {
        // this already avoids the copy if start == 0 && len == str.length()
        return str.substring(start, start + len);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof Chars && contentEquals((Chars) o);
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

    // test only
    boolean isFullString() {
        return start == 0 && len == str.length();
    }

    /**
     * Returns an iterable over the lines of this char sequence. The lines
     * are yielded without line separators. Like {@link BufferedReader#readLine()},
     * a line delimiter is {@code CR}, {@code LF} or {@code CR+LF}.
     */
    public Iterable<Chars> lines() {
        return () -> new Iterator<Chars>() {
            final int max = len;
            int pos = 0;
            // If those are NOT_TRIED, then we should scan ahead to find them
            // If the scan fails then they'll stay -1 forever and won't be tried again.
            // This is important to scan in documents where we know there are no
            // CR characters, as in our normalized TextFileContent.
            int nextCr = NOT_TRIED;
            int nextLf = NOT_TRIED;

            @Override
            public boolean hasNext() {
                return pos < max;
            }

            @Override
            public Chars next() {
                final int curPos = pos;
                if (nextCr == NOT_TRIED) {
                    nextCr = indexOf('\r', curPos);
                }
                if (nextLf == NOT_TRIED) {
                    nextLf = indexOf('\n', curPos);
                }
                final int cr = nextCr;
                final int lf = nextLf;

                if (cr != NOT_FOUND && lf != NOT_FOUND) {
                    // found both CR and LF
                    int min = Math.min(cr, lf);
                    if (lf == cr + 1) {
                        // CRLF
                        pos = lf + 1;
                        nextCr = NOT_TRIED;
                        nextLf = NOT_TRIED;
                    } else {
                        pos = min + 1;
                        resetLookahead(cr, min);
                    }

                    return subSequence(curPos, min);
                } else if (cr == NOT_FOUND && lf == NOT_FOUND) {
                    // no following line terminator, cut until the end
                    pos = max;
                    return subSequence(curPos, max);
                } else {
                    // lf or cr (exactly one is != -1 and max returns that one)
                    int idx = Math.max(cr, lf);
                    resetLookahead(cr, idx);
                    pos = idx + 1;
                    return subSequence(curPos, idx);
                }
            }

            private void resetLookahead(int cr, int idx) {
                if (idx == cr) {
                    nextCr = NOT_TRIED;
                } else {
                    nextLf = NOT_TRIED;
                }
            }
        };
    }

    /**
     * Returns a stream of lines yielded by {@link #lines()}.
     */
    public Stream<Chars> lineStream() {
        return StreamSupport.stream(lines().spliterator(), false);
    }

    /**
     * Returns a new stringbuilder containing the whole contents of this
     * char sequence.
     */
    public StringBuilder toStringBuilder() {
        StringBuilder sb = new StringBuilder(length());
        appendChars(sb);
        return sb;
    }

    /**
     * Split this slice into subslices, like {@link String#split(String)},
     * except it's iterated lazily.
     */
    public Iterable<Chars> splits(Pattern regex) {
        return () -> new AbstractIterator<Chars>() {
            final Matcher matcher = regex.matcher(Chars.this);
            int lastPos = 0;

            private boolean shouldRetry() {
                if (matcher.find()) {
                    if (matcher.start() == 0 && matcher.end() == 0 && lastPos != len) {
                        return true; // zero length match at the start, we should retry once
                    }
                    setNext(subSequence(lastPos, matcher.start()));
                    lastPos = matcher.end();
                } else if (lastPos != len) {
                    setNext(subSequence(lastPos, len));
                } else {
                    done();
                }
                return false;
            }

            @Override
            protected void computeNext() {
                if (matcher.hitEnd()) {
                    done();
                } else if (shouldRetry()) {
                    shouldRetry();
                }
            }
        };
    }

    /**
     * Returns a new reader for the whole contents of this char sequence.
     */
    public Reader newReader() {
        return new CharsReader(this);
    }

    private static final class CharsReader extends Reader {

        private Chars chars;
        private int pos;
        private final int max;
        private int mark = -1;

        private CharsReader(Chars chars) {
            this.chars = chars;
            this.pos = chars.start;
            this.max = chars.start + chars.len;
        }

        @Override
        public int read(char @NonNull [] cbuf, int off, int len) throws IOException {
            if (len < 0 || off < 0 || off + len > cbuf.length) {
                throw new IndexOutOfBoundsException();
            }
            ensureOpen();
            if (pos >= max) {
                return NOT_FOUND;
            }
            int toRead = Integer.min(max - pos, len);
            chars.str.getChars(pos, pos + toRead, cbuf, off);
            pos += toRead;
            return toRead;
        }

        @Override
        public int read() throws IOException {
            ensureOpen();
            return pos >= max ? NOT_FOUND : chars.str.charAt(pos++);
        }

        @Override
        public long skip(long n) throws IOException {
            ensureOpen();
            int oldPos = pos;
            pos = Math.min(max, pos + (int) n);
            return pos - oldPos;
        }

        private void ensureOpen() throws IOException {
            if (chars == null) {
                throw new IOException("Closed");
            }
        }

        @Override
        public void close() {
            chars = null;
        }

        @Override
        public void mark(int readAheadLimit) {
            mark = pos;
        }

        @Override
        public void reset() throws IOException {
            ensureOpen();
            if (mark == -1) {
                throw new IOException("Reader was not marked");
            }
            pos = mark;
        }

        @Override
        public boolean markSupported() {
            return true;
        }
    }
}
