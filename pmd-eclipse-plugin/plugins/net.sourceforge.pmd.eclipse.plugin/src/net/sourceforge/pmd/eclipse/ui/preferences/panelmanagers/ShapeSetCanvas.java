package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import java.util.Map;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.util.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Renders a set of coloured shapes mapped to known set of incoming items
 * 
 * @author Brian Remedios
 */
public class ShapeSetCanvas extends Canvas {

	private Object[] items;
	private int itemWidth;
	private Map<Object, ShapeDescriptor> shapeDescriptorsByItem;
		
	public ShapeSetCanvas(Composite parent, int style, int theItemWidth) {
		super(parent, style);
		
		itemWidth = theItemWidth;
		
		ShapeSetCanvas.this.addPaintListener( new PaintListener() {
			public void paintControl(PaintEvent pe) {
				doPaint(pe);
			}
		} );
	}
	
	private Color colourFor(int itemIndex) {
		ShapeDescriptor desc = shapeDescriptorsByItem.get(items[itemIndex]);
		if (desc == null) return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK); 
		return PMDPlugin.getDefault().colorFor(desc.rgbColor);
	}
	
	private Util.shape shapeFor(int itemIndex) {
		ShapeDescriptor desc = shapeDescriptorsByItem.get(items[itemIndex]);
		return desc == null ? Util.shape.circle : desc.shape;
	}
	
	private void doPaint(PaintEvent pe) {
		
		GC gc = pe.gc;
		int width = getSize().x;
		int xBoundary = 3;
		int gap = 2;
		
        for (int i=0; i<items.length; i++) {
        	gc.setBackground(colourFor(i));
                                	                                     
            int xOffset = 0;
            int step = (itemWidth + gap) * i;
            
            switch (SWT.LEFT) {	// TODO take from style bits
                case SWT.CENTER: xOffset = (width / 2) - (itemWidth / 2) - xBoundary + step; break;
                case SWT.RIGHT: xOffset = width - width - xBoundary; break;
                case SWT.LEFT: xOffset = xBoundary + step;
            }
            
            Util.drawShape(itemWidth, itemWidth, shapeFor(i), gc, pe.x + xOffset, pe.y);
        }
	}
	
	public Point computeSize(int wHint, int hHint, boolean changed) {

		Point pt = getSize();
		// TODO adapt by shape count
		return new Point(pt.x, pt.y);
	}
	
	public void setItems(Object[] theItems) {
		items = theItems;
		redraw();
	}
	
	public void setItemWidth(int width) {
		itemWidth = width;
		redraw();
	}
	
	public void setShapeMap(Map<Object, ShapeDescriptor> theShapeMap) {
		
		shapeDescriptorsByItem = theShapeMap;		
		redraw();
	}

}
