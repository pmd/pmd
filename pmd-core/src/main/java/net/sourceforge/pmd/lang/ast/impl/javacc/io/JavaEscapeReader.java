/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;

import java.io.IOException;

import net.sourceforge.pmd.util.document.Chars;

/**
 * An implementation of {@link EscapeAwareReader} that translates Java
 * unicode escapes.
 */
@SuppressWarnings("PMD.AssignmentInOperand")
public final class JavaEscapeReader extends BackslashEscapeReader {

    public JavaEscapeReader(Chars input) {
        super(input);
    }

    @Override
    protected int handleBackslash(final int maxOff, final int firstBackslashOff) throws IOException {
        int off = firstBackslashOff;
        while (off < input.length() && input.charAt(off) == '\\') {
            off++;
        }

        int bslashCount = off - firstBackslashOff;
        // is there an escape at offset firstBslashOff?
        if ((bslashCount & 1) == 1 // odd number of backslashes
            && off < input.length() && input.charAt(off) == 'u') { // at least one 'u'
            // odd number of backslashes, this is enough to expect an escape or throw an exception
            while (off < input.length() && input.charAt(off) == 'u') {
                // consume all the 'u's
                off++;
            }
            int end = replaceFirstBackslashWithEscape(firstBackslashOff, off - 1);
            return recordEscape(firstBackslashOff, end - firstBackslashOff, 1);
        } else {
            return abortEscape(off, maxOff);
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
