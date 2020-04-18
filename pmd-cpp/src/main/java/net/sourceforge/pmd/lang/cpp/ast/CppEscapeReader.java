/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp.ast;

import static java.lang.Integer.min;

import java.io.IOException;

import net.sourceforge.pmd.lang.ast.impl.io.EscapeAwareReader;
import net.sourceforge.pmd.util.document.Chars;

public class CppEscapeReader extends EscapeAwareReader {

    private static final char NEWLINE = '\n';
    private static final char CARRIAGE_RETURN = '\r';

    private int savedNotEscapeSpecialEnd = Integer.MAX_VALUE;

    public CppEscapeReader(Chars input) {
        super(input);
    }

    @Override
    protected int gobbleMaxWithoutEscape(final int maxOff) throws IOException {
        int off = this.bufpos;
        boolean noBackSlash = false;
        int notEscapeEnd = this.savedNotEscapeSpecialEnd;
        while (off < maxOff && (noBackSlash = input.charAt(off) != '\\' || notEscapeEnd < off)) {
            off++;
        }

        if (noBackSlash || off == maxOff) {
            this.bufpos = off;
            return off;
        }

        final int backSlackOff = off++;
        if (input.charAt(off) == NEWLINE) {
            return recordEscape(backSlackOff, 2, 0);
        } else if (input.charAt(off) == CARRIAGE_RETURN) {
            if (input.charAt(++off) == NEWLINE) {
                return recordEscape(backSlackOff, 3, 0);
            }
        }

        // not an escape sequence
        int min = min(maxOff, off);
        // save the number of backslashes that are part of the escape,
        // might have been cut in half by the maxReadahead
        this.savedNotEscapeSpecialEnd = min < off ? off : Integer.MAX_VALUE;
        this.bufpos = min;
        return min;
    }
}
