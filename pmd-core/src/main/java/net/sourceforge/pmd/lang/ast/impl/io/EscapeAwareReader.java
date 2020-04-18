/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.io;

import static java.lang.Integer.min;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import net.sourceforge.pmd.util.StringUtil;

/**
 * A reader that optionally escapes its input text. It records where
 * escapes occurred, and can translate an offset in the translated
 * input document to a line+column position in the original input.
 *
 * <p>The default implementation does not perform any escaping.
 */
@SuppressWarnings("PMD.AssignmentInOperand")
public class EscapeAwareReader extends Reader {

    /**
     * Source characters. When there is an escape, eg \ u00a0, the
     * first backslash is replaced with the translated value of the
     * escape. The bufpos is updated so that we read the next char
     * after the escape.
     *
     * <p>This makes it so that 1. we don't need an additional buffer for
     * translated chars, and 2. the full escape is preserved, just use
     * the {@link EscapeTracker} to get the position of the escape and
     * replace the first char with a backslash. We can report unnecessary
     * escapes that way.
     */
    protected char[] input;
    /** Position of the next char to read in the input. */
    protected int bufpos;
    /** Keep track of adjustments to make to the offsets, caused by unicode escapes. */
    final EscapeTracker escapes = new EscapeTracker();

    public EscapeAwareReader(CharSequence input, int startIdxInclusive, int endIdxExclusive) {
        assert input != null;
        assert startIdxInclusive >= 0;
        assert endIdxExclusive >= 0;
        assert endIdxExclusive >= startIdxInclusive;

        int len = endIdxExclusive - startIdxInclusive;

        this.input = new char[len];
        input.toString().getChars(startIdxInclusive, endIdxExclusive, this.input, 0);
        bufpos = 0;
    }

    public EscapeAwareReader(CharSequence input) {
        this(input, 0, input.length());
    }

    /**
     * Translate all the characters in the buffer.
     */
    public int translate() throws IOException {
        return read(null, 0, Integer.MAX_VALUE);
    }


    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        ensureOpen();
        if (this.bufpos == input.length) {
            return -1;
        }

        int readChars = 0;
        while (readChars < len && this.bufpos < input.length) {
            int bpos = this.bufpos;
            int nextJump = gobbleMaxWithoutEscape(bpos, len - readChars);
            int newlyReadChars = nextJump - bpos;

            assert newlyReadChars >= 0 && (readChars + newlyReadChars) <= len;

            if (newlyReadChars != 0) {
                if (cbuf != null) {
                    System.arraycopy(input, bpos, cbuf, off + readChars, newlyReadChars);
                }
            } else if (nextJump == input.length) {
                // eof
                break;
            }
            readChars += newlyReadChars;
        }
        return readChars;
    }


    /**
     * Returns the max offset, EXclusive, with which we can cut the input
     * array from the bufpos to dump it into the output array. This sets
     * the bufpos to where we should start the next jump.
     */
    protected int gobbleMaxWithoutEscape(final int bufpos, final int maxReadahead) throws IOException {
        return this.bufpos = min(bufpos + maxReadahead, input.length);
    }

    protected void recordEscape(final int startOffsetInclusive, int length) {
        this.escapes.recordEscape(startOffsetInclusive, length);
    }

    @Override
    public void close() throws IOException {
        this.bufpos = -1;
        this.input = null;
    }


    /** Check to make sure that the stream has not been closed */
    protected void ensureOpen() throws IOException {
        if (input == null) {
            throw new IOException("Stream closed");
        }
    }

    @Override
    public boolean ready() throws IOException {
        ensureOpen();
        return true;
    }

    /**
     * Returns the offset in the input text of the given translated offset.
     * This includes the length of any unicode escapes.
     *
     * <pre>
     * input:      "a\u00a0b"
     * translated: "a b"
     *
     * inputOffset(0) = 0
     * inputOffset(1) = 1
     * inputOffset(2) = 7 // includes the length of the escape
     * </pre>
     */
    public int inputOffset(int outputOffset) {
        return escapes.inputOffsetAt(outputOffset);
    }

    public int getLine(int idxInInput) {
        return StringUtil.lineNumberAt(CharBuffer.wrap(input), idxInInput);
    }

    public int getColumn(int idxInInput) {
        return StringUtil.columnNumberAt(CharBuffer.wrap(input), idxInInput);
    }

}
