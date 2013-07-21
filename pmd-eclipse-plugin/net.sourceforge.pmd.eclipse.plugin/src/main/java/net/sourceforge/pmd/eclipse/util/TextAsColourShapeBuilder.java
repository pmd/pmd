package net.sourceforge.pmd.eclipse.util;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import net.sourceforge.pmd.eclipse.ui.Shape;
import net.sourceforge.pmd.eclipse.ui.ShapePainter;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleFieldAccessor;
import net.sourceforge.pmd.util.StringUtil;

public class TextAsColourShapeBuilder extends AbstractCellPainterBuilder {

	private final int width;
	private final int height;
	private final Shape shapeId;
	
	public TextAsColourShapeBuilder(int theWidth, int theHeight, Shape theShapeId) {
		width = theWidth;
		height = theHeight;
		shapeId = theShapeId;
	}

	 private String getterTextIn(TreeItem tItem, RuleFieldAccessor getter) {
         
         String text = (String) valueFor(tItem, getter);
         
         return StringUtil.isEmpty(text) ? null : text;
     }
     	        
     public void addPainterFor(final Tree tree, final int columnIndex, final RuleFieldAccessor getter, Map<Integer, List<Listener>> listenersByEventCode) {
         
         Listener paintListener = new Listener() {
             public void handleEvent(Event event) {
                 if (event.index != columnIndex) return;
                 
                 String text = getterTextIn((TreeItem)event.item, getter);
                 if (text == null) return;
                 
                 Color original = event.gc.getBackground();
                 
                 Color clr = colorManager().colourFor(text);
                 event.gc.setBackground(clr);
                 
                 ShapePainter.drawShape(width, height, shapeId, event.gc, event.x, event.y, null);
                 
                 event.gc.setBackground(original);
             }
         };
                            
         Listener measureListener = new Listener() {
             public void handleEvent(Event e) {
                 if (e.index != columnIndex) return;
                 e.width = width + 2;
                 e.height = height + 2;
             }
         };
         
         addListener(tree, SWT.PaintItem, paintListener, listenersByEventCode);
         addListener(tree, SWT.MeasureItem, measureListener, listenersByEventCode);
     }            

}
