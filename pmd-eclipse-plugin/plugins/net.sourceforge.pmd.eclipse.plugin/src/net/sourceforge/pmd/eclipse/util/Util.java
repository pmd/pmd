package net.sourceforge.pmd.eclipse.util;

import java.util.Comparator;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.preferences.br.CellPainterBuilder;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleFieldAccessor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class Util {

    public static final Object[] EMPTY_ARRAY = new Object[0];
    
    private Util() {};
        
    public static boolean isEmpty(Object[] items) {        
        return items == null || items.length == 0;
    }
    
    public static boolean areSemanticEquals(Object[] a, Object[] b) {
        
        if (a == null) return isEmpty(b);
        if (b == null) return isEmpty(a);       
        return a.equals(b);
    }
    
    /**
     * Helper method to get a filename without its extension
     * @param fileName String
     * @return String
     */
    public static String getFileNameWithoutExtension(String fileName) {
        String name = fileName;

        int index = fileName.lastIndexOf('.');
        if (index != -1) {
            name = fileName.substring(0, index);
        }

        return name;
    }
    
	public static Comparator comparatorFrom(final RuleFieldAccessor accessor, final boolean inverted) {
		
		return new Comparator() {

			public int compare(Object a, Object b) {
				Comparable ca = accessor.valueFor((Rule) a);
				Comparable cb = accessor.valueFor((Rule) b);
				
				int result = (ca == null) ? 
						-1 : (cb == null) ? 
							1 : ca.compareTo(cb);
				
				return inverted ? result * -1 : result;
			}			
		};
	}
	
	/**
	 * Method asCleanString.
	 * @param original String
	 * @return String
	 */
	public static String asCleanString(String original) {
		return original == null ? "" : original.trim();
	}
	
	public static CellPainterBuilder backgroundBuilderFor(final int systemColourIndex) {
	    
	    return new CellPainterBuilder() {

	        public void addPainterFor(final Tree tree, final int columnIndex, final RuleFieldAccessor getter) {
	            
	            final Display display = tree.getDisplay();
	            
	            tree.addListener(SWT.EraseItem, new Listener() {
	                public void handleEvent(Event event) {
	                    
	                    if (event.index != columnIndex) return;
	                    event.detail &= ~SWT.HOT;
	                    
	                    if ((event.detail & SWT.SELECTED) != 0) {
	                        GC gc = event.gc;
	                        Rectangle area = tree.getClientArea();
	                        /*
	                         * If you wish to paint the selection beyond the end of last column, you must change the clipping region.
	                         */
	                        int columnCount = tree.getColumnCount();
	                        if (event.index == columnCount - 1 || columnCount == 0) {
	                            int width = area.x + area.width - event.x;
	                            if (width > 0) {
	                                Region region = new Region();
	                                gc.getClipping(region);
	                                region.add(event.x, event.y, width, event.height); 
	                                gc.setClipping(region);
	                                region.dispose();
	                            }
	                        }
	                        gc.setAdvanced(true);
	                        if (gc.getAdvanced()) gc.setAlpha(127);                             
	                        Rectangle rect = event.getBounds();
	                        Color foreground = gc.getForeground();
	                        Color background = gc.getBackground();
	                        gc.setForeground(display.getSystemColor(systemColourIndex));
	                        gc.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
	                        gc.fillGradientRectangle(event.x, rect.y, 500, rect.height, false);
	                        
	                        gc.setForeground(display.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
	                        gc.drawLine(event.x, rect.y, event.x + 20, rect.y + 20);
	                        
	                        gc.setForeground(foreground);  // restore colors for subsequent drawing
	                        gc.setBackground(background);
	                        event.detail &= ~SWT.SELECTED;                  
	                    }
	                }       
	            });
	        }};
	}
	
	   public static CellPainterBuilder textBuilderFor(final int systemColourIndex) {
	        
	        return new CellPainterBuilder() {

	            public void addPainterFor(final Tree tree, final int columnIndex, final RuleFieldAccessor getter) {
	                
	                final Display display = tree.getDisplay();

//	                   tree.addListener(SWT.EraseItem, new Listener() {
//	                        public void handleEvent(Event event) {
//	                            if (event.index != columnIndex) return;
//	                            
//	                //            GC gc = event.gc;
//                   //             Rectangle area = new Rectangle(event.x, event.y, event.width, event.height);
//                               
//       //                         gc.fillRectangle(area);
//	                        }
//	         
//	                        });
	                
	                tree.addListener(SWT.PaintItem, new Listener() {
	                    public void handleEvent(Event event) {
	                        
	                        if (event.index != columnIndex) return;
	                        event.detail &= ~SWT.HOT;
	                        
//	                        if ((event.detail & SWT.SELECTED) != 0) {
	                            GC gc = event.gc;
	                            Rectangle area = tree.getClientArea();
	                            
	                            Rule rule = (Rule)((TreeItem)event.item).getData();
	                            String text = getter.valueFor(rule).toString();
	                            
	                            int columnCount = tree.getColumnCount();
	                            if (event.index == columnCount - 1 || columnCount == 0) {
	                                int width = area.x + area.width - event.x;
	                                if (width > 0) {
	                                    Region region = new Region();
	                                    gc.getClipping(region);
	                                    region.add(event.x, event.y, width, event.height); 
	                                    gc.setClipping(region);
	                                    region.dispose();
	                                }
	                            }
	                        //    gc.setAdvanced(true);
	                        //    if (gc.getAdvanced()) gc.setAlpha(127);                             
	                            Rectangle rect = event.getBounds();
	                     //       Color foreground = gc.getForeground();
	                     //       Color background = gc.getBackground();
	                     //       gc.setForeground(display.getSystemColor(systemColourIndex));
	                     //       gc.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
	                            
	                     //       gc.setForeground(display.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
	                            gc.drawString(text, event.x, rect.y);
	                            
	                     //       gc.setForeground(foreground);  // restore colors for subsequent drawing
	                     //       gc.setBackground(background);
//	                            event.detail &= ~SWT.SELECTED;                  
//	                        }
	                    }       
	                });
	            }};
	    }
	
	
	// TODO move these to StringUtil
    public static String asString(List<String> items, String separator) {
        
        if (items == null || items.isEmpty()) return "";
        if (items.size() == 1) return items.get(0);
        
        StringBuilder sb = new StringBuilder(items.get(0));
        for (int i=1; i<items.size(); i++) {
            sb.append(separator).append(items.get(i));
        }
        return sb.toString();
    }
    
    public static void asString(Object[] items, String separator, StringBuilder target) {
        
        if (items == null || items.length==0) return;
        if (items.length == 1) target.append(items[0]);
        
        target.append(items[0]);
        for (int i=1; i<items.length; i++) {
            target.append(separator).append(items[i]);
        }
    }
}
