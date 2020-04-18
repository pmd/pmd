/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

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

    @Override
    protected int gobbleMaxWithoutEscape(final int maxOff) throws IOException {
        int off = this.bufpos;
        boolean noBackSlash = false;
        int notEscapeEnd = this.savedNotEscapeSpecialEnd;
        while (off < maxOff && (noBackSlash = input.charAt(off) != '\\' || notEscapeEnd < off)) {
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
        // is there an escape at offset firstBslashOff?
        if ((bslashCount & 1) == 1 // odd number of backslashes
            && off < input.length() && input.charAt(off) == 'u') { // at least one 'u'
            // odd number of backslashes, this is enough to expect an escape or throw an exception
            while (off < input.length() && input.charAt(off) == 'u') {
                // consume all the 'u's
                off++;
            }
            int end = replaceFirstBackslashWithEscape(firstBslashOff, off - 1);
            this.savedNotEscapeSpecialEnd = Integer.MAX_VALUE;
            return recordEscape(firstBslashOff, end - firstBslashOff, 1);
        } else {
            // not an escape sequence
            int min = min(maxOff, off);
            // save the number of backslashes that are part of the escape,
            // might have been cut in half by the maxReadahead
            this.savedNotEscapeSpecialEnd = min < off ? off : Integer.MAX_VALUE;
            this.bufpos = min;
            return min;
        }
    }

    private int replaceFirstBackslashWithEscape(int posOfFirstBackSlash, int offOfTheU) throws IOException {
        try {
            char c = (char)
                    ( hexVal(input.charAt(++offOfTheU)) << 12
                    | hexVal(input.charAt(++offOfTheU)) << 8
                    | hexVal(input.charAt(++offOfTheU)) << 4
                    | hexVal(input.charAt(++offOfTheU))
                    );
            input.set(posOfFirstBackSlash, c); // replace the start char of the backslash
            return offOfTheU + 1;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
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
