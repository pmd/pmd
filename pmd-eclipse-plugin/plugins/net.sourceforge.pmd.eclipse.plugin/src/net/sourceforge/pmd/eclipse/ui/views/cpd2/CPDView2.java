package net.sourceforge.pmd.eclipse.ui.views.cpd2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * An updated view for Cut & Paste Detector that shows the results in a tree table 
 * with the file matches as columns at the root level with actual code snippets 
 * that span all columns beneath them.  Clicking on the class names brings up
 * the relevant sections in the code editor.
 * 
 * @author Brian Remedios
 */
public class CPDView2 extends ViewPart implements IPropertyListener {
	
    private TreeViewer			treeViewer;
    private TreeNodeContentProvider contentProvider;
    private CPDViewLabelProvider2 labelProvider;
    private int[] 				columnWidths;
    
    private Listener			measureListener;
    private Listener			resizeListener;
    private Color 				classColor;
    private Color 				packageColor;
    private Map<String, int[]>	nameWidthsByName;
    private TreeColumn 			messageColumn;	// we adjust the width of this one 
    
    private static final int SpanColumnWidth = 50;
    private static final int MAX_MATCHES = 100;
    private static final int xGap = 6;
    private static final String TabEquivalent = "    ";	// tab char == 4 spaces
    public static final int SourceColumnIdx = 1;
    
    private static List<Match> asList(Iterator<Match> matchIter) {
    	
    	List<Match> matches = new ArrayList<Match>(MAX_MATCHES);
    	
    	for (int count = 0; matchIter.hasNext() && count < MAX_MATCHES; count++) {
            matches.add(matchIter.next());
    		}
    	
    	Collections.sort(matches, Match.MATCHES_COMPARATOR);
    	Collections.reverse(matches);
    	
    	return matches;
    }
    
    public static String[] partsOf(String fullName) {
    	
    	int pos = fullName.lastIndexOf('.');
    	
    	return new String[] {
    		fullName.substring(0, pos+1),
    		fullName.substring(pos+1)
    		};
    }
    
    public static String[] sourceLinesFrom(Match match, boolean trimLeadingWhitespace) {
    	
        final String text = match.getSourceCodeSlice().replaceAll("\t", TabEquivalent);
        final StringTokenizer lines = new StringTokenizer(text, "\n");
        
        List<String> sourceLines = new ArrayList<String>();
        
        for (int i=0; lines.hasMoreTokens(); i++) {
        	String line = lines.nextToken();
            sourceLines.add(line);
        }

        String[] lineArr = new String[sourceLines.size()];
        lineArr = sourceLines.toArray(lineArr);
        
        if (trimLeadingWhitespace) {        	
            int trimDepth = StringUtil.maxCommonLeadingWhitespaceForAll(lineArr);
      	  	if (trimDepth > 0) {
      	  		lineArr = StringUtil.trimStartOn(lineArr, trimDepth);
      	  }
        }
        return lineArr;
    }
    
    /*
     * @see org.eclipse.ui.ViewPart#init(org.eclipse.ui.IViewSite)
     */
    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        contentProvider = new TreeNodeContentProvider();
        labelProvider = new CPDViewLabelProvider2();
        
        measureListener = new Listener() {
			public void handleEvent(Event event) {
				captureColumnWidths();
			}
        };
        
        resizeListener = new Listener() {
			public void handleEvent(Event event) {
				int width = treeViewer.getTree().getBounds().width;
				messageColumn.setWidth(width - SpanColumnWidth);
				captureColumnWidths();
				treeViewer.refresh();
			}
        };
        
        nameWidthsByName = new HashMap<String, int[]>();
    }

    public int widthOf(int columnIndex) {
    	if (columnWidths == null) captureColumnWidths();
    	return columnWidths[columnIndex];
    }
    
    private void captureColumnWidths() {
    	
    	TreeColumn[] columns = treeViewer.getTree().getColumns();
    	columnWidths = new int[columns.length];
    	
    	for (int i=0; i<columnWidths.length; i++) {
    		columnWidths[i] = columns[i].getWidth();
    	}
    }

    /*
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
        int treeStyle = SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;
        treeViewer = new TreeViewer(parent, treeStyle);
        treeViewer.setUseHashlookup(true);
        Tree tree = treeViewer.getTree();
        tree.addListener(SWT.Move, measureListener);
        tree.addListener(SWT.Resize, resizeListener);
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        addPainters(tree);
        
        treeViewer.setContentProvider(contentProvider);
        treeViewer.setLabelProvider(labelProvider);
        addDeleteListener(treeViewer.getControl());
        
        createColumns(tree);
        
        CPDViewTooltipListener2 tooltipListener = new CPDViewTooltipListener2(this);
        tree.addListener(SWT.MouseMove, tooltipListener);
        tree.addListener(SWT.MouseHover, tooltipListener);
        tree.addListener(SWT.MouseDown, tooltipListener);
        
        Display disp = tree.getDisplay();
        classColor = disp.getSystemColor(SWT.COLOR_BLUE);
        packageColor = disp.getSystemColor(SWT.COLOR_GRAY);
    }

	protected void addDeleteListener(Control control) {

		control.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ev) {
				if (ev.character == SWT.DEL) {
					removeSelectedItems();
				}
			}
		});	
	}
    
	// TODO fix - not deleting 'model' elements?
	private void removeSelectedItems() {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		Object[] items = selection.toArray();
		treeViewer.remove(items);
	}
	
    public int inColumn(Point point) {
    	
    	if (columnWidths == null) return -1;
    	
    	int pos = 0;
    	
    	for (int i=0; i<columnWidths.length; i++) {
    		if (pos < point.x && pos + columnWidths[i] > point.x)  {
    			return i;
    		}
    		pos += columnWidths[i];
    	}
    	
    	return -1;
    }
    
    public int[] widthsFor(String name) {
    	return nameWidthsByName.get(name);
    }
    
    private void paintName(GC gc, int x, int y, String name, int rightEdge, int descent, int cellWidth) {

    	String[] parts = partsOf(name);
    	int packageWidth = 0;
    	int classWidth = 0;

    	int[] widths = nameWidthsByName.get(name);

    	if (widths != null) {
    		packageWidth = widths[0];
    		classWidth = widths[1];
	    	} else {
	    		gc.setFont( treeViewer.getTree().getFont() );
	    		packageWidth = gc.stringExtent(parts[0]).x;
	    		classWidth = gc.stringExtent(parts[1]).x;
	    		nameWidthsByName.put(name, new int[] {packageWidth, classWidth} );
	    		}
    	
    	int drawX = x + rightEdge - classWidth - xGap;
//    	Rectangle clipRect = new Rectangle(x, y, cellWidth, 24);
  
    	gc.setForeground(classColor);
//    	gc.drawRectangle(clipRect);
		gc.drawText(parts[1], drawX, y + descent, false);
//		gc.setClipping((Rectangle)null);
		
		drawX = x + rightEdge - classWidth - packageWidth - xGap;
//		clipRect.x = drawX;
//		clipRect.width = packageWidth;
		
		gc.setForeground(packageColor);
//    	gc.drawRectangle(clipRect);
		gc.drawText(parts[0], drawX, y + descent, false);
//		gc.setClipping((Rectangle)null);
    }
    
    private void addPainters(Tree tree) {
    	
    	Listener paintListener = new Listener() {
      		public void handleEvent(Event event) {
      			if (event.index != SourceColumnIdx) return; 

      			Object item = ((TreeNode)event.item.getData()).getValue();
      			
      			String[] names = null;
      			if (item instanceof Match) {
      				names = CPDViewLabelProvider2.sourcesFor((Match)item);
      				} else {
      					return;
      				}

      			int descent = event.gc.getFontMetrics().getDescent();
      			int colWidth = widthOf(SourceColumnIdx);     			
      			int cellWidth = colWidth / names.length;
      			
      			for (int i=0; i<names.length; i++) {
      				int rightEdge = colWidth - (cellWidth * i);
      				paintName(event.gc, event.x, event.y, names[i], rightEdge, descent, cellWidth);
      				}
      			};
          	};
          
      	Listener measureListener = new Listener() {
      		public void handleEvent(Event event) {
      			if (event.index != SourceColumnIdx) return;
      			      				            			
      			event.width = 400;
      			event.height = 24;
      		}
      	};


        tree.addListener(SWT.PaintItem, paintListener);
        tree.addListener(SWT.MeasureItem, measureListener);
    }
    
    /**
     * Creates the columns of the tree.
     * @param tree Tree from the treeViewer
     */
    private void createColumns(Tree tree) {
        // the "+"-sign for expanding packages
        TreeColumn plusColumn = new TreeColumn(tree, SWT.RIGHT);
        plusColumn.setText("Spans");
        plusColumn.setWidth(SpanColumnWidth);
  //      plusColumn.setResizable(false);

        // shows the source
        messageColumn = new TreeColumn(tree, SWT.LEFT);
        messageColumn.addListener(SWT.Move, measureListener);
        messageColumn.setText("Source");
        messageColumn.setWidth(500);
    }

    /**
     * @return the tree viewer.
     */
    public TreeViewer getTreeViewer() {
        return treeViewer;
    }

    /**
     * Helper method to return an NLS string from its key
     */
    private String getString(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }

    /*
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        treeViewer.getTree().setFocus();
    }
    
    /**
     * Sets input for the table.
     * @param matches CPD Command that contain the matches from the CPD
     */
    public void setData(Iterator<Match> matches) {
    	    	
        List<TreeNode> elements = new ArrayList<TreeNode>();
        if (matches != null) {
        	
            for (Match match : asList(matches)) {
                
                // create a treenode for the match and add to the list
                TreeNode matchNode = new TreeNode(match); // NOPMD by Sven on 02.11.06 11:27
                elements.add(matchNode);
                
                String[] lines = sourceLinesFrom(match, true);
                TreeNode[] children = new TreeNode[lines.length];
               
                for (int j=0; j<lines.length; j++) {
                	String line = lines[j];
                	if (line == null) {
                		System.out.println();
                	}
                    children[j] = new TreeNode(line);
                    children[j].setParent(matchNode);
                }
                matchNode.setChildren(children);
            }
        }

        // set the children of the rootnode: the matches
        treeViewer.setInput(elements.toArray(new TreeNode[elements.size()]));
    }

    /**
     * After the CPD command is executed, it will trigger an propertyChanged event.
     */
    public void propertyChanged(Object source, int propId) {
        if (propId == PMDRuntimeConstants.PROPERTY_CPD && source instanceof Iterator<?>) {
            Iterator<Match> iter = (Iterator<Match>) source;
            // after setdata(iter) iter.hasNext will always return false
            boolean hasResults = iter.hasNext();
            setData(iter);
            if (!hasResults) {
                // no entries
                MessageBox box = new MessageBox(this.treeViewer.getControl().getShell());
                box.setText(getString(StringKeys.DIALOG_CPD_NORESULTS_HEADER));
                box.setMessage(getString(StringKeys.DIALOG_CPD_NORESULTS_BODY));
                box.open();
            }
        }
    }
}
