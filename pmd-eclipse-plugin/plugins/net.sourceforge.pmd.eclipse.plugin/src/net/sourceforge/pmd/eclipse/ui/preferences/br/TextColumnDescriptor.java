package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.util.AbstractCellPainterBuilder;
import net.sourceforge.pmd.eclipse.util.ResourceManager;
import net.sourceforge.pmd.eclipse.util.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 *
 * @author Brian Remedios
 */
public class TextColumnDescriptor extends SimpleColumnDescriptor {

	public static final RuleFieldAccessor ruleSetNameAcc = new BasicRuleFieldAccessor() {
           public Comparable<?> valueFor(Rule rule) {
               return RuleUIUtil.ruleSetNameFrom(rule);
           }
       };

    public static final RuleFieldAccessor propertiesAcc = new BasicRuleFieldAccessor() {
            public Comparable<?> valueFor(Rule rule) {
               return RuleUIUtil.propertyStringFrom(rule, "*");
            }
      };

      
    private static final int ImgOffset = 14;
	
    /**
	 * @param theId String
	 * @param theLabel String
	 * @param theAlignment int
	 * @param theWidth int
	 * @param theAccessor RuleFieldAccessor
	 * @param resizableFlag boolean
	 * @param theImagePath String
	 */
	public TextColumnDescriptor(String theId, String theLabel, int theAlignment, int theWidth, RuleFieldAccessor theAccessor, boolean resizableFlag, String theImagePath) {
		super(theId, theLabel, theAlignment,theWidth,theAccessor,resizableFlag, theImagePath);
	}

	private static boolean isCheckboxTree(Tree tree) {
		return (tree.getStyle() | SWT.CHECK) > 0;
	}
	
	/* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.ui.preferences.br.IRuleColumnDescriptor#newTreeColumnFor(org.eclipse.swt.widgets.Tree, int, net.sourceforge.pmd.eclipse.ui.preferences.br.RuleSortListener, java.util.Map)
     */
	public TreeColumn newTreeColumnFor(Tree parent, int columnIndex, SortListener sortListener, Map<Integer, List<Listener>> paintListeners) {
		
		TreeColumn tc = super.newTreeColumnFor(parent, columnIndex, sortListener, paintListeners);
        
        if (isCheckboxTree(parent) && columnIndex != 0) {	// can't owner-draw the check or expansion toggles
        	addPainterFor(tc.getParent(), columnIndex, accessor(), paintListeners);
        }
        
        return tc;
	}
	
	public String stringValueFor(Rule rule) {
		return "";	// we draw it ourselves
	}

	public String stringValueFor(RuleCollection collection) {
		return "";	// we draw it ourselves
	}
	
    public Image imageFor(Rule rule) {

        boolean hasIssues = rule.dysfunctionReason() != null;

        return hasIssues ? ResourceManager.imageFor(PMDUiConstants.ICON_WARN) : null;
    }

	public void addPainterFor(final Tree tree, final int columnIndex, final RuleFieldAccessor getter, Map<Integer, List<Listener>> thePaintListeners) {
            
		CellPainterBuilder cpl = new AbstractCellPainterBuilder() {
			
			public void addPainterFor(final Tree tree, final int columnIndex, final RuleFieldAccessor getter, Map<Integer, List<Listener>> paintListeners) {
					
	            Listener paintListener = new Listener() {
	                public void handleEvent(Event event) {
	                    
	                    if (event.index != columnIndex) return;

                        Object value = ((TreeItem)event.item).getData();
                        if (value instanceof RuleCollection) return;
                        
                        GC gc = event.gc;
	                        
                        int imgOffset = 0;
                        
                        Rule rule = (Rule)value;
                        gc.setFont( fontFor(tree, rule) );	
                        imgOffset = rule.dysfunctionReason() != null ? ImgOffset : 0;

                        String text = textFor((TreeItem)event.item, getter);
	                    int descent = gc.getFontMetrics().getDescent();
	                    
                        gc.drawString(text, event.x+imgOffset, event.y+descent, true);                 
	                    }	                
	            };
	            
            	Listener measureListener = new Listener() {
            		public void handleEvent(Event event) {
            			if (event.index != columnIndex) return;

            			String text = textFor((TreeItem)event.item, getter);
            			
	                    Point size = event.gc.textExtent(text);
	                    event.width = size.x + 2 * (3);
	           //         event.height = Math.max(event.height, size.y + (3));
            		}
            	};
	            
                Util.addListener(tree, SWT.PaintItem, paintListener, paintListeners);
                Util.addListener(tree, SWT.MeasureItem, measureListener, paintListeners);
			}
        };
		
		cpl.addPainterFor(tree, columnIndex, getter, thePaintListeners);
	}
}
