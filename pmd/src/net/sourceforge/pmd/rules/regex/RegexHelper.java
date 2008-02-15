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
		List<Pattern> patterns;
		if (list != null && list.size() > 0) {
			patterns = new ArrayList<Pattern>(list.size());
			for (String stringPattern : list) {
				if ( stringPattern != null && ! "".equals(stringPattern) ) {
					patterns.add(Pattern.compile(stringPattern));
				}
			}
		}
		else
			patterns = new ArrayList<Pattern>(0);
		return patterns;
	}

	/**
	 * <p>Simple commidity method (also designed to increase readability of source code,
	 * and to decrease import in the calling class).</p>
	 * <p>Provide a pattern and a subject, it'll do the proper matching.</p>
	 *
	 * @param pattern, a compiled regex pattern.
	 * @param subject, a String to match
	 * @return
	 */
 	public static boolean isMatch(Pattern pattern,String subject) {
 		if ( subject != null && "".equals(subject) ) {
	        Matcher matcher = pattern.matcher(subject);
	        if (matcher.find()) {
	            return true;
	        }
 		}
		return false;
	}


}
