/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;


import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

public final class AssertionUtil {

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("\\w+(\\.\\w+)*|");

    private AssertionUtil() {
        // utility class
    }

    public static boolean isValidJavaPackageName(CharSequence name) {
        requireParamNotNull("name", name);
        return PACKAGE_PATTERN.matcher(name).matches();
    }

    public static boolean isJavaBinaryName(CharSequence name) {
        return name.length() > 0 && PACKAGE_PATTERN.matcher(name).matches();
    }

    private static boolean isValidRange(int startInclusive, int endExclusive, int minIndex, int maxIndex) {
        return startInclusive <= endExclusive && minIndex <= startInclusive && endExclusive <= maxIndex;
    }

    private static String invalidRangeMessage(int startInclusive, int endExclusive, int minIndex, int maxIndex) {
        return "Invalid range [" + startInclusive + "," + endExclusive + "[ in [" + minIndex + "," + maxIndex + "[";
    }

    /**
     * @throws IllegalArgumentException if [startInclusive,endExclusive[ is
     *                                  not a valid substring range for the given string
     */
    public static void validateStringRange(CharSequence string, int startInclusive, int endExclusive) {
        if (!isValidRange(startInclusive, endExclusive, 0, string.length())) {
            throw new IllegalArgumentException(invalidRangeMessage(startInclusive, endExclusive, 0, string.length()));
        }
    }

    /**
     * Like {@link #validateStringRange(CharSequence, int, int)} but eliminated
     * at runtime if running without assertions.
     */
    public static void assertValidStringRange(CharSequence string, int startInclusive, int endExclusive) {
        assert isValidRange(startInclusive, endExclusive, 0, string.length())
            : invalidRangeMessage(startInclusive, endExclusive, 0, string.length());
    }

    /**
     * Returns true if the charsequence is a valid java identifier.
     *
     * @param name Name (non-null)
     *
     * @throws NullPointerException If the name is null
     */
    public static boolean isJavaIdentifier(CharSequence name) {
        int len = name.length();
        if (len == 0 || !Character.isJavaIdentifierStart(name.charAt(0))) {
            return false;
        }

        for (int i = 1; i < len; i++) {
            if (!Character.isJavaIdentifierPart(name.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static int requireOver1(String name, final int value) {
        if (value < 1) {
            throw mustBe(name, value, ">= 1");
        }
        return value;
    }

    public static int requireNonNegative(String name, int value) {
        if (value < 0) {
            throw mustBe(name, value, "non-negative");
        }
        return value;
    }

    public static RuntimeException mustBe(String name, Object value, String condition) {
        return new IllegalArgumentException(String.format("%s must be %s, got %s", name, condition, value));
    }

    @NonNull
    public static <T> T requireParamNotNull(String paramName, T obj) {
        if (obj == null) {
            throw new NullPointerException("Parameter " + paramName + " is null");
        }

        return obj;
    }

}
