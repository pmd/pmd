/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xpath;

public class PMDFunctions {
    public static boolean matches(String s, String pattern1) {
	return MatchesFunction.matches(s, pattern1);
    }

    public static boolean matches(String s, String pattern1, String pattern2) {
	return MatchesFunction.matches(s, pattern1, pattern2);
    }

    public static boolean matches(String s, String pattern1, String pattern2, String pattern3) {
	return MatchesFunction.matches(s, pattern1, pattern2, pattern3);
    }

    public static boolean matches(String s, String pattern1, String pattern2, String pattern3, String pattern4) {
	return MatchesFunction.matches(s, pattern1, pattern2, pattern3, pattern4);
    }

    public static boolean matches(String s, String pattern1, String pattern2, String pattern3, String pattern4,
	    String pattern5) {
	return MatchesFunction.matches(s, pattern1, pattern2, pattern3, pattern4, pattern5);
    }

    public static boolean matches(String s, String pattern1, String pattern2, String pattern3, String pattern4,
	    String pattern5, String pattern6) {
	return MatchesFunction.matches(s, pattern1, pattern2, pattern3, pattern4, pattern5, pattern6);
    }
}
