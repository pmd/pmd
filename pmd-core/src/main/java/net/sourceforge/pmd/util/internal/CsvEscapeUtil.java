/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.internal;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * @since 7.19.0
 */
public final class CsvEscapeUtil {
    private CsvEscapeUtil() {}

    /**
     * Escape a CSV column. Makes potential formula-sensitive characters safe. Always quotes the column.
     * @param s
     * @return
     * @since 7.29.0
     */
    public static String quoteAndEscapeCsvColumn(String s) {
        if (s == null) {
            s = "";
        }

        if (!s.isEmpty()) {
            char firstCharacter = s.charAt(0);
            // escape formula-sensitive metacharacters
            if (firstCharacter == '=' || firstCharacter == '+' || firstCharacter == '-' || firstCharacter == '@') {
                s = "'" + s;
            }
        }

        String escapeCsv = StringEscapeUtils.escapeCsv(s);
        if (!escapeCsv.isEmpty() && escapeCsv.charAt(0) == '"') {
            return escapeCsv;
        }
        return "\"" + escapeCsv + "\"";
    }
}
