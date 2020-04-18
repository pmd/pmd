/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.io.BackslashEscapeReader;
import net.sourceforge.pmd.util.document.Chars;

public class CppEscapeReader extends BackslashEscapeReader {

    private static final char NEWLINE = '\n';
    private static final char CARRIAGE_RETURN = '\r';

    public CppEscapeReader(Chars input) {
        super(input);
    }

    @Override
    protected int handleBackslash(int maxOff, final int backSlashOff) {
        int off = backSlashOff;

        if (input.charAt(off) == NEWLINE) {
            return recordEscape(backSlashOff, 2, 0);
        } else if (input.charAt(off) == CARRIAGE_RETURN) {
            if (input.charAt(++off) == NEWLINE) {
                return recordEscape(backSlashOff, 3, 0);
            }
        }

        return abortEscape(off, maxOff);
    }
}
