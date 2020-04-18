/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import static java.lang.Integer.min;

import java.io.IOException;
import java.io.Reader;

import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.document.Chars;

/**
 * A reader that can interpret escapes in its input text. It records where
 * escapes occurred, and can translate an offset in the translated
 * input document to a line+column position in the original input.
 *
 * <p>The default implementation does not perform any escape translation.
 */
@SuppressWarnings("PMD.AssignmentInOperand")
public class EscapeAwareReader extends Reader {

    /**
     * Source characters. When there is an escape, eg \ u00a0, the
     * first backslash is replaced with the translated value of the
     * escape. The bufpos is updated so that we read the next char
     * after the escape.
     */
    protected Chars input;
    /** Position of the next char to read in the input. */
    protected int bufpos;
    /** Keep track of adjustments to make to the offsets, caused by unicode escapes. */
    final EscapeTracker escapes = new EscapeTracker();

    public EscapeAwareReader(Chars input) {
        assert input != null;
        this.input = input.mutableCopy();
        bufpos = 0;
    }

    /**
     * Translate all the characters in the buffer.
     */
    public int translate() throws IOException {
        return readUnchecked(null, 0, Integer.MAX_VALUE);
    }


    @Override
    public int read(final char[] cbuf, final int off, int len) throws IOException {
        if (off < 0 || len < 0 || len + off > cbuf.length) {
            throw new IndexOutOfBoundsException("cbuf len=" + cbuf.length + " off=" + off + " len=" + len);
        }
        return readUnchecked(cbuf, off, len);
    }

    private int readUnchecked(char[] cbuf, int off, int len) throws IOException {
        ensureOpen();
        if (this.bufpos == input.length()) {
            return -1;
        }

        len = min(len, input.length()); // remove Integer.MAX_VALUE

        int readChars = 0;
        while (readChars < len && this.bufpos < input.length()) {
            int bpos = this.bufpos;
            int nextJump = gobbleMaxWithoutEscape(min(input.length(), bpos + len - readChars));
            int newlyReadChars = nextJump - bpos;

            assert newlyReadChars >= 0 && (readChars + newlyReadChars) <= len;

            if (newlyReadChars != 0) {
                if (cbuf != null) {
                    input.getChars(bpos, cbuf, off + readChars, newlyReadChars);
                }
            } else if (nextJump == input.length()) {
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
    protected int gobbleMaxWithoutEscape(int maxOff) throws IOException {
        return this.bufpos = maxOff;
    }

    protected int recordEscape(final int startOffsetInclusive, int lengthInSource, int translatedLength) {
        assert lengthInSource > 0 && startOffsetInclusive >= 0;
        this.escapes.recordEscape(startOffsetInclusive, lengthInSource, translatedLength);
        this.bufpos = startOffsetInclusive + lengthInSource;
        return startOffsetInclusive + translatedLength;
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
        return StringUtil.lineNumberAt(input, idxInInput);
    }

    public int getColumn(int idxInInput) {
        return StringUtil.columnNumberAt(input, idxInInput);
    }

}
