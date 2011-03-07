package net.sourceforge.pmd.eclipse.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.eclipse.ui.ShapeDescriptor;
import net.sourceforge.pmd.eclipse.ui.ShapePainter;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleFieldAccessor;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class UniqueItemsAsShapeBuilder extends AbstractCellPainterBuilder {

	private final int width;
	private final int height;
	private final int hAlignment;
	private final Map<Object, ShapeDescriptor> shapesByItem;
	
	public UniqueItemsAsShapeBuilder(int theWidth, int theHeight, int horizAlignment, Map<Object, ShapeDescriptor> theShapesByItem) {
		width = theWidth;
		height = theHeight;
		hAlignment = horizAlignment;
		shapesByItem = theShapesByItem;
	}
    
    private List<ShapeDescriptor> getterShapesIn(TreeItem tItem, RuleFieldAccessor getter) {

        Set<Comparable<?>> values = RuleUtil.uniqueItemsIn(tItem.getData(), getter);
        
        List<ShapeDescriptor> shapes = new ArrayList<ShapeDescriptor>(values.size());
        Iterator<?> iter = values.iterator();
        while (iter.hasNext()) {
        	ShapeDescriptor desc = shapesByItem.get(iter.next());
        	if (desc != null) {
        		shapes.add( desc );
        	}
        	
        }
        return shapes;
    }
    
    public void addPainterFor(final Tree tree, final int columnIndex, final RuleFieldAccessor getter, Map<Integer, List<Listener>> listenersByEventCode) {
        
    	final int xBoundary = 3;
    	
        Listener paintListener = new Listener() {
        	final int gap = 2;
            public void handleEvent(Event event) {
                if (event.index != columnIndex) return;
            
                List<ShapeDescriptor> shapes = getterShapesIn((TreeItem)event.item, getter);
                if (shapes.isEmpty()) return;
                
                GC gc = event.gc;
                int verticalOffset = (event.height / 2) - (height / 2);
                
                Color original = gc.getBackground();

            	final int cellWidth = widthOf(columnIndex, tree);
            	
                for (int i=0; i<shapes.size(); i++) {
                	ShapeDescriptor shape = shapes.get(i);
                	Color clr = colorManager().colourFor(shape.rgbColor);
                	gc.setBackground(clr);
                                        	                                     
                    int xOffset = 0;
                    int step = (width+gap) * i;
                    
                    switch (hAlignment) {
                        case SWT.CENTER: xOffset = (cellWidth / 2) - (width / 2) - xBoundary + step; 		break;
                        case SWT.RIGHT: xOffset = cellWidth - width - xBoundary;	break;
                        case SWT.LEFT: xOffset = xBoundary + step;
                    }
                    
                    ShapePainter.drawShape(width, height, shape.shape, gc, event.x + xOffset, event.y + verticalOffset, null);		                    
                }
                
                gc.setBackground(original);
            }

        };
                           
        Listener measureListener = new Listener() {
            public void handleEvent(Event event) {
                if (event.index != columnIndex) return;
                
                Object item = ((TreeItem)event.item).getData();
                Set<Comparable<?>> items = RuleUtil.uniqueItemsIn(item, getter);
                
                event.width = width + (items.size() * width);
                event.height = height ;
            }
        };
        
        addListener(tree, SWT.PaintItem, paintListener, listenersByEventCode);
        addListener(tree, SWT.MeasureItem, measureListener, listenersByEventCode);
    }

}
