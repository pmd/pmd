/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.Locale;

/**
 * This represents an exclusion of a line range from parsing.
 */
public final class ParsingExclusion {

    public static final String BEGIN_MARKER = "PMD-EXCLUDE-BEGIN";
    public static final String END_MARKER = "PMD-EXCLUDE-END";
    public static final int LEN_BEGIN_MARKER = BEGIN_MARKER.length();

    public int beginLine;
    public int endLine;
    public String source = null;


    /**
     * The reason is the comment text in the first line after the beginMarker.
     */
    public String reason = null;

    public ParsingExclusion(int beginLine, int endLine, String source) {
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.source = source;
        final String sourceUpper = source.toUpperCase(Locale.US);
        int i1 = sourceUpper.indexOf(BEGIN_MARKER);
        int i2 = sourceUpper.indexOf("\n");
        this.reason = source.substring(i1 + LEN_BEGIN_MARKER, i2).trim();
        if (this.reason.startsWith(":")) {
            this.reason = this.reason.substring(2).trim(); 
        }
    }
}
