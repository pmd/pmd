/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.util.StringUtil;

public final class CommentUtil {

	private CommentUtil() {}

	private static final String CR = "\n";

	public static String wordAfter(String text, int position) {

		if (position >= text.length()) return null;

		int end = ++position;
		char ch = text.charAt(end);

		while (Character.isLetterOrDigit(ch) && end < text.length()) {
			ch = text.charAt(++end);
		}

		return text.substring(position, end);
	}

	public static String javadocContentAfter(String text, int position) {

		int endPos = text.indexOf('\n', position);
		if (endPos < 0) return null;

		if (StringUtil.isNotEmpty(text.substring(position, endPos))) {
			return text.substring(position, endPos).trim();
		}

		if (text.indexOf('@', endPos) >= 0) return null;	//nope, this is another entry

			// try next line
		int nextEndPos = text.indexOf('\n', endPos + 1);

		if (StringUtil.isNotEmpty(text.substring(endPos, nextEndPos))) {
			return text.substring(endPos, nextEndPos).trim();
		}

		return null;
	}

	private static Pattern JAVADOC_TAG = Pattern.compile("@[A-Za-z0-9]+");
	private static Map<String, String> JAVADOC_CACHE = new HashMap<String, String>();

	public static Map<String, Integer> javadocTagsIn(String comment) {
		Matcher m = JAVADOC_TAG.matcher(comment);
		Map<String, Integer> tags = null;
		while (m.find()) {
			if (tags == null )  { tags = new HashMap<String, Integer>(); }
			String match = comment.substring(m.start() + 1, m.end());
			String tag = JAVADOC_CACHE.get(match);
			if (tag == null) {
				JAVADOC_CACHE.put(match, match);
			}
			tags.put(tag, m.start());
 		}
                if ( tags == null ) {
                    return Collections.emptyMap();
                }
                return tags;
	}

	public static List<String> multiLinesIn(String comment) {

		String[] lines = comment.split(CR);
		List<String> filteredLines = new ArrayList<String>(lines.length);

		for (String rawLine : lines) {
			String line = rawLine.trim();

			if (line.startsWith("//")) {
				filteredLines.add(line.substring(2));
				continue;
			}

			if (line.endsWith("*/")) {
				int end = line.length()-2;
				int start = line.startsWith("/**") ? 3 : line.startsWith("/*") ? 2 : 0;
				filteredLines.add(line.substring(start, end));
				continue;
			}

			if (line.charAt(0) == '*') {
				filteredLines.add(line.substring(1));
				continue;
			}

			if (line.startsWith("/**")) {
				filteredLines.add(line.substring(3));
				continue;
			}

			if (line.startsWith("/*")) {
				filteredLines.add(line.substring(2));
				continue;
			}

			filteredLines.add(line);
		}

		return filteredLines;
	}

	/**
	 * Similar to the String.trim() function, this one removes the leading and
	 * trailing empty/blank lines from the line list.
	 *
	 * @param lines
	 */
	public static List<String> trim(List<String> lines) {

		int firstNonEmpty = 0;
		for (; firstNonEmpty<lines.size(); firstNonEmpty++) {
			if (StringUtil.isNotEmpty(lines.get(firstNonEmpty))) break;
		}

		// all of them empty?
		if (firstNonEmpty == lines.size()) return Collections.emptyList();

		int lastNonEmpty = lines.size() - 1;
		for (; lastNonEmpty>0; lastNonEmpty--) {
			if (StringUtil.isNotEmpty(lines.get(lastNonEmpty))) break;
		}

		List<String> filtered = new ArrayList<String>();
		for (int i=firstNonEmpty; i<lastNonEmpty; i++) {
			filtered.add( lines.get(i) );
		}

		return filtered;
	}

	public static void main(String[] args) {

		Collection<String> tags = javadocTagsIn(args[0]).keySet();

		for (String tag : tags) {
			System.out.println( tag );
		}
	}
}
