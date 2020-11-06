/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

public final class EscapeUtils {
    private static final String BACKTICK = "`";
    private static final String URL_START = "<http";
    private static final String QUOTE_START = ">";
    private static final Pattern RULE_TAG = Pattern.compile("\\{%\\s+rule\\s+&quot;([^&]+)&quot;\\s*\\%}");

    private EscapeUtils() {
        // This is a utility class
    }

    public static String escapeMarkdown(String unescaped) {
        return unescaped.replace("\\", "\\\\")
                .replace("*", "\\*")
                .replace("_", "\\_")
                .replace("~", "\\~")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("|", "\\|");
    }

    public static String escapeSingleLine(String line) {
        StringBuilder escaped = new StringBuilder(line.length() + 16);

        String currentLine = line;
        if (currentLine.startsWith(QUOTE_START)) {
            escaped.append(currentLine.substring(0, 1));
            currentLine = currentLine.substring(1);
        }

        int url = currentLine.indexOf(URL_START);
        while (url > -1) {
            String before = currentLine.substring(0, url);
            before = escapeBackticks(escaped, before);
            escaped.append(StringEscapeUtils.escapeHtml4(before));
            int urlEnd = currentLine.indexOf(">", url) + 1;
            // add the url unescaped
            escaped.append(currentLine.substring(url, urlEnd));
            currentLine = currentLine.substring(urlEnd);
            url = currentLine.indexOf(URL_START);
        }

        currentLine = escapeBackticks(escaped, currentLine);
        escaped.append(StringEscapeUtils.escapeHtml4(currentLine));
        return escaped.toString();
    }

    private static String escapeBackticks(StringBuilder escaped, String linePart) {
        String currentLine = linePart;
        int pos = currentLine.indexOf(BACKTICK);
        boolean needsEscaping = true;
        while (pos > -1) {
            String before = currentLine.substring(0, pos);
            if (needsEscaping) {
                escaped.append(StringEscapeUtils.escapeHtml4(before));
                escaped.append(BACKTICK);
                needsEscaping = false;
            } else {
                escaped.append(before);
                escaped.append(BACKTICK);
                needsEscaping = true;
            }
            currentLine = currentLine.substring(pos + 1);
            pos = currentLine.indexOf(BACKTICK);
        }
        return currentLine;
    }

    public static List<String> escapeLines(List<String> lines) {
        boolean needsEscape = true;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("```")) {
                needsEscape = !needsEscape;
            }
            if (needsEscape && !line.startsWith("    ")) {
                line = escapeSingleLine(line);
                line = preserveRuleTagQuotes(line);
            }
            lines.set(i, line);
        }
        return lines;
    }

    /**
     * If quotes are used for rule tags, e.g. {@code {% rule "OtherRule" %}}, these
     * quotes might have been escaped for html, but it's actually markdown/jekyll/liquid
     * and not html. This undoes the escaping.
     * @param text the already escaped text that might contain rule tags
     * @return text with the fixed rule tags
     */
    public static String preserveRuleTagQuotes(String text) {
        Matcher m = RULE_TAG.matcher(text);
        return m.replaceAll("{% rule \"$1\" %}");
    }
}
