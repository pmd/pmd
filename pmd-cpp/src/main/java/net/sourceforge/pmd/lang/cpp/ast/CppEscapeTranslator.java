/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.io.BackslashEscapeTranslator;
import net.sourceforge.pmd.util.document.Chars;
import net.sourceforge.pmd.util.document.TextDocument;

public class CppEscapeTranslator extends BackslashEscapeTranslator {

    private static final char NEWLINE = '\n';
    private static final char CARRIAGE_RETURN = '\r';

    public CppEscapeTranslator(TextDocument input) {
        super(input);
    }

    @Override
    protected int handleBackslash(int maxOff, final int backSlashOff) {
        int off = backSlashOff + 1;

        if (input.charAt(off) == NEWLINE) {
            return recordEscape(backSlashOff, off + 1, Chars.EMPTY);
        } else if (input.charAt(off) == CARRIAGE_RETURN) {
            if (input.charAt(++off) == NEWLINE) {
                return recordEscape(backSlashOff, off + 1, Chars.EMPTY);
            }
        }

        return abortEscape(off, maxOff);
    }
}
