package net.sourceforge.pmd.eclipse.ui.editors;

import java.util.List;

import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;

/**
 * This class performs the syntax highlighting and styling for Pmpe
 */
public class BasicLineStyleListener extends StyleExtractor implements LineStyleListener {

/**
	 * PmpeLineStyleListener constructor
	 * 
	 * @param syntaxData
	 *          the syntax data to use
	 */
	public BasicLineStyleListener(SyntaxData theSyntaxData) {
		super(theSyntaxData);
	}

	/**
	 * Called by StyledText to get styles for a line
	 */
	public void lineGetStyle(LineStyleEvent event) {
		
		List<StyleRange> styles = lineStylesFor(
				event.lineText,
				event.lineOffset, 
				event.lineText.length()
				);

		event.styles = styles.toArray(new StyleRange[styles.size()]);
	}
}