/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * A number of String-specific utility methods for use by PMD or its IDE
 * plugins.
 *
 * @author BrianRemedios
 */
public final class StringUtil {

    private static final String[] EMPTY_STRINGS = new String[0];

    private StringUtil() {
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
     * @param text
     * @param prefixes
     *
     * @return boolean
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
     * @param text
     * @param tests
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
     * @param text
     * @param prefixes
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
     * @param a
     * @param b
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
     * @param buf
     * @param src
     * @param supportUTF8 override the default setting, whether special characters should be replaced with entities (
     *                    <code>false</code>) or should be included as is ( <code>true</code>).
     *
     */
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
     * Replace some whitespace characters so they are visually apparent.
     *
     * @param o
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
        return list.toArray(new String[list.size()]);
    }


    /**
     * Copies the elements returned by the iterator onto the string buffer each
     * delimited by the separator.
     *
     * @param sb        StringBuffer
     * @param iter      Iterator
     * @param separator String
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

        for (int i = 0; i < strings.length; i++) {
            if (strings[i] == null) {
                return 0;
            }
            minLength = Math.min(minLength, strings[i].length());
        }

        return minLength;
    }


    /**
     * Trims off the leading characters off the strings up to the trimDepth
     * specified. Returns the same strings if trimDepth = 0
     *
     * @param strings
     * @param trimDepth
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
}
