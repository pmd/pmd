/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp.cpd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.impl.javacc.EscapeTranslator;
import net.sourceforge.pmd.lang.ast.impl.javacc.MalformedSourceException;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextDocument;

/**
 *
 */
class CppBlockSkipper extends EscapeTranslator {

    private final Pattern skipStart;
    private final Pattern skipEnd;

    static Pattern compileSkipMarker(String marker) {
        return Pattern.compile("^(?i)" + Pattern.quote(marker), Pattern.MULTILINE);
    }

    CppBlockSkipper(TextDocument original, Pattern skipStartMarker, Pattern skipEndMarker) {
        super(original);
        skipStart = skipStartMarker;
        skipEnd = skipEndMarker;
    }

    @Override
    protected int gobbleMaxWithoutEscape(int maxOff) throws MalformedSourceException {
        Matcher start = skipStart.matcher(input).region(this.bufpos, maxOff);
        if (start.find()) {
            Matcher end = skipEnd.matcher(input).region(start.end(), maxOff);
            if (end.find()) {
                return recordEscape(start.start(), end.end(), Chars.EMPTY);
            }
        }
        return super.gobbleMaxWithoutEscape(maxOff);
    }
}
