package net.sourceforge.pmd.eclipse.util;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Brian Remedios
 */
public class FontBuilder {
	   
	   public final String name;
	   public final int size;
	   public final int style;
	   public final int colorIdx;
	   
	   public FontBuilder(String theName, int theSize, int theStyle, int theColorIndex) {
		   name = theName;
		   size = theSize;
		   style = theStyle;
		   colorIdx = theColorIndex;
	   }
	   
	   public FontBuilder(String theName, int theSize, int theStyle) {
		   this(theName, theSize, theStyle, -1);
	   }
	   
	   public Font build(Display display) {
		   return new Font(display, name, size, style);
	   }
	   
	   public TextStyle style(Display display) {			   
		   return new TextStyle(build(display), 
				   colorIdx < 0 ? null : display.getSystemColor(colorIdx), 
				   null);
	   }
}