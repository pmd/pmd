package net.sourceforge.pmd.eclipse.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Brian Remedios
 */
public class PageBuilder {

	private List<int[]> headingSpans;
	private List<int[]> codeSpans;
	private StringBuilder buffer;
	
	private Color headingColor;
	private int   indentDepth;
	
	private static final char CR = '\n';
	private static final Color BACKGROUND = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	
	public PageBuilder(int textIndent, int headingColorIndex) {
		buffer = new StringBuilder();
		indentDepth = textIndent;
		headingColor = Display.getCurrent().getSystemColor(headingColorIndex);
	}
	
	public void clear() {
		
		buffer.setLength(0);
		if (headingSpans != null) {
			headingSpans.clear();
		}
	}
	
	public void addText(String text) {
		
		for (int i=0; i<indentDepth; i++) buffer.append(' ');
		buffer.append(text).append(CR);
	}
	
	public void addRawText(String text) {
		
		buffer.append(text);
	}
	
	public void addHeading(String heading) {
		
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
		
		widget.setText( buffer.toString() );
		
		StyleRange[] styles = new StyleRange[headingSpans.size()];
		
		for (int i=0; i<styles.length; i++) {
			int[] span = headingSpans.get(i);
			styles[i] = new StyleRange(span[0], span[1]-span[0], headingColor, BACKGROUND, SWT.BOLD);
			}
		
		widget.setStyleRanges(styles);
	}
}
