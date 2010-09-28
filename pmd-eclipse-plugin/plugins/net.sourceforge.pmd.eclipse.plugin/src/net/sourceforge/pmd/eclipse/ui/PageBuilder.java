package net.sourceforge.pmd.eclipse.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.pmd.eclipse.ui.editors.StyleExtractor;
import net.sourceforge.pmd.eclipse.ui.editors.SyntaxData;
import net.sourceforge.pmd.eclipse.ui.editors.SyntaxManager;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;
import net.sourceforge.pmd.eclipse.util.FontBuilder;
import net.sourceforge.pmd.lang.Language;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Brian Remedios
 */
public class PageBuilder {

	private List<int[]> 	headingSpans;
	private List<int[]> 	codeSpans;
	private StringBuilder 	buffer;
	
	private Color 			headingColor;
	private int   			indentDepth;
	private TextStyle		codeStyle;
	private StyleExtractor 	codeStyleExtractor;
	
	private static final char CR = '\n';
	private static final Color BACKGROUND = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	
	private static final Comparator<StyleRange> StyleComparator = new Comparator<StyleRange>() {
		public int compare(StyleRange sr1, StyleRange sr2) {
			return sr1.start - sr2.start;
		}
	};
	
	public static StyleRange[] sort(List<StyleRange> ranges) {
		
		StyleRange[] styles = ranges.toArray(new StyleRange[ranges.size()]);
		Arrays.sort(styles, StyleComparator);
		return styles;
	}
	
	public PageBuilder(int textIndent, int headingColorIndex, FontBuilder codeFontBuilder) {
		buffer = new StringBuilder(500);
		indentDepth = textIndent;
		
		Display display = Display.getCurrent();		
		headingColor = display.getSystemColor(headingColorIndex);
		codeStyle = codeFontBuilder.style(display);
		
		SyntaxData syntax = SyntaxManager.getSyntaxData("java");
		codeStyleExtractor = new StyleExtractor(syntax);
	}
	
	public void clear() {
		
		buffer.setLength(0);
		if (headingSpans != null) {
			headingSpans.clear();
		}
		if (codeSpans != null) {
			codeSpans.clear();
		}
	}
	
	public void setLanguage(Language language) {
		
		SyntaxData syntax = SyntaxManager.getSyntaxData(language.getTerseName());
		codeStyleExtractor.syntax(syntax);
	}
	
	public void addText(String text) {
		
		for (int i=0; i<indentDepth; i++) buffer.append(' ');
		buffer.append(text).append(CR);
	}
	
	public void addRawText(String text) {
		
		buffer.append(text);
	}
	
	public void addHeading(String headingKey) {
		
		String heading = SWTUtil.stringFor(headingKey);
		
		if (headingSpans == null) headingSpans = new ArrayList<int[]>();

		int length = buffer.length();
		if (length > 0) {
			buffer.append(CR);
			length += 1;
		}
		
		headingSpans.add( new int[] { length, length + heading.length() } );
		buffer.append(heading).append(CR);
	}
	
	public void addCode(String code) {
		
		if (codeSpans == null) codeSpans = new ArrayList<int[]>();

		int length = buffer.length();
		
		codeSpans.add( new int[] { length, length + code.length() } );
		buffer.append(code);
	}
	
	public void showOn(StyledText widget) {
		
		String text = buffer.toString();
		
		widget.setText(text);
		
		StyleRange[] styles = new StyleRange[headingSpans.size()];
		int[] span = null;
		
		for (int i=0; i<styles.length; i++) {
			span = headingSpans.get(i);
			styles[i] = new StyleRange(span[0], span[1]-span[0], headingColor, BACKGROUND, SWT.BOLD);
			}
		
		if (codeSpans == null) {
			widget.setStyleRanges(styles);
			return;
			}
	
		List<StyleRange> ranges = new ArrayList<StyleRange>(styles.length + codeSpans.size());
		for (StyleRange style : styles) ranges.add(style);
		
		String crStr = Character.toString(CR);
		StyleRange sr = null;
		
		for (int i=0; i<codeSpans.size(); i++) {
			span = codeSpans.get(i);
			sr = new StyleRange(codeStyle);
			sr.start = span[0];
			sr.length = span[1]-span[0];
	//		ranges.add(sr);  TODO  wtf?  causes crashes
			
			List<StyleRange> colorRanges = codeStyleExtractor.stylesFor(text, sr.start, sr.length, crStr);
			for (StyleRange range : colorRanges) {
				ranges.add(range);			
				}
			}
		
		styles = sort(ranges);	// must be in order!
		widget.setStyleRanges(styles);		
	}
}
