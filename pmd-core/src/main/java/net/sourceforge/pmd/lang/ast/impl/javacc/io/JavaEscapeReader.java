/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;

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
    protected int handleBackslash(final int maxOff, final int firstBackslashOff) throws MalformedSourceException {
        int off = firstBackslashOff;
        while (off < input.length() && input.charAt(off) == '\\') {
            off++;
        }

        int bslashCount = off - firstBackslashOff;
        // is there an escape at offset firstBslashOff?
        if ((bslashCount & 1) == 1 // odd number of backslashes
            && off < input.length() && input.charAt(off) == 'u') { // at least one 'u'
            // this is enough to expect an escape or throw an exception
            while (off < input.length() && input.charAt(off) == 'u') {
                // consume all the 'u's
                off++;
            }
            Chars value = escapeValue(firstBackslashOff, off - 1);
            int endOffset = off + 4; // + 4 hex digits
            return recordEscape(firstBackslashOff, endOffset, value);
        } else {
            return abortEscape(off, maxOff);
        }
    }

    private Chars escapeValue(int posOfFirstBackSlash, int offOfTheU) throws MalformedSourceException {
        try {
            char c = (char)
                    ( hexVal(input.charAt(++offOfTheU)) << 12 // SUPPRESS CHECKSTYLE paren pad
                    | hexVal(input.charAt(++offOfTheU)) << 8
                    | hexVal(input.charAt(++offOfTheU)) << 4
                    | hexVal(input.charAt(++offOfTheU))
                    );

            return Chars.wrap(Character.toString(c));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new MalformedSourceException("Invalid escape sequence", e, posOfFirstBackSlash, getLine(posOfFirstBackSlash), getColumn(posOfFirstBackSlash));
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
