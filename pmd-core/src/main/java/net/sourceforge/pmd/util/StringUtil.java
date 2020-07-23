/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
     * Return whether the non-null text arg starts with any of the prefix
     * values.
     *
     * @return boolean
     *
     * @deprecated {@link StringUtils#startsWithAny(CharSequence, CharSequence...)}
     */
    @Deprecated
    public static boolean startsWithAny(String text, String... prefixes) {

        for (String prefix : prefixes) {
            if (text.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Returns whether the non-null text arg matches any of the test values.
     *
     * @return boolean
     */
    public static boolean isAnyOf(String text, String... tests) {

        for (String test : tests) {
            if (text.equals(test)) {
                return true;
            }
        }

        return false;
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
     * @param value String
     *
     * @return boolean
     *
     * @deprecated {@link StringUtils#isNotBlank(CharSequence)}
     */
    @Deprecated
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }


    /**
     * Returns true if the value arg is either null, empty, or full of
     * whitespace characters. More efficient that calling
     * (string).trim().length() == 0.
     *
     * @param value String to test
     *
     * @return <code>true</code> if the value is empty, <code>false</code> otherwise.
     *
     * @deprecated {@link StringUtils#isBlank(CharSequence)}
     */
    @Deprecated
    public static boolean isEmpty(String value) {
        return StringUtils.isBlank(value);
    }


    /**
     * Returns true if the argument is null or the empty string.
     *
     * @param value String to test
     *
     * @return True if the argument is null or the empty string
     *
     * @deprecated {@link StringUtils#isEmpty(CharSequence)}
     */
    @Deprecated
    public static boolean isMissing(String value) {
        return StringUtils.isEmpty(value);
    }


    /**
     * Returns true if both strings are effectively null or whitespace, returns
     * false otherwise if they have actual text that differs.
     *
     * @return boolean
     */
    @Deprecated
    public static boolean areSemanticEquals(String a, String b) {

        if (a == null) {
            return isEmpty(b);
        }
        if (b == null) {
            return isEmpty(a);
        }

        return a.equals(b);
    }


    /**
     * @param original  String
     * @param oldString String
     * @param newString String
     *
     * @return String
     *
     * @deprecated {@link StringUtils#replace(String, String, String)}
     */
    @Deprecated
    public static String replaceString(final String original, final String oldString, final String newString) {
        int index = original.indexOf(oldString);
        if (index < 0) {
            return original;
        } else {
            final String replace = newString == null ? "" : newString;
            final StringBuilder buf = new StringBuilder(Math.max(16, original.length() + replace.length()));
            int last = 0;
            while (index != -1) {
                buf.append(original.substring(last, index));
                buf.append(replace);
                last = index + oldString.length();
                index = original.indexOf(oldString, last);
            }
            buf.append(original.substring(last));
            return buf.toString();
        }
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
     * @param original  String
     * @param oldChar   char
     * @param newString String
     *
     * @return String
     *
     * @deprecated {@link StringUtils#replace(String, String, String)} or {@link StringUtils#replaceChars(String, char, char)}
     */
    @Deprecated
    public static String replaceString(final String original, char oldChar, final String newString) {
        int index = original.indexOf(oldChar);
        if (index < 0) {
            return original;
        } else {
            final String replace = newString == null ? "" : newString;
            final StringBuilder buf = new StringBuilder(Math.max(16, original.length() + replace.length()));
            int last = 0;
            while (index != -1) {
                buf.append(original.substring(last, index));
                buf.append(replace);
                last = index + 1;
                index = original.indexOf(oldChar, last);
            }
            buf.append(original.substring(last));
            return buf.toString();
        }
    }


    /**
     * Parses the input source using the delimiter specified. This method is
     * much faster than using the StringTokenizer or String.split(char) approach
     * and serves as a replacement for String.split() for JDK1.3 that doesn't
     * have it.
     *
     * @param source    String
     * @param delimiter char
     *
     * @return String[]
     *
     * @deprecated {@link StringUtils#split(String, char)}
     */
    @Deprecated
    public static String[] substringsOf(String source, char delimiter) {

        if (source == null || source.length() == 0) {
            return EMPTY_STRINGS;
        }

        int delimiterCount = 0;
        int length = source.length();
        char[] chars = source.toCharArray();

        for (int i = 0; i < length; i++) {
            if (chars[i] == delimiter) {
                delimiterCount++;
            }
        }

        if (delimiterCount == 0) {
            return new String[] {source};
        }

        String[] results = new String[delimiterCount + 1];

        int i = 0;
        int offset = 0;

        while (offset <= length) {
            int pos = source.indexOf(delimiter, offset);
            if (pos < 0) {
                pos = length;
            }
            results[i++] = pos == offset ? "" : source.substring(offset, pos);
            offset = pos + 1;
        }

        return results;
    }


    /**
     * Much more efficient than StringTokenizer.
     *
     * @param str       String
     * @param separator char
     *
     * @return String[]
     *
     * @deprecated {@link StringUtils#split(String, String)}
     */
    @Deprecated
    public static String[] substringsOf(String str, String separator) {

        if (str == null || str.length() == 0) {
            return EMPTY_STRINGS;
        }

        int index = str.indexOf(separator);
        if (index == -1) {
            return new String[] {str};
        }

        List<String> list = new ArrayList<>();
        int currPos = 0;
        int len = separator.length();
        while (index != -1) {
            list.add(str.substring(currPos, index));
            currPos = index + len;
            index = str.indexOf(separator, currPos);
        }
        list.add(str.substring(currPos));
        return list.toArray(new String[0]);
    }


    /**
     * Copies the elements returned by the iterator onto the string buffer each
     * delimited by the separator.
     *
     * @param sb        StringBuffer
     * @param iter      Iterator
     * @param separator String
     *
     * @deprecated {@link StringUtils#join(Iterator, String)}
     */
    @Deprecated
    public static void asStringOn(StringBuffer sb, Iterator<?> iter, String separator) {

        if (!iter.hasNext()) {
            return;
        }

        sb.append(iter.next());

        while (iter.hasNext()) {
            sb.append(separator);
            sb.append(iter.next());
        }
    }


    /**
     * Copies the array items onto the string builder each delimited by the
     * separator. Does nothing if the array is null or empty.
     *
     * @param sb        StringBuilder
     * @param items     Object[]
     * @param separator String
     *
     * @deprecated {@link StringUtils#join(Iterable, String)}
     */
    @Deprecated
    public static void asStringOn(StringBuilder sb, Object[] items, String separator) {

        if (items == null || items.length == 0) {
            return;
        }

        sb.append(items[0]);

        for (int i = 1; i < items.length; i++) {
            sb.append(separator);
            sb.append(items[i]);
        }
    }


    /**
     * Determine the maximum number of common leading whitespace characters the
     * strings share in the same sequence. Useful for determining how many
     * leading characters can be removed to shift all the text in the strings to
     * the left without misaligning them.
     *
     * @param strings String[]
     *
     * @return int
     */
    public static int maxCommonLeadingWhitespaceForAll(String[] strings) {

        int shortest = lengthOfShortestIn(strings);
        if (shortest == 0) {
            return 0;
        }

        char[] matches = new char[shortest];

        String str;
        for (int m = 0; m < matches.length; m++) {
            matches[m] = strings[0].charAt(m);
            if (!Character.isWhitespace(matches[m])) {
                return m;
            }
            for (int i = 0; i < strings.length; i++) {
                str = strings[i];
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
     * @param strings String[]
     *
     * @return int
     */
    public static int lengthOfShortestIn(String[] strings) {

        if (CollectionUtil.isEmpty(strings)) {
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
     * Left pads a string.
     *
     * @param s      The String to pad
     * @param length The desired minimum length of the resulting padded String
     *
     * @return The resulting left padded String
     *
     * @deprecated {@link StringUtils#leftPad(String, int)}
     */
    @Deprecated
    public static String lpad(String s, int length) {
        String res = s;
        if (length - s.length() > 0) {
            char[] arr = new char[length - s.length()];
            Arrays.fill(arr, ' ');
            res = new StringBuilder(length).append(arr).append(s).toString();
        }
        return res;
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
     * Converts the given string to Camel case,
     * that is, removing all spaces, and capitalising
     * the first letter of each word except the first.
     *
     * <p>If the first word starts with an uppercase
     * letter, it's kept as is. This method can thus
     * be used for Pascal case too.
     *
     * @param name The string to convert
     *
     * @return The string converted to Camel case
     *
     * @deprecated Use {@link CaseConvention}
     */
    @Deprecated
    public static String toCamelCase(String name) {
        return toCamelCase(name, false);
    }


    /**
     * Converts the given string to Camel case,
     * that is, removing all spaces, and capitalising
     * the first letter of each word except the first.
     *
     * <p>The second parameter can be used to force the
     * words to be converted to lowercase before capitalising.
     * This can be useful if eg the first word contains
     * several uppercase letters.
     *
     * @param name           The string to convert
     * @param forceLowerCase Whether to force removal of all upper
     *                       case letters except on word start
     *
     * @return The string converted to Camel case
     *
     * @deprecated Use {@link CaseConvention}
     */
    @Deprecated
    public static String toCamelCase(String name, boolean forceLowerCase) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String word : name.trim().split("\\s++")) {
            String pretreated = forceLowerCase ? word.toLowerCase(Locale.ROOT) : word;
            if (isFirst) {
                sb.append(pretreated);
                isFirst = false;
            } else {
                sb.append(StringUtils.capitalize(pretreated));
            }
        }
        return sb.toString();
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
