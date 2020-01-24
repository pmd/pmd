/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple helper class to regroup a bunch of method generally used by rules
 * using regex.
 *
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public final class RegexHelper {

    /**
     * Default private empty constructors
     */
    private RegexHelper() {
    }

    /**
     * Compiles a list of regex into a list of patterns.
     *
     * @param list
     *            the regex list
     * @return the pattern list
     */
    public static List<Pattern> compilePatternsFromList(List<String> list) {
        List<Pattern> patterns;
        if (list != null && !list.isEmpty()) {
            patterns = new ArrayList<>(list.size());
            for (String stringPattern : list) {
                if (stringPattern != null && !"".equals(stringPattern)) {
                    patterns.add(Pattern.compile(stringPattern));
                }
            }
        } else {
            patterns = new ArrayList<>(0);
        }
        return patterns;
    }

    /**
     * Simple commodity method (also designed to increase readability of source
     * code, and to decrease import in the calling class). Provide a pattern and
     * a subject, it'll do the proper matching.
     *
     * @param pattern
     *            a compiled regex pattern
     * @param subject
     *            a String to match
     * @return {@code true} if there is a match; {@code false} otherwise
     */
    public static boolean isMatch(Pattern pattern, String subject) {
        if (subject != null && !"".equals(subject)) {
            Matcher matcher = pattern.matcher(subject);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

}
