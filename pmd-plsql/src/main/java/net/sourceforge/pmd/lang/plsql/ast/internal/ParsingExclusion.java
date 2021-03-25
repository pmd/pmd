/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast.internal;

import java.util.Locale;

/**
 * This represents an exclusion of a line range from parsing.
 */
public final class ParsingExclusion {

    private static final String BEGIN_MARKER = "PMD-EXCLUDE-BEGIN";
    private static final String END_MARKER = "PMD-EXCLUDE-END";
    private static final int LEN_BEGIN_MARKER = BEGIN_MARKER.length();

    private final int beginLine;
    private final int endLine;
    private final String excludedSource;
    private final String reason;

    public ParsingExclusion(int beginLine, int endLine, String source) {
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.excludedSource = source;
        final String sourceUpper = source.toUpperCase(Locale.ROOT);
        int i1 = sourceUpper.indexOf(BEGIN_MARKER);
        int i2 = sourceUpper.indexOf("\n");
        String reason = source.substring(i1 + LEN_BEGIN_MARKER, i2).trim();
        if (reason.startsWith(":")) {
            this.reason = reason.substring(2).trim(); 
        } else {
            this.reason = null;
        }
    }

    public String getExcludedSource() {
        return excludedSource;
    }

    public int getBeginLine() {
        return beginLine;
    }

    public int getEndLine() {
        return endLine;
    }

    /**
     * The reason is the comment text in the first line after the beginMarker.
     */
    public String getReason() {
        return reason;
    }
}
