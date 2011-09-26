package net.sourceforge.pmd.eclipse.ui.views.ast;

import java.util.List;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.ast.JavadocElement;
import net.sourceforge.pmd.util.ClassUtil;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 
 * @author Brian Remedios
 */
public class ASTPainterHelper {

	private Font 		renderFont;
	private Font 		italicFont;
	private TextLayout 	textLayout;
	private TextStyle 	labelStyle;
	private TextStyle 	imageStyle;
	private TextStyle 	derivedStyle;
	
	public ASTPainterHelper(Display display) {
		
		textLayout = new TextLayout(display);    	
			
		// TODO take values from the font/color registries and then adapt to changes
		renderFont = new Font(display, "Tahoma", 10, SWT.NORMAL);
		italicFont = new Font(display, "Tahoma", 10, SWT.ITALIC);
		labelStyle = new TextStyle(renderFont, display.getSystemColor(SWT.COLOR_BLACK), null);
		imageStyle = new TextStyle(renderFont, display.getSystemColor(SWT.COLOR_BLUE), null);
		derivedStyle = new TextStyle(italicFont, display.getSystemColor(SWT.COLOR_GRAY), null);
	}
	
	private String lineTextFor(Comment comment) {
		
		StringBuilder sb = new StringBuilder();
		
		if (comment.isSingleLine()) {
			sb.append( comment.getBeginLine() );
			} else {
				sb.append( comment.getBeginLine() ).append('-').append(comment.getEndLine());
				}
		
		sb.append(' ');
		
		List<String> lines = CommentUtil.multiLinesIn(comment.getImage());
		String first = lines.get(0);
		if (StringUtil.isNotEmpty(first)) sb.append(first);
		
		if (lines.size() == 1) {
			return sb.toString();
		} else {
			for (String line : lines) {
				if (StringUtil.isEmpty(line)) continue;
				sb.append('|').append(line);
				}
		}
		
		return sb.toString();
	}
	
	private TextLayout layoutFor(Comment comment) {
		String label = ClassUtil.withoutPackageName(comment.getClass().getSimpleName());
		int labelLength = label.length();
		
		String lineText = lineTextFor(comment);
		textLayout.setText(label + " " + lineText);
		textLayout.setStyle(derivedStyle, labelLength, labelLength + lineText.length());
		return textLayout;
	}
	
	private TextLayout layoutFor(JavadocElement javadoc) {
		String label = "@" + javadoc.tag().label;
	//	int labelLength = label.length();
		
		textLayout.setText(label);
	//	textLayout.setStyle(derivedStyle, labelLength, labelLength + label.length());
		return textLayout;
	}
	
	private String textFor(AbstractNode node) {
		String txt = node.getImage();
		if (StringUtil.isNotEmpty(txt)) return txt;
		
		// booleans don't have image values..convert them
		if (node instanceof ASTBooleanLiteral) {
			return Boolean.toString( ((ASTBooleanLiteral)node).isTrue());
		}
		
		return null;
	}
	
	public TextLayout layoutFor(TreeItem item) {

		Object data = item.getData();
		if (data instanceof Comment) {
			return layoutFor((Comment)data);
		}
		
		if (data instanceof JavadocElement) {
			return layoutFor((JavadocElement)data);
		}
		
		AbstractNode node = (AbstractNode)data;
		String label = node.toString();

		TextStyle extraStyle = imageStyle;
		String extra = NodeImageDeriver.derivedTextFor(node);
		if (extra != null) {
			extraStyle = derivedStyle;
			} else {
				extra = textFor(node);
				}

		textLayout.setText(label + (extra == null  ? "" : " " + extra));

		int labelLength = label.length();

		textLayout.setStyle(labelStyle, 0, labelLength);
		if (extra != null) {
			textLayout.setStyle(extraStyle, labelLength, labelLength + extra.length() + 1);
		}

		return textLayout;
	}
	
	public void dispose() {
		renderFont.dispose();
		italicFont.dispose();
	}
}
