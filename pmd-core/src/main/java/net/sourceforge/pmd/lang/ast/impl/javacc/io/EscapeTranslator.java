/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;

import static java.lang.Integer.min;

import java.util.function.Function;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.document.Chars;
import net.sourceforge.pmd.util.document.FragmentedDocBuilder;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * An object that can translate an input document into an output document,
 * typically by replacing escape sequences with the character they represent.
 *
 * <p>This is an abstract class because the default implementation does not
 * perform any escape processing. Subclasses refine this behavior.
 */
@SuppressWarnings("PMD.AssignmentInOperand")
public abstract class EscapeTranslator implements AutoCloseable {
    // Note that this can easily be turned into a java.io.Reader with
    // efficient block IO, optimized for the common case where there are
    // few or no escapes. This is part of the history of this file, but
    // was removed for simplicity.

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

    public EscapeTranslator(TextDocument original) {
        AssertionUtil.requireParamNotNull("builder", original);
        this.input = original.getText();
        this.bufpos = 0;
        this.escapes = new FragmentedDocBuilder(original);
    }


    /**
     * Translate all the input in the buffer.
     */
    public TextDocument translateDocument() throws MalformedSourceException {
        ensureOpen();
        if (this.bufpos == input.length()) {
            return escapes.build();
        }

        final int len = input.length(); // remove Integer.MAX_VALUE

        int readChars = 0;
        while (readChars < len && (this.bufpos < input.length() || curEscape != null)) {
            if (curEscape != null) {
                int toRead = min(len - readChars, curEscape.length() - offInEscape);

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

            if (newlyReadChars == 0 && nextJump == input.length()) {
                // eof
                break;
            }
            readChars += newlyReadChars;
        }
        return escapes.build();
    }

    /**
     * Returns the max offset, EXclusive, up to which we can cut the input
     * array from the bufpos to dump it into the output array.
     *
     * @param maxOff Max offset up to which to read ahead
     */
    protected int gobbleMaxWithoutEscape(int maxOff) throws MalformedSourceException {
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

    public void close() {
        this.bufpos = -1;
        this.input = null;
    }


    /** Check to make sure that the stream has not been closed */
    protected void ensureOpen() {
        if (input == null) {
            throw new IllegalStateException("Closed");
        }
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
    protected int inputOffset(int outputOffset) {
        return escapes.inputOffsetAt(outputOffset);
    }

    /**
     * The parameter is an *input* offset, if you got this offset from
     * somewhere else than the input buffer you must first translate it
     * back with {@link #inputOffset(int)}. This implementation is very
     * inefficient but currently is only used for error messages (which
     * obviously are exceptional).
     */
    protected int getLine(int idxInInput) {
        return StringUtil.lineNumberAt(input, idxInInput);
    }

    /**
     * @see #getLine(int)
     */
    protected int getColumn(int idxInInput) {
        return StringUtil.columnNumberAt(input, idxInInput);
    }


    public static Function<TextDocument, TextDocument> translatorFor(Function<TextDocument, EscapeTranslator> translatorMaker) {
        return original -> {
            try (EscapeTranslator translator = translatorMaker.apply(original)) {
                return translator.translateDocument();
            }
        };
    }

}
