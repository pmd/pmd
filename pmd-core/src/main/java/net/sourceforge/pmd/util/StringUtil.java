/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.document.Chars;

/**
 * String-related utility functions. See also {@link StringUtils}.
 *
 * @author BrianRemedios
 * @author Clément Fournier
 */
public final class StringUtil {


    private static final Pattern XML_10_INVALID_CHARS = Pattern.compile(
            "\\x00|\\x01|\\x02|\\x03|\\x04|\\x05|\\x06|\\x07|\\x08|"
          + "\\x0b|\\x0c|\\x0e|\\x0f|"
          + "\\x10|\\x11|\\x12|\\x13|\\x14|\\x15|\\x16|\\x17|\\x18|"
          + "\\x19|\\x1a|\\x1b|\\x1c|\\x1d|\\x1e|\\x1f");

    private StringUtil() {
    }

    public static String inSingleQuotes(String s) {
        if (s == null) {
            s = "";
        }
        return "'" + s + "'";
    }

    public static @NonNull String inDoubleQuotes(String expected) {
        return "\"" + expected + "\"";
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
     * Like {@link StringBuilder#append(CharSequence)}, but uses an optimized
     * implementation if the charsequence happens to be a {@link Chars}. {@link StringBuilder}
     * already optimises the cases where the charseq is a string, a StringBuilder,
     * or a stringBuffer. This is especially useful in parsers.
     */
    public static StringBuilder append(StringBuilder sb, CharSequence charSeq) {
        if (charSeq instanceof Chars) {
            ((Chars) charSeq).appendChars(sb);
            return sb;
        } else {
            return sb.append(charSeq);
        }
    }

    /**
     * Returns the substring following the last occurrence of the
     * given character. If the character doesn't occur, returns
     * the whole string. This contrasts with {@link StringUtils#substringAfterLast(String, String)},
     * which returns the empty string in that case.
     *
     * @param str String to cut
     * @param c   Delimiter
     */
    public static String substringAfterLast(String str, int c) {
        int i = str.lastIndexOf(c);
        return i < 0 ? str : str.substring(i + 1);
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
     * <p>Note: the spec is described in
     * <a href='https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/lang/String.html#stripIndent()'>String#stripIndent</a>
     *
     * <quote>
     * The minimum indentation (min) is determined as follows:
     * <ul>
     *     <li>For each non-blank line (as defined by isBlank()), the leading white space characters are counted.
     *     <li>The leading white space characters on the last line are also counted even if blank.
     * </ul>
     * The min value is the smallest of these counts.
     * </quote>
     *
     * @throws NullPointerException If the parameter is null
     */
    private static int maxCommonLeadingWhitespaceForAll(List<? extends CharSequence> lines) {
        int maxCommonWs = Integer.MAX_VALUE;
        for (int i = 0; i < lines.size(); i++) {
            CharSequence line = lines.get(i);
            // compute common prefix
            if (!StringUtils.isBlank(line) || i == lines.size() - 1) {
                maxCommonWs = Math.min(maxCommonWs, countLeadingWhitespace(line));
            }
        }
        if (maxCommonWs == Integer.MAX_VALUE) {
            // common prefix not found
            maxCommonWs = 0;
        }
        return maxCommonWs;
    }

    /**
     * Returns a list of
     */
    public static List<Chars> linesWithTrimIndent(String source) {
        List<String> lines = Arrays.asList(source.split("\n"));
        List<Chars> result = lines.stream().map(Chars::wrap).collect(CollectionUtil.toMutableList());
        trimIndentInPlace(result);
        return result;
    }

    /**
     * Trim the common indentation of each line in place in the input list.
     * Trailing whitespace is removed on each line. Note that blank lines do
     * not count towards computing the max common indentation, except
     * the last one.
     *
     * @param lines mutable list
     */
    public static void trimIndentInPlace(List<Chars> lines) {
        int trimDepth = maxCommonLeadingWhitespaceForAll(lines);
        lines.replaceAll(chars -> chars.length() >= trimDepth
                                  ? chars.subSequence(trimDepth).trimEnd()
                                  : chars.trimEnd());
    }

    /**
     * Trim common indentation in the lines of the string. Like
     * {@link #trimIndentInPlace(List)} called with the list of lines
     * and joined with {@code \n}.
     */
    public static StringBuilder trimIndent(Chars string) {
        List<Chars> lines = string.lineStream().collect(CollectionUtil.toMutableList());
        trimIndentInPlace(lines);
        return CollectionUtil.joinCharsIntoStringBuilder(lines, "\n");
    }


    private static int countLeadingWhitespace(CharSequence s) {
        int count = 0;
        while (count < s.length() && Character.isWhitespace(s.charAt(count))) {
            count++;
        }
        return count;
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
     * If the string starts and ends with the delimiter, returns the substring
     * within the delimiters. Otherwise returns the original string. The
     * start and end delimiter must be 2 separate instances.
     * <pre>{@code
     * removeSurrounding("",     _ )  = ""
     * removeSurrounding("q",   'q')  = "q"
     * removeSurrounding("qq",  'q')  = ""
     * removeSurrounding("q_q", 'q')  = "_"
     * }</pre>
     */
    public static String removeSurrounding(String string, char delimiter) {
        if (string.length() >= 2
            && string.charAt(0) == delimiter
            && string.charAt(string.length() - 1) == delimiter) {
            return string.substring(1, string.length() - 1);
        }
        return string;
    }

    /**
     * Like {@link #removeSurrounding(String, char) removeSurrounding} with
     * a double quote as a delimiter.
     */
    public static String removeDoubleQuotes(String string) {
        return removeSurrounding(string, '"');
    }

    /**
     * Truncate the given string to some maximum length. If it needs
     * truncation, the ellipsis string is appended. The length of the
     * returned string is always lower-or-equal to the maxOutputLength,
     * even when truncation occurs.
     */
    public static String elide(String string, int maxOutputLength, String ellipsis) {
        AssertionUtil.requireNonNegative("maxOutputLength", maxOutputLength);
        if (ellipsis.length() > maxOutputLength) {
            throw new IllegalArgumentException("Ellipsis too long '" + ellipsis + "', maxOutputLength=" + maxOutputLength);
        }
        if (string.length() <= maxOutputLength) {
            return string;
        }
        String truncated = string.substring(0, maxOutputLength - ellipsis.length());
        return truncated + ellipsis;
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

    /**
     * Escape the string so that it appears literally when interpreted
     * by a {@link MessageFormat}.
     */
    public static String quoteMessageFormat(String str) {
        return str.replaceAll("'", "''");
    }


    /** Return the empty string if the parameter is null. */
    public static String nullToEmpty(final String value) {
        return value == null ? "" : value;
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
