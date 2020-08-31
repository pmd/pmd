/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;

import static java.lang.Integer.min;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.document.Chars;

/**
 * A reader that may interpret escapes in its input text. It records
 * where escapes occurred, and can translate an offset in the translated
 * document (the "output") to an offset in the original input.
 * The implementation is optimised for the case where there are few escapes.
 * {@link CharStream} is the API to navigate on a translated document
 * (with arbitrary backtrack abilities).
 *
 * <p>This is useful to back a {@link CharStream} for JavaCC implementation,
 * but can also be used as a plain {@link Reader} if using other parser/lexer
 * implementations. The reader behaviour is optimised for block IO and has
 * poor char-by-char performance. Use a {@link BufferedReader} if you need it.
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
    final FragmentedDocBuilder escapes;

    private Chars curEscape;
    private int offInEscape;

    public EscapeAwareReader(Chars input) {
        AssertionUtil.requireParamNotNull("input", input);
        this.input = input;
        bufpos = 0;
        escapes = new FragmentedDocBuilder(input);
    }

    /**
     * Translate all the input in the buffer. This is fed to a cursor initialized to zero.
     */
    FragmentedDocCursor translate() throws IOException {
        readUnchecked(null, 0, Integer.MAX_VALUE);
        return escapes.newCursor();
    }


    @Override
    public int read(final char[] cbuf, final int off, int len) throws IOException {
        if (off < 0 || len < 0 || len + off > cbuf.length) {
            throw new IndexOutOfBoundsException("cbuf len=" + cbuf.length + " off=" + off + " len=" + len);
        }
        return readUnchecked(cbuf, off, len);
    }

    // if cbuf is null we just want to record escapes
    private int readUnchecked(char @Nullable [] cbuf, int off, int len) throws IOException {
        ensureOpen();
        if (this.bufpos == input.length()) {
            return -1;
        }

        len = min(len, input.length()); // remove Integer.MAX_VALUE

        int readChars = 0;
        while (readChars < len && (this.bufpos < input.length() || curEscape != null)) {
            if (curEscape != null) {
                int toRead = min(len - readChars, curEscape.length() - offInEscape);

                if (cbuf != null) {
                    curEscape.getChars(0, cbuf, off + readChars, toRead);
                }
                readChars += toRead;
                offInEscape += toRead;

                if (curEscape.length() == offInEscape) {
                    curEscape = null;
                    continue;
                } else {
                    break; // len cut us off, we'll retry next time
                }
            }

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
     * Returns the max offset, EXclusive, up to which we can cut the input
     * array from the bufpos to dump it into the output array.
     *
     * @param maxOff Max offset up to which to read ahead
     */
    protected int gobbleMaxWithoutEscape(int maxOff) throws IOException {
        this.bufpos = maxOff;
        return maxOff;
    }

    protected int recordEscape(final int startOffsetInclusive, int endOffsetExclusive, Chars translation) {
        assert endOffsetExclusive > startOffsetInclusive && startOffsetInclusive >= 0;
        this.escapes.recordDelta(startOffsetInclusive, endOffsetExclusive, translation);
        this.bufpos = endOffsetExclusive;
        this.curEscape = translation;
        this.offInEscape = 0;
        return startOffsetInclusive;
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

    /**
     * The parameter is an *input* offset, if you got this offset from
     * somewhere else than the input buffer you must first translate it
     * back with {@link #inputOffset(int)}. This implementation is very
     * inefficient but currently is only used for error messages (which
     * obviously are exceptional).
     */
    public int getLine(int idxInInput) {
        return StringUtil.lineNumberAt(input, idxInInput);
    }

    /**
     * @see #getLine(int)
     */
    public int getColumn(int idxInInput) {
        return StringUtil.columnNumberAt(input, idxInInput);
    }

}
