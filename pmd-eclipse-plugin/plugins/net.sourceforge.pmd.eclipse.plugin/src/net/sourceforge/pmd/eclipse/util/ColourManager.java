package net.sourceforge.pmd.eclipse.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Brian Remedios
 */
public class ColourManager {

	private final Display display;
	
	private final Map<int[], Color> coloursByRGB = new HashMap<int[], Color>();
	
	public ColourManager(Display theDisplay) {
		display = theDisplay;
	}

	public Color colourFor(String text) {
		
		if (StringUtil.isEmpty(text)) return display.getSystemColor(SWT.COLOR_WHITE);
		
		text = text.trim();
		int length = text.length();
		
		if (length < 3) return  display.getSystemColor(SWT.COLOR_WHITE);
		
		int posA = length / 3;
		int posB = posA * 2;
				
		int rHash = text.subSequence(0, posA).hashCode();
		int gHash = text.subSequence(posA, posB).hashCode();
		int bHash = text.subSequence(posB, length).hashCode();
		
		int colourFractions[] = new int[] {
			(int)(Math.log10(rHash) % 1 * 255),
			(int)(Math.log10(gHash) % 1 * 255),
			(int)(Math.log10(bHash) % 1 * 255)
			};
		
		Color colour = coloursByRGB.get(colourFractions);
		if (colour != null) return colour;
		
		colour = new Color(display, colourFractions[0], colourFractions[1],	colourFractions[2]);
		coloursByRGB.put(colourFractions, colour);
		return colour;
	}
	
	public void dispose() {
		
		Iterator<Color> iter = coloursByRGB.values().iterator();
		while (iter.hasNext()) iter.next().dispose();
	}
}
