/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * A number of String-specific utility methods for use by PMD or its IDE
 * plugins.
 *
 * @author BrianRemedios
 * @deprecated Is internal API
 */
@Deprecated
@InternalApi
public final class StringUtil {

    private static final String[] EMPTY_STRINGS = new String[0];

    private static final Pattern XML_10_INVALID_CHARS = Pattern.compile(
            "\\x00|\\x01|\\x02|\\x03|\\x04|\\x05|\\x06|\\x07|\\x08|"
          + "\\x0b|\\x0c|\\x0e|\\x0f|"
          + "\\x10|\\x11|\\x12|\\x13|\\x14|\\x15|\\x16|\\x17|\\x18|"
          + "\\x19|\\x1a|\\x1b|\\x1c|\\x1d|\\x1e|\\x1f");

    private StringUtil() {
    }


    /**
     * Returns the (1-based) line number of the character at the given index.
     * Line terminators (\r, \n) are assumed to be on the line they *end*
     * and not on the following line. The method also accepts that the given
     * offset be the length of the string (in which case there's no targeted character),
     * to get the line number of a character that would be inserted at
     * the end of the string.
     *
     * <pre>
     *
     *     lineNumberAt("a\nb", 0)  = 1
     *     lineNumberAt("a\nb", 1)  = 1
     *     lineNumberAt("a\nb", 2)  = 2
     *     lineNumberAt("a\nb", 3)  = 2  // charAt(3) doesn't exist though
     *     lineNumberAt("a\nb", 4)  = -1
     *
     *     lineNumberAt("", 0) = 1
     *     lineNumberAt("", _) = -1
     *
     * </pre>
     *
     * @param charSeq         Char sequence
     * @param offsetInclusive Offset in the sequence of the targeted character.
     *                        May be the length of the sequence.
     * @return -1 if the offset is not in {@code [0, length]}, otherwise
     * the line number
     */
    public static int lineNumberAt(CharSequence charSeq, int offsetInclusive) {
        int len = charSeq.length();

        if (offsetInclusive > len || offsetInclusive < 0) {
            return -1;
        }

        int l = 1;
        for (int curOffset = 0; curOffset < offsetInclusive; curOffset++) {
            // if we end up outside the string, then the line is undefined
            if (curOffset >= len) {
                return -1;
            }

            char c = charSeq.charAt(curOffset);
            if (c == '\n') {
                l++;
            } else if (c == '\r') {
                if (curOffset + 1 < len && charSeq.charAt(curOffset + 1) == '\n') {
                    if (curOffset == offsetInclusive - 1) {
                        // the CR is assumed to be on the same line as the LF
                        return l;
                    }
                    curOffset++; // SUPPRESS CHECKSTYLE jump to after the \n
                }
                l++;
            }
        }
        return l;
    }

    /**
     * Returns the (1-based) column number of the character at the given index.
     * Line terminators are by convention taken to be part of the line they end,
     * and not the new line they start. Each character has width 1 (including {@code \t}).
     * The method also accepts that the given offset be the length of the
     * string (in which case there's no targeted character), to get the column
     * number of a character that would be inserted at the end of the string.
     *
     * <pre>
     *
     *     columnNumberAt("a\nb", 0)  = 1
     *     columnNumberAt("a\nb", 1)  = 2
     *     columnNumberAt("a\nb", 2)  = 1
     *     columnNumberAt("a\nb", 3)  = 2   // charAt(3) doesn't exist though
     *     columnNumberAt("a\nb", 4)  = -1
     *
     *     columnNumberAt("a\r\n", 2)  = 3
     *
     * </pre>
     *
     * @param charSeq         Char sequence
     * @param offsetInclusive Offset in the sequence
     * @return -1 if the offset is not in {@code [0, length]}, otherwise
     * the column number
     */
    public static int columnNumberAt(CharSequence charSeq, final int offsetInclusive) {
        if (offsetInclusive == charSeq.length()) {
            return charSeq.length() == 0 ? 1 : 1 + columnNumberAt(charSeq, offsetInclusive - 1);
        } else if (offsetInclusive > charSeq.length() || offsetInclusive < 0) {
            return -1;
        }

        int col = 0;
        char next = 0;
        for (int i = offsetInclusive; i >= 0; i--) {
            char c = charSeq.charAt(i);

            if (offsetInclusive != i) {
                if (c == '\n' || c == '\r' && next != '\n') {
                    return col;
                }
            }

            col++;
            next = c;
        }
        return col;
    }

    /**
     * Formats a double to a percentage, keeping {@code numDecimal} decimal places.
     *
     * @param val         a double value between 0 and 1
     * @param numDecimals The number of decimal places to keep
     *
     * @return A formatted string
     *
     * @throws IllegalArgumentException if the double to format is not between 0 and 1
     */
    public static String percentageString(double val, int numDecimals) {
        if (val < 0 || val > 1) {
            throw new IllegalArgumentException("Expected a number between 0 and 1");
        }

        return String.format(Locale.ROOT, "%." + numDecimals + "f%%", 100 * val);
    }


    /**
     * Checks for the existence of any of the listed prefixes on the non-null
     * text and removes them.
     *
     * @return String
     */
    public static String withoutPrefixes(String text, String... prefixes) {

        for (String prefix : prefixes) {
            if (text.startsWith(prefix)) {
                return text.substring(prefix.length());
            }
        }

        return text;
    }


    /**
     * @param supportUTF8 override the default setting, whether special characters should be replaced with entities (
     *                    <code>false</code>) or should be included as is ( <code>true</code>).
     * @deprecated for removal. Use {@link StringEscapeUtils#escapeXml10(String)} instead.
     */
    @Deprecated
    public static void appendXmlEscaped(StringBuilder buf, String src, boolean supportUTF8) {
        char c;
        int i = 0;
        while (i < src.length()) {
            c = src.charAt(i++);
            if (c > '~') {
                // 126
                if (!supportUTF8) {
                    int codepoint = c;
                    // surrogate characters are not allowed in XML
                    if (Character.isHighSurrogate(c)) {
                        char low = src.charAt(i++);
                        codepoint = Character.toCodePoint(c, low);
                    }
                    buf.append("&#x").append(Integer.toHexString(codepoint)).append(';');
                } else {
                    buf.append(c);
                }
            } else if (c == '&') {
                buf.append("&amp;");
            } else if (c == '"') {
                buf.append("&quot;");
            } else if (c == '<') {
                buf.append("&lt;");
            } else if (c == '>') {
                buf.append("&gt;");
            } else {
                buf.append(c);
            }
        }
    }

    /**
     * Remove characters, that are not allowed in XML 1.0 documents.
     *
     * <p>Allowed characters are:
     * <blockquote>
     * Char    ::=      #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
     *  // any Unicode character, excluding the surrogate blocks, FFFE, and FFFF.
     * </blockquote>
     * (see <a href="https://www.w3.org/TR/xml/#charsets">Extensible Markup Language (XML) 1.0 (Fifth Edition)</a>).
     */
    public static String removedInvalidXml10Characters(String text) {
        Matcher matcher = XML_10_INVALID_CHARS.matcher(text);
        return matcher.replaceAll("");
    }

    /**
     * Replace some whitespace characters so they are visually apparent.
     *
     * @return String
     */
    public static String escapeWhitespace(Object o) {

        if (o == null) {
            return null;
        }
        String s = String.valueOf(o);
        s = s.replace("\n", "\\n");
        s = s.replace("\r", "\\r");
        s = s.replace("\t", "\\t");
        return s;
    }


    /**
     * Determine the maximum number of common leading whitespace characters the
     * strings share in the same sequence. Useful for determining how many
     * leading characters can be removed to shift all the text in the strings to
     * the left without misaligning them.
     *
     * @throws NullPointerException If the parameter is null
     */
    public static int maxCommonLeadingWhitespaceForAll(String[] strings) {

        int shortest = lengthOfShortestIn(strings);
        if (shortest == 0) {
            return 0;
        }

        char[] matches = new char[shortest];

        for (int m = 0; m < matches.length; m++) {
            matches[m] = strings[0].charAt(m);
            if (!Character.isWhitespace(matches[m])) {
                return m;
            }
            for (String str : strings) {
                if (str.charAt(m) != matches[m]) {
                    return m;
                }
            }
        }

        return shortest;
    }


    /**
     * Return the length of the shortest string in the array. If the collection
     * is empty or any one of them is null then it returns 0.
     *
     * @throws NullPointerException If the parameter is null
     */
    public static int lengthOfShortestIn(String[] strings) {

        if (strings.length == 0) {
            return 0;
        }

        int minLength = Integer.MAX_VALUE;

        for (String string : strings) {
            if (string == null) {
                return 0;
            }
            minLength = Math.min(minLength, string.length());
        }

        return minLength;
    }


    /**
     * Trims off the leading characters off the strings up to the trimDepth
     * specified. Returns the same strings if trimDepth = 0
     *
     * @return String[]
     */
    public static String[] trimStartOn(String[] strings, int trimDepth) {

        if (trimDepth == 0) {
            return strings;
        }

        String[] results = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            results[i] = strings[i].substring(trimDepth);
        }
        return results;
    }


    /**
     * Are the two String values the same. The Strings can be optionally trimmed
     * before checking. The Strings can be optionally compared ignoring case.
     * The Strings can be have embedded whitespace standardized before
     * comparing. Two null values are treated as equal.
     *
     * @param s1                    The first String.
     * @param s2                    The second String.
     * @param trim                  Indicates if the Strings should be trimmed before comparison.
     * @param ignoreCase            Indicates if the case of the Strings should ignored during comparison.
     * @param standardizeWhitespace Indicates if the embedded whitespace should be standardized before comparison.
     *
     * @return <code>true</code> if the Strings are the same, <code>false</code> otherwise.
     */
    public static boolean isSame(String s1, String s2, boolean trim, boolean ignoreCase,
                                 boolean standardizeWhitespace) {
        if (s1 == null && s2 == null) {
            return true;
        } else if (s1 == null || s2 == null) {
            return false;
        } else {
            if (trim) {
                s1 = s1.trim();
                s2 = s2.trim();
            }
            if (standardizeWhitespace) {
                // Replace all whitespace with a standard single space
                // character.
                s1 = s1.replaceAll("\\s+", " ");
                s2 = s2.replaceAll("\\s+", " ");
            }
            return ignoreCase ? s1.equalsIgnoreCase(s2) : s1.equals(s2);
        }
    }


    /**
     * Formats all items onto a string with separators if more than one exists,
     * return an empty string if the items are null or empty.
     *
     * @param items     Object[]
     * @param separator String
     *
     * @return String
     */
    public static String asString(Object[] items, String separator) {

        if (items == null || items.length == 0) {
            return "";
        }
        if (items.length == 1) {
            return items[0].toString();
        }

        StringBuilder sb = new StringBuilder(items[0].toString());
        for (int i = 1; i < items.length; i++) {
            sb.append(separator).append(items[i]);
        }

        return sb.toString();
    }


    /**
     * Returns an empty array of string
     *
     * @return String
     */
    public static String[] getEmptyStrings() {
        return EMPTY_STRINGS;
    }


    /**
     * Replaces unprintable characters by their escaped (or unicode escaped)
     * equivalents in the given string
     */
    public static String escapeJava(String str) {
        StringBuilder retval = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            final char ch = str.charAt(i);
            switch (ch) {
            case 0:
                break;
            case '\b':
                retval.append("\\b");
                break;
            case '\t':
                retval.append("\\t");
                break;
            case '\n':
                retval.append("\\n");
                break;
            case '\f':
                retval.append("\\f");
                break;
            case '\r':
                retval.append("\\r");
                break;
            case '\"':
                retval.append("\\\"");
                break;
            case '\'':
                retval.append("\\'");
                break;
            case '\\':
                retval.append("\\\\");
                break;
            default:
                if (ch < 0x20 || ch > 0x7e) {
                    String s = "0000" + Integer.toString(ch, 16);
                    retval.append("\\u").append(s.substring(s.length() - 4));
                } else {
                    retval.append(ch);
                }
                break;
            }
        }
        return retval.toString();
    }


    public enum CaseConvention {
        /** SCREAMING_SNAKE_CASE. */
        SCREAMING_SNAKE_CASE {
            @Override
            List<String> toWords(String name) {
                return CollectionUtil.map(name.split("_"), s -> s.toLowerCase(Locale.ROOT));
            }

            @Override
            String joinWords(List<String> words) {
                return words.stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.joining("_"));
            }
        },
        /** camelCase. */
        CAMEL_CASE {
            @Override
            List<String> toWords(String name) {
                return PASCAL_CASE.toWords(name);
            }

            @Override
            String joinWords(List<String> words) {
                if (words.isEmpty()) {
                    return "";
                }
                return words.get(0).toLowerCase(Locale.ROOT) + PASCAL_CASE.joinWords(words.subList(1, words.size()));
            }
        },
        /** PascalCase. */
        PASCAL_CASE {
            @Override
            List<String> toWords(String name) {
                return CollectionUtil.map(name.split("(?<![A-Z])(?=[A-Z])"), s -> s.toLowerCase(Locale.ROOT));
            }

            @Override
            String joinWords(List<String> words) {
                return words.stream().map(StringUtils::capitalize).collect(Collectors.joining());
            }
        },
        /** space separated. */
        SPACE_SEPARATED {
            @Override
            List<String> toWords(String name) {
                return CollectionUtil.map(name.split("\\s++"), s -> s.toLowerCase(Locale.ROOT));
            }

            @Override
            String joinWords(List<String> words) {
                return String.join(" ", words);
            }
        };

        /** Split a name written with this convention into a list of *lowercase* words. */
        abstract List<String> toWords(String name);

        /** Takes a list of lowercase words and joins them into a name following this convention. */
        abstract String joinWords(List<String> words);

        public String convertTo(CaseConvention to, String name) {
            return to.joinWords(toWords(name));
        }
    }
}
