package net.sourceforge.pmd.eclipse.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.IndexedString;
import net.sourceforge.pmd.eclipse.ui.Shape;
import net.sourceforge.pmd.eclipse.ui.ShapeDescriptor;
import net.sourceforge.pmd.eclipse.ui.ShapePainter;
import net.sourceforge.pmd.eclipse.ui.preferences.br.CellPainterBuilder;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleCollection;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleFieldAccessor;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleUtil;
import net.sourceforge.pmd.util.ClassUtil;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class Util {

    public static final Object[] EMPTY_ARRAY = new Object[0];
    
    public static final Comparator<Method> MethodNameComparator = new Comparator<Method>() {
        public int compare(Method a, Method b) {            
            return a.getName().compareTo(b.getName());
        }
    };
    
    private Util() {};
    
    /**
     * Scans the source for occurrences of the prefix char and assumes that all letters
     * that follow immediately after make up a reference name. Returns the position and
     * lengths of all names found.
     * 
     * NOTE: Doesn't handle the prefix within escaped or quoted strings
     * 
     * @param source
     * @param prefix
     * @return List<int[]>
     */
    public static List<int[]> referencedNamePositionsIn(String source, char prefix) {
        
        List<int[]> namePositions = new ArrayList<int[]>();
        if (StringUtil.isEmpty(source)) return namePositions;
        
        int pos = source.indexOf(prefix);
        int max = source.length();
        
        while (pos >= 0) {            
            int end = pos+1;
            while (end < max && Character.isLetter(source.charAt(end)) ) end++;
            int length = end - pos -1;
            if (length < 1) {
            	pos = source.indexOf(prefix, pos + 1);
            	continue;
            }
            
            namePositions.add( new int[] {pos+1, length} );
            pos = source.indexOf(prefix, pos + length);
        }
        
        return namePositions;
    }
    
    /**
     * Extract and return all the string fragments listed in the list of positions
     * where each position = int [start][length]
     * 
     * @param source
     * @param positions
     * @return List<String>
     */
    public static List<String> fragmentsWithin(String source, List<int[]> positions) {
        
        List<String> fragments = new ArrayList<String>(positions.size());
        for (int[] position : positions) {
            fragments.add(
                    source.substring(position[0], position[0] + position[1])
                    );
        }
        return fragments;
    }
    
    public static String signatureFor(Method method, String[] unwantedPrefixes) {

        StringBuilder sb = new StringBuilder();
        Class<?> returnType = method.getReturnType();
        if (returnType.getName().equals("void")) {  // TODO is there a better way?
            sb.append("void ");
        } else {
            signatureFor(returnType, unwantedPrefixes, sb);
            sb.append(' ');
        }
        Class<?>[] types = method.getParameterTypes();
        if (types.length == 0) {
            sb.append(method.getName());
            sb.append("()");
            return sb.toString();
        }
        
        sb.append(method.getName()).append('(');
        signatureFor(types[0], unwantedPrefixes, sb);
        for (int i=1; i<types.length; i++) {
            sb.append(',');
            signatureFor(types[i], unwantedPrefixes, sb);
        }
        sb.append(')');
        return sb.toString();
    }
  
    private static String filteredPrefixFrom(String paramType, String[] unwantedPrefixes) {
        for (String prefix : unwantedPrefixes) {
            if (paramType.startsWith(prefix)) {
                return paramType.substring(prefix.length());
            }
        }
        return paramType;
    }
    
    public static String signatureFor(Class<?> type, String[] unwantedPrefixes) {
        
        StringBuilder sb = new StringBuilder();
        signatureFor(type, unwantedPrefixes, sb);
        return sb.toString();
    }
    
    private static void signatureFor(Class<?> type, String[] unwantedPrefixes, StringBuilder sb) {        
        
        String typeName = ClassUtil.asShortestName(
                type.isArray() ? type.getComponentType() : type
                );
        typeName = filteredPrefixFrom(typeName, unwantedPrefixes);
        
        sb.append(typeName);
        if (type.isArray()) sb.append("[]");
    }
    
	public static Comparator<?> comparatorFrom(final RuleFieldAccessor accessor, final boolean inverted) {
		
		if (accessor == null) {
			throw new IllegalArgumentException("Accessor is required");
		}
		
		return new Comparator<Rule>() {

			public int compare(Rule a, Rule b) {
				Comparable ca = accessor.valueFor(a);
				Comparable cb = accessor.valueFor(b);
				
				int result = (ca == null) ? 
						-1 : (cb == null) ? 
							1 : ca.compareTo(cb);
				
				return inverted ? result * -1 : result;
			}			
		};
	}
	
    public static void removeListeners(Control widget, int listenerType) {
        for (Listener listener : widget.getListeners(listenerType)) {
            widget.removeListener(listenerType, listener);
        }
    }	
    
    public static int indexOf(Object[] items, Object choice) {
        for (int i=0; i<items.length; i++) {
            if (items[i].equals(choice)) return i;
        }
        return -1;
    }
    
	/**
	 * Method asCleanString.
	 * @param original String
	 * @return String
	 */
	public static String asCleanString(String original) {
		return original == null ? "" : original.trim();
	}
	
	
	public static CellPainterBuilder textAsColorShapeFor(final int width, final int height, final Shape shapeId) {
	    
	    return new CellPainterBuilder() {

	        private ColourManager colorManager;
	        
	        private ColourManager colorManager() {
	            if (colorManager != null) return colorManager;
	            colorManager = ColourManager.managerFor(Display.getCurrent());
	            return colorManager;
	        }
	        
	        private String getterTextIn(TreeItem tItem, RuleFieldAccessor getter) {
	            
	            Object item = tItem.getData();
	            
	            String text = null;
	            if (item instanceof Rule) {
	            	text = (String)getter.valueFor((Rule) item);
	            	}
	            if (item instanceof RuleCollection) {
	            	text = (String)getter.valueFor((RuleCollection) item);
	            	}
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
	    };
	}
	
	public static CellPainterBuilder itemAsShapeFor(final int width, final int height, final Shape shapeId, final int horizAlignment, final Map<Object, RGB> coloursByItem) {
	    
	    return new CellPainterBuilder() {

	        private ColourManager colorManager;
	        
	        private ColourManager colorManager() {
	            if (colorManager != null) return colorManager;
	            colorManager = ColourManager.managerFor(Display.getCurrent());
	            return colorManager;
	        }
	        
	        public void dispose() {
	        	colorManager().dispose();
	        }
	        
	        private Color getterColorIn(TreeItem tItem, RuleFieldAccessor getter) {
	            
	            Object item = tItem.getData();
	            Object value = null;
	            
	            if (item instanceof Rule) {
	            	value = getter.valueFor((Rule) item);
	            }
	            
	            if (item instanceof RuleCollection) {
	            	value = getter.valueFor((RuleCollection)item);
	            }
	            
	            RGB color = coloursByItem.get(value);
                return color == null ? null : colorManager().colourFor(color);
	        }
	        	        
	        private int widthOf(int columnIndex, Tree tree) {
	        	return tree.getColumn(columnIndex).getWidth();
	        }
	        
            public void addPainterFor(final Tree tree, final int columnIndex, final RuleFieldAccessor getter, Map<Integer, List<Listener>> listenersByEventCode) {
                
            	final int xBoundary = 3;
            	
                Listener paintListener = new Listener() {
                    public void handleEvent(Event event) {
                        if (event.index != columnIndex) return;
                    
                        Color clr = getterColorIn((TreeItem)event.item, getter);
                        if (clr == null) return;
                        
                        Color original = event.gc.getBackground();
                        
                        event.gc.setBackground(clr);
                        
                        int xOffset = 0;
                        final int cellWidth = widthOf(columnIndex, tree);
                                                
                        switch (horizAlignment) {
	                        case SWT.CENTER: xOffset = (cellWidth / 2) - (width / 2) - xBoundary; 		break;
	                        case SWT.RIGHT: xOffset = cellWidth - width - xBoundary;	break;
	                        case SWT.LEFT: xOffset = 0;
                        }
                        
                        ShapePainter.drawShape(width, height, shapeId, event.gc, event.x + xOffset, event.y, null);
                        
                        event.gc.setBackground(original);
                    }

                };
                                   
                Listener measureListener = new Listener() {
                    public void handleEvent(Event e) {
                        if (e.index != columnIndex) return;
                        e.width = width ;
                        e.height = height ;
                    }
                };
                
                addListener(tree, SWT.PaintItem, paintListener, listenersByEventCode);
                addListener(tree, SWT.MeasureItem, measureListener, listenersByEventCode);
            }            
	    };
	}
	
	public static CellPainterBuilder uniqueItemsAsShapeFor(final int width, final int height, final int horizAlignment, final Map<Object, ShapeDescriptor> shapesByItem) {
	    
	    return new CellPainterBuilder() {

	        private ColourManager colorManager;
	        
	        private ColourManager colorManager() {
	            if (colorManager != null) return colorManager;
	            colorManager = ColourManager.managerFor(Display.getCurrent());
	            return colorManager;
	        }
	        
	        public void dispose() {
	        	colorManager().dispose();
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

	        private int widthOf(int columnIndex, Tree tree) {
	        	return tree.getColumn(columnIndex).getWidth();
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
                            
		                    switch (horizAlignment) {
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
	    };
	}
	
    private static void addListener(Control control, int eventType, Listener listener, Map<Integer, List<Listener>> listenersByEventCode) {
        
        Integer eventCode = Integer.valueOf(eventType);
        
        control.addListener(eventType, listener);
        if (!listenersByEventCode.containsKey(eventCode)) {
            listenersByEventCode.put(eventCode, new ArrayList<Listener>());
            }
        
        listenersByEventCode.get(eventCode).add(listener);
    }
    
	
    
    
	public static CellPainterBuilder backgroundBuilderFor(final int systemColourIndex) {
	    
	    return new CellPainterBuilder() {

	        public void addPainterFor(final Tree tree, final int columnIndex, final RuleFieldAccessor getter, Map<Integer, List<Listener>> paintListeners) {
	            
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

	            public void addPainterFor(final Tree tree, final int columnIndex, final RuleFieldAccessor getter, Map<Integer, List<Listener>> paintListeners) {
	                
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
	   
	   
	   public static CellPainterBuilder styledTextBuilder(final FontBuilder builder) {
	        
	        return new CellPainterBuilder() {

	        	final Display display = Display.getCurrent();
	        	final TextLayout layout = new TextLayout(display);
	        	final TextStyle style = builder.style(display);
	        	
	        	private boolean adjust(TreeItem tItem, RuleFieldAccessor getter) {
		            Object item = tItem.getData();
		            Object value = null;
		            
		            if (item instanceof Rule) {
		            	value = getter.valueFor((Rule) item);
		            }
		            
		            if (item instanceof RuleCollection) {
		            	value = getter.valueFor((RuleCollection)item);
		            }
		            
		            IndexedString is = (IndexedString)value;
		            String text = value == null ? "" : is.string;
		            		            
		            layout.setText( text );
		            
		            if (StringUtil.isEmpty(text)) {
		            	layout.setStyle(style, 0, 0);
		            } else {
		            	for (int i=0; i<is.indexSpans.size(); i++) {
		            		layout.setStyle(style, is.indexSpans.get(i)[0], is.indexSpans.get(i)[1]-1);
		            	}
		            }
		            
		            return value != null;
	        	}
	        	
	            public void addPainterFor(final Tree tree, final int columnIndex, final RuleFieldAccessor getter, Map<Integer, List<Listener>> listenersByEventCode) {
	                	                
	            	Listener paintListener = new Listener() {
	            		public void handleEvent(Event event) {
	            			if (event.index != columnIndex) return; 

	            			adjust((TreeItem)event.item, getter);
	            			layout.draw(event.gc, event.x, event.y);
	            			};
	                	};
	                
	            	Listener measureListener = new Listener() {
	            		public void handleEvent(Event event) {
	            			if (event.index != columnIndex) return;
	            			
	            			adjust((TreeItem)event.item, getter);
	            			
	            			final Rectangle textLayoutBounds = layout.getBounds();
	            			event.width = textLayoutBounds.width + 2;
	            			event.height = textLayoutBounds.height + 2;
	            		}
	            	};


	                addListener(tree, SWT.PaintItem, paintListener, listenersByEventCode);
	                addListener(tree, SWT.MeasureItem, measureListener, listenersByEventCode);
	            };
	        };
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
         
        target.append(items[0]);
        for (int i=1; i<items.length; i++) {
            target.append(separator).append(items[i]);
        }
    }
    
//	private static Map<Object, Image> imagesFor(Map<Object, String> imageNamesByValue) {
//		
//		Map<Object, Image> imagesByValue = new HashMap<Object, Image>(imageNamesByValue.size());
//		for (Map.Entry<Object, String> entry : imageNamesByValue.entrySet()) {
//			imagesByValue.put(entry.getKey(), ResourceManager.imageFor(entry.getValue()));
//			}
//		return imagesByValue;
//	}

//	public static CellPainterBuilder iconsFromNameFor(final int width, final int height, final Map<Object, String> imageNamesByValue) {
//		return iconsFor(width, height, imagesFor(imageNamesByValue));
//	}
//	
//	public static CellPainterBuilder iconsFor(final int width, final int height, final Map<Object, Image> imagesByValue) {
//	    
//	    return new CellPainterBuilder() {
//	        
//	        private Object valueFrom(TreeItem tItem, RuleFieldAccessor getter) {
//	            
//	            Object item = tItem.getData();
//	            if (!(item instanceof Rule)) return null;
//	            return getter.valueFor((Rule) item);
//	        }
//	        	        
//	        public void addPainterFor(final Tree tree, final int columnIndex, final RuleFieldAccessor getter, Map<Integer, List<Listener>> listenersByEventCode) {
//	            
//	            Listener paintListener = new Listener() {
//	                public void handleEvent(Event event) {
//	                    if (event.index != columnIndex) return;
//	                    
//	                    Object value = valueFrom((TreeItem)event.item, getter);
//	                    if (value == null) return;
//	                    Image image = imagesByValue.get(value);
//	                  	event.gc.drawImage(image, event.x+1, event.y+2);                
//	                }
//	            };
//	                               
//	            Listener measureListener = new Listener() {
//	                public void handleEvent(Event e) {
//	                    if (e.index != columnIndex) return;
//	                    e.width = width + 2;
//	                    e.height = height + 2;
//	                }
//	            };
//	            
//	            addListener(tree, SWT.PaintItem, paintListener, listenersByEventCode);
//	            addListener(tree, SWT.MeasureItem, measureListener, listenersByEventCode);
//	        }            
//	    };
//	}
}
