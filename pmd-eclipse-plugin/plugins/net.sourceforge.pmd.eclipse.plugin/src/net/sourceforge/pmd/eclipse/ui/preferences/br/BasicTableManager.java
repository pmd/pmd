package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.Comparator;

import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.ui.AbstractColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.BasicTableLabelProvider;
import net.sourceforge.pmd.eclipse.ui.ColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.ItemColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.ItemFieldAccessor;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
/**
 * 
 * @author Brian Remedios
 *
 * @param <T>
 */

public class BasicTableManager <T extends Object> extends AbstractTableManager<T> {

	protected TableViewer tableViewer;
	
//	public interface Predicate0 {
//		boolean value();
//	}
	
	// TODO move to util
	public static int columnIndexAt(TableItem item, int xPosition) {

		TableColumn[] cols = item.getParent().getColumns();
		Rectangle bounds = null;

		for(int i = 0; i < cols.length; i++){
			bounds = item.getBounds(i);
			if (bounds.x < xPosition && xPosition < (bounds.x + bounds.width)) {
				return i;
			}
		}
		return -1;
	}
	
	private static final int DefaultTableStyle = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;
	
	public BasicTableManager(String theWidgetId, IPreferences thePreferences, ColumnDescriptor[] theColumns) {
		super(theWidgetId, thePreferences, theColumns);
	}

//	public void setStyle(int columnNo, Predicate0 predicate, TextStyle ifTrue, TextStyle ifFalse) {
//		
//	}
	
	protected String idFor(Object column) {
		return ((TableColumn)column).getToolTipText();
	}
	
	protected ColumnWidthAdapter columnAdapterFor(ColumnDescriptor desc) {
		TableColumn column = columnFor(desc);
		return adapterFor(column);
	}
	
	private TableColumn columnFor(ColumnDescriptor desc) {
	   	for (TableColumn column : tableViewer.getTable().getColumns()) {
	   		if ((column.getData(AbstractColumnDescriptor.DescriptorKey)) == desc) return column;
	    	}
	   	return null;
	}
	
    private TableColumn columnFor(String tooltipText) {
    	for (TableColumn column : tableViewer.getTable().getColumns()) {
    		if (String.valueOf(column.getToolTipText()).equals(tooltipText)) return column;
    	}
    	return null;
    }
    
    private ViewerSorter createSorter() {
    	
    	return new ViewerSorter() {
			
		    public int compare(Viewer viewer, Object e1, Object e2) {
		    	
		    	ItemFieldAccessor<?,T> acc = (ItemFieldAccessor<?,T>)columnSorter;
		    	Comparator comp = acc.comparator();
		    	if (comp == null) return 0;
		    	
		    	Object v1 = acc.valueFor((T)e1);
		    	Object v2 = acc.valueFor((T)e2);
		    	
		    	if (v1 == null) {
		    		return v2 == null ? 0 : -1;
		    		} else {
		    			if (v2 == null) {
		    				return v1 == null ? 0 : 1;
		    			}
		    		}
		    	
		        int result = comp.compare(v1, v2);
		        return sortDescending ? 0 - result : result;
		    }
		};
    }
    
	protected void redrawTable(String sortColumnLabel, int sortDir) {
		
		Table table = tableViewer.getTable();
		
		tableViewer.setComparator( createSorter() );
		
		TableColumn sortColumn = columnFor(sortColumnLabel);
		table.setSortColumn(sortColumn);
		table.setSortDirection(sortDir);
	}
	

	
	public TableViewer buildTableViewer(Composite parent) {
		return buildTableViewer(parent, DefaultTableStyle);
	}
	
	public TableViewer buildTableViewer(Composite parent, int tableStyle) {
		
		tableViewer = new TableViewer(parent, tableStyle);
		tableViewer.setUseHashlookup(true);
		
		final Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				selectedItems(selection.toArray());
			}
		});
		
		addDeleteListener(table);	
		
		table.addListener(SWT.MouseMove, new Listener() {
	        public void handleEvent(Event event) {
	        	Point point = new Point(event.x, event.y);
	            TableItem item = table.getItem(point);
	            if (item != null) {
	            	int columnIndex = columnIndexAt(item, event.x);
	            	updateTooltipFor(item, columnIndex);
	            }
	        }
	    });
		
		setupMenusFor(table);
		
		return tableViewer;
	}
	
	/**
	 *
	 * @param columnDescs RuleColumnDescriptor[]
	 * @param groupingField RuleFieldAccessor
	 */
	public void setupColumns(ItemColumnDescriptor[] columnDescs) {

		Table table = tableViewer.getTable();
		
		for (int i=0; i<columnDescs.length; i++) {
			TableColumn tc = columnDescs[i].buildTableColumn(table, this);
		}

		tableViewer.setLabelProvider( new BasicTableLabelProvider(columnDescs) );
		
		TableColumn[] columns = table.getColumns();
		for (TableColumn column : columns) column.pack();
	}
	
	protected void updateTooltipFor(TableItem item, int columnIndex) {
		
	}
	
	protected void selectedItems(Object[] items) {
		// TODO
	}
	
	@Override
	protected void removeSelectedItems() {
		// TODO Auto-generated method stub		
	}	
	
	protected int headerHeightFor(Control control) {
		return ((Table)control).getHeaderHeight();		
	}
	
	protected void setMenu(Control control, Menu menu) {
		((Table)control).setMenu(menu);
	}
	
	protected Rectangle clientAreaFor(Control control) {
		return ((Table)control).getClientArea();
	}

	@Override
	protected void saveItemSelections() {
		// TODO Auto-generated method stub
		
	}

}
