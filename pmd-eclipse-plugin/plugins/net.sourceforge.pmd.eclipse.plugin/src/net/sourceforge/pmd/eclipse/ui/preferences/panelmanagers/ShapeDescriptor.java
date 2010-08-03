package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import net.sourceforge.pmd.eclipse.util.Util;

import org.eclipse.swt.graphics.RGB;

/**
 * 
 * @author Brian Remedios
 */
public class ShapeDescriptor {

	public final Util.shape shape;
	public final RGB rgbColor;
	public final int size;
	
	public ShapeDescriptor(Util.shape theShape, RGB theColor, int theSize) {
		shape = theShape;
		rgbColor = theColor;
		size = theSize;
	}
}
