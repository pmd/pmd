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
    protected int gobbleMaxWithoutEscape(int bufpos, int maxReadahead) throws IOException {
        int off = bufpos;
        int max = min(bufpos + maxReadahead, input.length());
        boolean noBackSlash = false;
        int notEscapeEnd = this.savedNotEscapeSpecialEnd;
        while (off < max && (noBackSlash = input.charAt(off) != '\\' || notEscapeEnd < off)) {
            off++;
        }

        if (noBackSlash) {
            this.bufpos = off;
            return off;
        }

        final int backSlackOff = off++;
        if (input.charAt(off) == NEWLINE) {
            recordEscape(backSlackOff, 2);
            this.bufpos = off + 2;
            return backSlackOff;
        } else if (input.charAt(off) == CARRIAGE_RETURN) {
            if (input.charAt(++off) == NEWLINE) {
                recordEscape(backSlackOff, 3);
                this.bufpos = off + 3;
                return backSlackOff;
            }
        }

        // not an escape sequence
        int min = min(bufpos + maxReadahead, off);
        // save the number of backslashes that are part of the escape,
        // might have been cut in half by the maxReadahead
        this.savedNotEscapeSpecialEnd = min < off ? off : Integer.MAX_VALUE;
        this.bufpos = min;
        return min;
    }
}
