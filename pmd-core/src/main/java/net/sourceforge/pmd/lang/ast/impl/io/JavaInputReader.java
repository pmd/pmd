/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.io;

import static java.lang.Integer.min;

import java.io.BufferedReader;
import java.io.IOException;

import net.sourceforge.pmd.util.document.Chars;

/**
 * An implementation of java.io.Reader that translates Java unicode escapes.
 * This implementation has efficient block IO but poor char-by-char performance.
 * If this is required, wrap it into a {@link BufferedReader}.
 */
@SuppressWarnings("PMD.AssignmentInOperand")
public final class JavaInputReader extends EscapeAwareReader {

    /**
     * An offset until which we read backslashes and decided they were not
     * an escape. The read procedure may cut off in the middle of the escape,
     * and turn an even num of backslashes into an odd one, so until we crossed
     * this offset, backslashes are not treated specially.
     */
    private int savedNotEscapeSpecialEnd = Integer.MAX_VALUE;

    public JavaInputReader(Chars input) {
        super(input);
    }

    /**
     * Returns the max offset, EXclusive, with which we can cut the input
     * array from the bufpos to dump it into the output array. This sets
     * the bufpos to where we should start the next jump.
     */
    @Override
    protected int gobbleMaxWithoutEscape(final int bufpos, final int maxReadahead) throws IOException {
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

        final int firstBslashOff = off;
        while (off < input.length() && input.charAt(off) == '\\') {
            off++;
        }

        int bslashCount = off - firstBslashOff;
        // this condition is "is there an escape at offset firstBslashOff"
        if ((bslashCount & 1) == 1    // odd number of backslashes
            && off < input.length() - 4 // at least 5 chars to form the escape ('u' + 4 hex digits)
            && input.charAt(off) == 'u') {   // the char after the last backslash is a 'u'

            replaceFirstBackslashWithEscape(firstBslashOff, off);
            this.savedNotEscapeSpecialEnd = Integer.MAX_VALUE;
            this.bufpos = off + 5;
            this.recordEscape(firstBslashOff, off + 5 - firstBslashOff);
            return firstBslashOff + 1;
        } else {
            // not an escape sequence
            int min = min(bufpos + maxReadahead, off);
            // save the number of backslashes that are part of the escape,
            // might have been cut in half by the maxReadahead
            this.savedNotEscapeSpecialEnd = min < off ? off : Integer.MAX_VALUE;
            this.bufpos = min;
            return min;
        }
    }

    private void replaceFirstBackslashWithEscape(int posOfFirstBackSlash, int offOfTheU) throws IOException {
        try {
            char c = (char)
                    ( hexVal(input.charAt(++offOfTheU)) << 12
                    | hexVal(input.charAt(++offOfTheU)) << 8
                    | hexVal(input.charAt(++offOfTheU)) << 4
                    | hexVal(input.charAt(++offOfTheU))
                    );
            input.set(posOfFirstBackSlash, c); // replace the start char of the backslash
        } catch (NumberFormatException e) {

            String message = "Invalid escape sequence at line "
                + getLine(posOfFirstBackSlash) + ", column "
                + getColumn(posOfFirstBackSlash);

            throw new IOException(message, e);
        }
    }

    private static int hexVal(char c) {
        switch (c) {
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            return c - '0';
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
            return c - ('A' - 10);
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
            return c - ('a' - 10);
        default:
            throw new NumberFormatException("Character '" + c + "' is not a valid hexadecimal digit");
        }
    }
}
