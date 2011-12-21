package net.sourceforge.pmd.eclipse.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

/**
 * 
 * @author Brian Remedios
 */
public class PageBuilder {

	private List<int[]> 		headingSpans = new ArrayList<int[]>();
	private List<int[]> 		codeSpans = new ArrayList<int[]>();
	private Map<int[], String>	linksBySpan = new HashMap<int[], String>();
	
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
	
	public void indentDepth(int aDepth) { indentDepth = aDepth; }
	public int indentDepth() { return indentDepth; }
	
	public boolean hasLinks() { return !linksBySpan.isEmpty(); }
	
	public void clear() {
		
		buffer.setLength(0);
		if (headingSpans != null) {
			headingSpans.clear();
		}
		if (codeSpans != null) {
			codeSpans.clear();
		}
		if (linksBySpan != null) {
			linksBySpan.clear();
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
		
		int length = buffer.length();
		if (length > 0) {
			buffer.append(CR);
			length += 1;
		}
		
		headingSpans.add( new int[] { length, length + heading.length() } );
		buffer.append(heading).append(CR);
	}
	
	public void addCode(String code) {
		
		int length = buffer.length();
		
		codeSpans.add( new int[] { length, length + code.length() } );
		buffer.append(code);
	}
	
	public void addLink(String text, String link) {

		int length = buffer.length();
		
		linksBySpan.put(
				new int[] { length, length + text.length() },
				link
				);
		
		buffer.append(text);
	}
	
	private String linkAt(int textIndex) {
		
		int[] span;

		for (Map.Entry<int[], String> entry : linksBySpan.entrySet()) {
			span = entry.getKey();
			if (span[0] <= textIndex && textIndex <= span[1]) {
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	public void showOn(StyledText widget) {
		
		String text = buffer.toString();
		
		widget.setText(text);
		
		List<StyleRange> ranges = new ArrayList<StyleRange>();
		
		int[] span = null;
		
		for (int i=0; i<headingSpans.size(); i++) {
			span = headingSpans.get(i);
			ranges.add(  new StyleRange(span[0], span[1]-span[0], headingColor, BACKGROUND, SWT.BOLD) );
			}
		
		for (int[] spn : linksBySpan.keySet()) {
			StyleRange style =  new StyleRange(spn[0], spn[1]-spn[0], headingColor, BACKGROUND, SWT.UNDERLINE_LINK);
			style.underline = true;
			ranges.add(style);
			}
		
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
		
		StyleRange[] styles = sort(ranges);	// must be in order!
		widget.setStyleRanges(styles);		
	}
	
	public void addLinkHandler(final StyledText widget) {
		
		widget.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				// It is up to the application to determine when and how a link should be activated.
				// In this snippet links are activated on mouse down when the control key is held down 
		//		if ((event.stateMask & SWT.MOD1) != 0) {
					try {
						int offset = widget.getOffsetAtLocation(new Point (event.x, event.y));
						String link = linkAt(offset);
						if (link != null) {
							launchBrowser(link);
						}

					} catch (IllegalArgumentException e) {
						// no character under event.x, event.y
					}
					
				}
		//	}
		});		
		
	}
	
	private static void launchBrowser(String link) {
		try {
			IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
			browser.openURL(new URL(link));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
	}
}
