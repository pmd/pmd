package net.sourceforge.pmd.eclipse.util;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.IndexedString;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleFieldAccessor;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class StyledTextBuilder extends AbstractCellPainterBuilder {

	private Display display = Display.getCurrent(); // NOPMD by br on 2/22/11 11:22 PM
	private TextLayout layout = new TextLayout(display);
	private final TextStyle style;

	public StyledTextBuilder(final FontBuilder theBuilder) {
		style = theBuilder.style(display);
	}

	private Font adjust(TreeItem tItem, RuleFieldAccessor getter, Tree tree) {
        
        Object value = valueFor(tItem, getter);
        
        IndexedString is = (IndexedString)value;
        String text = value == null ? "" : is.string;
        		        
        Rule rule = ruleFrom(tItem);
        if (rule != null) style.font = fontFor(tree, rule);
        layout.setText( text );
        
        if (StringUtil.isEmpty(text)) {
        	layout.setStyle(style, 0, 0);
        } else {
        	for (int i=0; i<is.indexSpans.size(); i++) {
        		layout.setStyle(style, is.indexSpans.get(i)[0], is.indexSpans.get(i)[1]-1);
        	}
        }
        
        return style.font;
	}
	
	  public void addPainterFor(final Tree tree, final int columnIndex, final RuleFieldAccessor getter, Map<Integer, List<Listener>> listenersByEventCode) {
          
      	Listener paintListener = new Listener() {
      		public void handleEvent(Event event) {
      			if (event.index != columnIndex) return; 

      			Font font = adjust((TreeItem)event.item, getter, tree);
      			event.gc.setFont(font);
      			layout.setFont(font);
      			int descent = event.gc.getFontMetrics().getDescent();
      			
      			layout.draw(event.gc, event.x, event.y+descent);
      			};
          	};
          
      	Listener measureListener = new Listener() {
      		public void handleEvent(Event event) {
      			if (event.index != columnIndex) return;
      			
      			adjust((TreeItem)event.item, getter, tree);
      				            			
      			final Rectangle textLayoutBounds = layout.getBounds();
      			event.width = textLayoutBounds.width + 2;
      			event.height = textLayoutBounds.height + 2;
      		}
      	};


          addListener(tree, SWT.PaintItem, paintListener, listenersByEventCode);
          addListener(tree, SWT.MeasureItem, measureListener, listenersByEventCode);
      };
}
