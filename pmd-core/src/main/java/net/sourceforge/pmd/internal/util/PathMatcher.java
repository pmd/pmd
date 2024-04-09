/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class PathMatcher implements Predicate<String> {

    private final Pattern pattern;
    private final String glob;

    private PathMatcher(Pattern pattern, String glob) {
        this.pattern = pattern;
        this.glob = glob;
    }

    @Override
    public String toString() {
        return glob;
    }

    @Override
    public boolean test(String path) {
        // make platform independent
        path = path.replace('\\', '/');
        return pattern.matcher(path).matches();
    }

    public static PathMatcher compileGlob(String globPattern) {
        String pat = convertGlobToRegex(globPattern);
        return new PathMatcher(Pattern.compile(pat), globPattern);
    }

    /**
     * Converts a standard POSIX Shell globbing pattern into a regular expression
     * pattern. The result can be used with the standard {@link java.util.regex} API to
     * recognize strings which match the glob pattern.
     * <p/>
     * See also, the POSIX Shell language:
     * <a href="http://pubs.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html#tag_02_13_01">...</a>
     *
     * <p> Courtesy of Neil Traft (<a href="https://stackoverflow.com/a/17369948/6245827">...</a>)
     *
     * @param pattern A glob pattern.
     *
     * @return A regex pattern to recognize the given glob pattern.
     */
    private static String convertGlobToRegex(String pattern) {
        StringBuilder sb = new StringBuilder(pattern.length());
        int inGroup = 0;
        int inClass = 0;
        int firstIndexInClass = -1;
        char[] arr = pattern.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char ch = arr[i];
            switch (ch) {
            case '\\':
                i++; // SUPPRESS CHECKSTYLE ModifiedControlVariable
                if (i >= arr.length) {
                    sb.append('\\');
                } else {
                    char next = arr[i];
                    switch (next) {
                    case ',':
                        // escape not needed
                        break;
                    case 'Q':
                    case 'E':
                        // extra escape needed
                        sb.append('\\');
                        // fallthrough
                    default:
                        sb.append('\\');
                        // fallthrough
                    }
                    sb.append(next);
                }
                break;
            case '*':
                if (inClass == 0) {
                    sb.append(".*");
                } else {
                    sb.append('*');
                }
                break;
            case '?':
                if (inClass == 0) {
                    sb.append('.');
                } else {
                    sb.append('?');
                }
                break;
            case '[':
                inClass++;
                firstIndexInClass = i + 1;
                sb.append('[');
                break;
            case ']':
                inClass--;
                sb.append(']');
                break;
            case '.':
            case '(':
            case ')':
            case '+':
            case '|':
            case '^':
            case '$':
            case '@':
            case '%':
                if (inClass == 0 || firstIndexInClass == i && ch == '^') {
                    sb.append('\\');
                }
                sb.append(ch);
                break;
            case '!':
                if (firstIndexInClass == i) {
                    sb.append('^');
                } else {
                    sb.append('!');
                }
                break;
            case '{':
                inGroup++;
                sb.append('(');
                break;
            case '}':
                inGroup--;
                sb.append(')');
                break;
            case ',':
                if (inGroup > 0) {
                    sb.append('|');
                } else {
                    sb.append(',');
                }
                break;
            default:
                sb.append(ch);
            }
        }
        return sb.toString();
    }


}
