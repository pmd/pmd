/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;

import static java.lang.Integer.min;

import net.sourceforge.pmd.util.document.Chars;
import net.sourceforge.pmd.util.document.TextDocument;

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
        int off = this.bufpos;
        boolean seenBackslash = true;
        int notEscapeEnd = this.savedNotEscapeSpecialEnd;
        while (off < maxOff) {
            seenBackslash = input.charAt(off) == BACKSLASH && notEscapeEnd >= off;
            if (seenBackslash) {
                break;
            }
            off++;
        }

        if (!seenBackslash || off == maxOff) {
            this.bufpos = off;
            return off;
        }

        return handleBackslash(maxOff, off);
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
