package net.sourceforge.pmd.eclipse.ui.views.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.util.StringUtil;

public class CommentUtil {

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
	
	public static Map<String, Integer> javadocTagsIn(String comment) {
		
		int atPos = comment.indexOf('@');
		if (atPos < 0) return Collections.emptyMap();
		
		Map<String, Integer> tags = new HashMap<String, Integer>();
		while (atPos >= 0) {
			String tag = wordAfter(comment, atPos);
			tags.put(tag, atPos);
			atPos = comment.indexOf('@', atPos + tag.length());
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
