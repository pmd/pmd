/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import static java.lang.Integer.min;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextDocument;

/**
 * A base class for readers that handle escapes starting with a backslash.
 */
public abstract class BackslashEscapeTranslator extends EscapeTranslator {

    private static final char BACKSLASH = '\\';

    /**
     * An offset until which we read backslashes and decided they were not
     * an escape. The read procedure may cut off in the middle of the escape,
     * and turn an even num of backslashes into an odd one, so until we crossed
     * this offset, backslashes are not treated specially.
     */
    private int savedNotEscapeSpecialEnd = Integer.MAX_VALUE;


    public BackslashEscapeTranslator(TextDocument builder) {
        super(builder);
    }

    @Override
    protected int gobbleMaxWithoutEscape(final int maxOff) throws MalformedSourceException {
        int notEscapeEnd = this.savedNotEscapeSpecialEnd;
        int off = input.indexOf(BACKSLASH, bufpos, min(maxOff, notEscapeEnd));
        if (off == -1) {
            this.bufpos = maxOff;
            return maxOff;
        } else {
            return handleBackslash(maxOff, off);
        }
    }

    protected abstract int handleBackslash(int maxOff, int firstBackslashOff) throws MalformedSourceException;

    @Override
    protected int recordEscape(int startOffsetInclusive, int endOffsetExclusive, Chars translation) {
        this.savedNotEscapeSpecialEnd = Integer.MAX_VALUE;
        return super.recordEscape(startOffsetInclusive, endOffsetExclusive, translation);
    }

    protected int abortEscape(int off, int maxOff) {
        // not an escape sequence
        int min = min(maxOff, off);
        // save the number of backslashes that are part of the escape,
        // might have been cut in half by the maxReadahead
        this.savedNotEscapeSpecialEnd = min < off ? off : Integer.MAX_VALUE;
        this.bufpos = min;
        return min;
    }

}
