package net.sourceforge.pmd.rules.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>A simple helper class to regroup a bunch of method
 * generally used by rules using regex.</p>
 *
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class RegexHelper {

	/**
	 * Default private empty constructors
	 */
	private RegexHelper() {}

	/**
	 * <p>Compile a List of regex
	 * @param list
	 * @return
	 */
	public static List<Pattern> compilePatternFromList(List<String> list) {
		List<Pattern> patterns = new ArrayList<Pattern>(list.size());
		for (String stringPattern : list) {
			if ( stringPattern != null && stringPattern.length() > 0 ) {
				patterns.add(Pattern.compile(stringPattern));
			} else {
				throw new IllegalArgumentException("The following pattern is not valid:" + stringPattern);
			}
		}
		return patterns;
	}

 	public static boolean isMatch(Pattern pattern,String image) {
        Matcher matcher = pattern.matcher(image);
        if (matcher.find()) {
            return true;
        }
		return false;
	}


}
