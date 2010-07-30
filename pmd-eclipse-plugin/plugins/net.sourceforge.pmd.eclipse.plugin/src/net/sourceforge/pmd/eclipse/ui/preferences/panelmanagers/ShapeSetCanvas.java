package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.eclipse.util.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * Renders a set of coloured shapes mapped to incoming items
 * 
 * @author Brian Remedios
 */
public class ShapeSetCanvas extends Canvas {

	private Util.shape shapeId;
	private Object[] items;
	private int itemWidth;
	private Map<Object, Color> coloursByItem;
	
	public ShapeSetCanvas(Composite parent, int style, Util.shape theShape, int theItemWidth) {
		super(parent, style);
		
		shapeId = theShape;
		itemWidth = theItemWidth;
		
		ShapeSetCanvas.this.addPaintListener( new PaintListener() {
			public void paintControl(PaintEvent pe) {
				doPaint(pe);
			}
		} );
		ShapeSetCanvas.this.addDisposeListener( new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				disposeAll(coloursByItem.values());
			}});
	}
	
	public static void disposeAll(Collection<Color> colors) {
		for (Color color : colors) color.dispose();
	}
	
	private void doPaint(PaintEvent pe) {
		
		GC gc = pe.gc;
		int width = getSize().x;
		int xBoundary = 3;
		
        for (int i=0; i<items.length; i++) {
        	gc.setBackground(coloursByItem.get(items[i]));
                                	                                     
            int xOffset = 0;
            int step = itemWidth * i;
            
            switch (SWT.LEFT) {	// TODO take from style bits
                case SWT.CENTER: xOffset = (width / 2) - (itemWidth / 2) - xBoundary + step; break;
                case SWT.RIGHT: xOffset = width - width - xBoundary; break;
                case SWT.LEFT: xOffset = xBoundary + step;
            }
            
            Util.drawShape(itemWidth, itemWidth, shapeId, gc, pe.x + xOffset, pe.y);
            
            xOffset += itemWidth;
        }
	}
	
	public Point computeSize(int wHint, int hHint, boolean changed) {

		Point pt = getSize();
		// TODO adapt by shape count
		return new Point(pt.x, pt.y);
	}

	public void setShape(Util.shape theShape) {
		shapeId = theShape;
		redraw();
	}
	
	public void setItems(Object[] theItems) {
		items = theItems;
		redraw();
	}
	
	public void setItemWidth(int width) {
		itemWidth = width;
		redraw();
	}
	
	public void setColourMap(Map<Object, RGB> colourMap) {
		
		coloursByItem = new HashMap<Object, Color>(colourMap.size());
		
		for (Map.Entry<Object, RGB> entry : colourMap.entrySet()) {
			RGB rgb = entry.getValue();
			coloursByItem.put(
				entry.getKey(), 
				new Color(null, rgb.red, rgb.green, rgb.blue)
				);
		}
		
		redraw();
	}

}
