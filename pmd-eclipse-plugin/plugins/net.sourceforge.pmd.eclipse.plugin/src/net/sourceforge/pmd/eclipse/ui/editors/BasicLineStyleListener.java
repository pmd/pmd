package net.sourceforge.pmd.eclipse.ui.editors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * This class performs the syntax highlighting and styling for Pmpe
 */
public class BasicLineStyleListener implements LineStyleListener {

//	FIXME	take these from the Eclipse preferences instead
	private static final Color COMMENT_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
	private static final Color REFERENCED_VAR_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
	private static final Color UNREFERENCED_VAR_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
	private static final Color COMMENT_BACKGROUND = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private static final Color PUNCTUATION_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	private static final Color KEYWORD_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);
	private static final Color STRING_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);

	// Holds the syntax data
	private SyntaxData syntaxData;

	// Holds the offsets for all multiline comments
	private List<int[]> commentOffsets;

	/**
	 * PmpeLineStyleListener constructor
	 * 
	 * @param syntaxData
	 *          the syntax data to use
	 */
	public BasicLineStyleListener(SyntaxData theSyntaxData) {
		syntaxData = theSyntaxData;
		commentOffsets = new LinkedList<int[]>();
	}

	/**
	 * Refreshes the offsets for all multiline comments in the parent StyledText.
	 * The parent StyledText should call this whenever its text is modified. Note
	 * that this code doesn't ignore comment markers inside strings.
	 * 
	 * @param text
	 *          the text from the StyledText
	 */
	public void refreshMultilineComments(String text) {
		// Clear any stored offsets
		commentOffsets.clear();

		if (syntaxData != null) {
			// Go through all the instances of COMMENT_START
			for (int pos = text.indexOf(syntaxData.getMultiLineCommentStart()); pos > -1; pos = text.indexOf(syntaxData.getMultiLineCommentStart(), pos)) {
				// offsets[0] holds the COMMENT_START offset
				// and COMMENT_END holds the ending offset
				int[] offsets = new int[2];
				offsets[0] = pos;

				// Find the corresponding end comment.
				pos = text.indexOf(syntaxData.getMultiLineCommentEnd(), pos);

				// If no corresponding end comment, use the end of the text
				offsets[1] = pos == -1 ? 
						text.length() - 1 : 
							pos + syntaxData.getMultiLineCommentEnd().length() - 1;
						pos = offsets[1];
						// Add the offsets to the collection
						commentOffsets.add(offsets);
			}
		}
	}

	/**
	 * Checks to see if the specified section of text begins inside a multiline
	 * comment. Returns the index of the closing comment, or the end of the line
	 * if the whole line is inside the comment. Returns -1 if the line doesn't
	 * begin inside a comment.
	 * 
	 * @param start
	 *          the starting offset of the text
	 * @param length
	 *          the length of the text
	 * @return int
	 */
	private int getBeginsInsideComment(int start, int length) {
		// Assume section doesn't being inside a comment
		int index = -1;

		// Go through the multiline comment ranges
		for (int i = 0, n = commentOffsets.size(); i < n; i++) {
			int[] offsets = commentOffsets.get(i);

			// If starting offset is past range, quit
			if (offsets[0] > start + length)
				break;
			// Check to see if section begins inside a comment
			if (offsets[0] <= start && offsets[1] >= start) {
				// It does; determine if the closing comment marker is inside this section
				index = offsets[1] > start + length ? 
						start + length : 
						offsets[1] + syntaxData.getMultiLineCommentEnd().length() - 1;
			}
		}
		return index;
	}

	private boolean isDefinedVariable(String text) {
		return StringUtil.isNotEmpty(text);
	}

	private boolean atMultiLineCommentStart(String text, int position) {
		return text.indexOf(syntaxData.getMultiLineCommentStart(), position) == position;
	}

	private boolean atStringStart(String text, int position) {
		return text.indexOf(syntaxData.stringStart, position) == position;
	}

	private boolean atVarnameReference(String text, int position) {
		if (syntaxData.varnameReference == null) return false;
		return text.indexOf(syntaxData.varnameReference, position) == position;
	}

	private boolean atSingleLineComment(String text, int position) {
		if (syntaxData.getComment() == null) return false;		
		return text.indexOf(syntaxData.getComment(), position) == position;
	}

	/**
	 * Called by StyledText to get styles for a line
	 */
	public void lineGetStyle(LineStyleEvent event) {
		
		String lineText = event.lineText;
		int lineOffset = event.lineOffset;

		List<StyleRange> styles = new ArrayList<StyleRange>();

		int start = 0;
		int length = lineText.length();

		// Check if line begins inside a multiline comment
		int mlIndex = getBeginsInsideComment(lineOffset, lineText.length());
		if (mlIndex > -1) {
			// Line begins inside multiline comment; create the range
			styles.add(new StyleRange(lineOffset, mlIndex - lineOffset, COMMENT_COLOR, COMMENT_BACKGROUND));
			start = mlIndex;
		}
		// Do punctuation, single-line comments, and keywords
		while (start < length) {
			// Check for multiline comments that begin inside this line
			if (atMultiLineCommentStart(lineText, start)) {
				// Determine where comment ends
				int endComment = lineText.indexOf(syntaxData.getMultiLineCommentEnd(), start);

				// If comment doesn't end on this line, extend range to end of line
				if (endComment == -1)
					endComment = length;
				else
					endComment += syntaxData.getMultiLineCommentEnd().length();
				styles.add(new StyleRange(lineOffset + start, endComment - start, COMMENT_COLOR, COMMENT_BACKGROUND));

				start = endComment;
			}

			else if (atStringStart(lineText, start)) {
				// Determine where comment ends
				int endString = lineText.indexOf(syntaxData.stringEnd, start+1);

				// If string doesn't end on this line, extend range to end of line
				if (endString == -1)
					endString = length;
				else
					endString += syntaxData.stringEnd.length();
				styles.add(new StyleRange(lineOffset + start, endString - start, STRING_COLOR, COMMENT_BACKGROUND));

				start = endString;
			}

			else if (atSingleLineComment(lineText, start)) {   // Check for single line comments

				styles.add(new StyleRange(lineOffset + start, length - start, COMMENT_COLOR, COMMENT_BACKGROUND));
				start = length;
			}

			else if (atVarnameReference(lineText, start)) {		// Check for variable references

				StringBuilder buf = new StringBuilder();
				int i = start + syntaxData.getVarnameReference().length();
				// Call any consecutive letters a word
				for (; i < length && Character.isLetter(lineText.charAt(i)); i++) {
					buf.append(lineText.charAt(i));
				}

				// See if the word is a variable
				if (isDefinedVariable(buf.toString())) {
					// It's a keyword; create the StyleRange
					styles.add(new StyleRange(lineOffset + start, i - start, REFERENCED_VAR_COLOR, null, SWT.BOLD));
				}
				// Move the marker to the last char (the one that wasn't a letter)
				// so it can be retested in the next iteration through the loop
				start = i;
			}

			// Check for punctuation
			else if (syntaxData.isPunctuation(lineText.charAt(start))) {
				// Add range for punctuation
				styles.add(new StyleRange(lineOffset + start, 1, PUNCTUATION_COLOR, null));
				++start;
			} else if (Character.isLetter(lineText.charAt(start))) {

				int kwEnd = getKeywordEnd(lineText, start);  // See if the word is a keyword

				if (kwEnd > start) {		// Its a keyword; create the StyleRange					
					styles.add(new StyleRange(lineOffset + start, kwEnd - start, KEYWORD_COLOR, null));
				}

				// Move the marker to the last char (the one that wasn't a letter)
				// so it can be retested in the next iteration through the loop
				start = Math.abs(kwEnd);
			} else          
				++start;		// It's nothing we're interested in; advance the marker
		}

		// Copy the StyleRanges back into the event
		event.styles = styles.toArray(new StyleRange[styles.size()]);
	}

	private int getKeywordEnd(String lineText, int start) {

		int length = lineText.length();

		StringBuilder buf = new StringBuilder(length);
		int i = start;

		// Call any consecutive letters a word
		for (; i < length && Character.isLetter(lineText.charAt(i)); i++) {
			buf.append(lineText.charAt(i));
		}

		return syntaxData.isKeyword(buf.toString()) ? i : 0-i;
	}
}