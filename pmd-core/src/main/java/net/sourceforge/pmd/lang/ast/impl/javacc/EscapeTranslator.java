/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import static java.lang.Integer.min;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.FragmentedDocBuilder;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * An object that can translate an input document into an output document,
 * typically by replacing escape sequences with the character they represent.
 *
 * <p>This is an abstract class because the default implementation does not
 * perform any escape processing. Subclasses refine this behavior.
 */
@SuppressWarnings("PMD.AssignmentInOperand")
public abstract class EscapeTranslator {
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
    final FragmentedDocBuilder builder;

    private Chars curEscape;
    private int offInEscape;

    /**
     * Create a translator that will read from the given document.
     *
     * @param original Original document
     *
     * @throws NullPointerException If the parameter is null
     */
    public EscapeTranslator(TextDocument original) {
        AssertionUtil.requireParamNotNull("builder", original);
        this.input = original.getText();
        this.bufpos = 0;
        this.builder = new FragmentedDocBuilder(original);
    }


    /**
     * Translate all the input in the buffer. This consumes this object.
     *
     * @return The translated text document. If there is no escape, returns the original text
     *
     * @throws IllegalStateException    If this method is called more than once on the same object
     * @throws MalformedSourceException If there are invalid escapes in the source
     */
    public TextDocument translateDocument() throws MalformedSourceException {
        ensureOpen();
        try {
            return translateImpl();
        } finally {
            close();
        }
    }

    private TextDocument translateImpl() {
        if (this.bufpos == input.length()) {
            return builder.build();
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
        return builder.build();
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
        this.builder.recordDelta(startOffsetInclusive, endOffsetExclusive, translation);
        this.bufpos = endOffsetExclusive;
        this.curEscape = translation;
        this.offInEscape = 0;
        return startOffsetInclusive;
    }

    /**
     * Closing a translator does not close the underlying document, it just
     * clears the intermediary state.
     */
    private void close() {
        this.bufpos = -1;
        this.input = null;
    }


    /** Check to make sure that the stream has not been closed */
    protected final void ensureOpen() {
        if (input == null) {
            throw new IllegalStateException("Closed");
        }
    }

    protected FileLocation locationAt(int indexInInput) {
        return builder.toLocation(indexInInput);
    }

}
