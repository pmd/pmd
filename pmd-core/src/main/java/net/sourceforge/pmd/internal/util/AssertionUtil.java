/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public final class AssertionUtil {

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("[\\w$]+(\\.[\\w$]+)*|");
    private static final Pattern BINARY_NAME_PATTERN = Pattern.compile("[\\w$]+(?:\\.[\\w$]+)*(?:\\[])*");

    private AssertionUtil() {
        // utility class
    }

    public static boolean isValidJavaPackageName(CharSequence name) {
        requireParamNotNull("name", name);
        return PACKAGE_PATTERN.matcher(name).matches();
    }

    /**
     * @throws IllegalArgumentException if the name is not a binary name
     */
    public static void assertValidJavaBinaryName(CharSequence name) {
        if (!isJavaBinaryName(name)) {
            throw new IllegalArgumentException("Not a Java binary name '" + name + "'");
        }
    }

    public static boolean isJavaBinaryName(CharSequence name) {
        return BINARY_NAME_PATTERN.matcher(name).matches();
    }


    public static <T> T requireParamNotNull(String paramName, T obj) {
        if (obj == null) {
            throw new NullPointerException("Parameter " + paramName + " is null");
        }

        return obj;
    }

    public static AssertionError shouldNotReachHere(String message) {
        String prefix = "This should be unreachable";
        message = StringUtils.isBlank(message) ? prefix
                                               : prefix + ": " + message;
        return new AssertionError(message);
    }


}
