package net.sourceforge.pmd.eclipse.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.util.StringUtil;

/**
 * 
 * @author Brian Remedios
 */
public class StringArranger {

	private String indentString;
	
	private static final char CR = '\n';
	
	public StringArranger(String theIndent) {
		indentString = theIndent;
	}
	
	public String withIndent(String rawText) {
		
		return indentString + rawText;
	}
	
	public StringBuilder format(String rawText) {
		
		StringBuilder sb = new StringBuilder();
		for (String line : trimmedLinesIn(rawText)) {
			sb.append(indentString);
			sb.append(line).append(CR);
		}
		
		return sb;
	}
	
	public List<String> trimmedLinesIn(String text) {
		
		String[] lines = text.split("\n");
		if (lines.length < 1) return Collections.emptyList();
		
		List<String> lineSet = new ArrayList<String>(lines.length);
		
		int startLine = 0;
		while (startLine < lines.length && StringUtil.isEmpty(lines[startLine])) startLine++;
		
		int endLine = lines.length-1;
		while (endLine >= 0 && StringUtil.isEmpty(lines[endLine])) endLine--;
		
		for (int i=startLine; i<=endLine; i++) {
			lineSet.add( lines[i].trim() );
		}
		return lineSet;
	}
}
