package net.sourceforge.pmd.eclipse.views;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.model.DataflowMethodRecord;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


/**
 * Shows Dataflow Anomalies
 * 
 * @author SebastianRaffel  ( 06.06.2005 )
 */
public class DataflowAnomalyTableViewer extends TableViewer {
	
	protected Integer[] columnWidths;
	protected int[] columnSortOrder = {1,1,1};
	protected int currentSortedColumn;
	
	public DataflowAnomalyTableViewer(Composite parent, int style, 
			DataflowView view) {
		super(parent, style | SWT.H_SCROLL | SWT.V_SCROLL 
			| SWT.SINGLE | SWT.FULL_SELECTION);
		setUseHashlookup(true);
		
		getTable().setHeaderVisible(true);
		getTable().setLinesVisible(true);
		
		GridData tableData = new GridData(GridData.FILL_BOTH);
		getTable().setLayoutData(tableData);
		
		createColumns(getTable());
		
		GridLayout tableLayout = new GridLayout(1, false);
		tableLayout.horizontalSpacing = tableLayout.verticalSpacing = 0;
		getTable().setLayout(tableLayout);
		
		addSelectionChangedListener(view);
	}
	
	/**
	 * Cresate the Columns for th Table
	 * 
	 * @param table
	 */
	private void createColumns(Table table) {
		// type of Anomaly
		TableColumn typeColumn = new TableColumn(table, SWT.LEFT);
		typeColumn.setWidth(80);
		typeColumn.setText(PMDPlugin.getDefault().getMessage(
			PMDConstants.MSGKEY_VIEW_DATAFLOW_TABLE_COLUMN_TYPE));
		
		// Line(s) where the Anomaly occures
		TableColumn lineColumn = new TableColumn(table, SWT.RIGHT);
		lineColumn.setWidth(100);
		lineColumn.setText(PMDPlugin.getDefault().getMessage(
			PMDConstants.MSGKEY_VIEW_DATAFLOW_TABLE_COLUMN_LINE));
		
		// Variable
		TableColumn varColumn = new TableColumn(table, SWT.RIGHT);
		varColumn.setWidth(70);
		varColumn.setText(PMDPlugin.getDefault().getMessage(
			PMDConstants.MSGKEY_VIEW_DATAFLOW_TABLE_COLUMN_VARIABLE));
		
		// set Sorter and ResizeListener
		createColumnAdapters(table);
		setSorter(getViewerSorter(1));
	}
	
	/**
	 * Creates Adapter for sorting and resizing the Columns
	 * 
	 * @param table
	 */
	private void createColumnAdapters(Table table) {
		TableColumn[] columns = table.getColumns();
		columnWidths = new Integer[columns.length];
		
		for (int k=0; k<columns.length; k++) {
			columnWidths[k] = new Integer(columns[k].getWidth());
			final int i = k;
			columns[k].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
	        		currentSortedColumn = i;
	        		columnSortOrder[currentSortedColumn] *= -1;
	        		setSorter(getViewerSorter(currentSortedColumn));
	        	}
	        });
			columns[k].addControlListener(new ControlAdapter() {
	        	public void controlResized(ControlEvent e) {
	        		columnWidths[i] = new Integer(
	        			getTable().getColumn(i).getWidth());
	        	}
			});
		}
	}
	
	/**
	 * Returns the ViewerSorter for Column with the given Number
	 * 
	 * @param columnNr
	 * @return the ViewerSorter for a Column
	 */
	private ViewerSorter getViewerSorter(int columnNr) {
		TableColumn column = getTable().getColumn(columnNr);
		final int sortOrder = columnSortOrder[columnNr];
		
		switch (columnNr) {
			// sort by Anomaly-Type-Name
			case 0: return new TableColumnSorter(column, sortOrder) {
				public int compare(Viewer viewer, Object e1, Object e2) {
					String message1 = 
						((IMarker) e1).getAttribute(IMarker.MESSAGE, "");
					String message2 = 
						((IMarker) e2).getAttribute(IMarker.MESSAGE, "");
					
					if (message1.equalsIgnoreCase(message2)) {
						int m1_l1 = 0;
						int m1_l2 = 0;
						int m2_l1 = 0;
						int m2_l2 = 0;
						
						m1_l1 = ((IMarker) e1).getAttribute(
							IMarker.LINE_NUMBER, 0);
						m1_l2 = ((IMarker) e1).getAttribute(
							PMDPlugin.KEY_MARKERATT_LINE2, 0);
						m2_l1 = ((IMarker) e2).getAttribute(
							IMarker.LINE_NUMBER, 0);
						m2_l2 = ((IMarker) e2).getAttribute(
							PMDPlugin.KEY_MARKERATT_LINE2, 0);
						
						Integer line1 = new Integer(
							(m1_l1 < m1_l2) ? (m1_l1) : (m1_l2));
						Integer line2 = new Integer(
							(m2_l1 < m2_l2) ? (m2_l1) : (m2_l2));
						
						return line1.compareTo(line2) * sortOrder;
					}
					
					return message1.compareToIgnoreCase(message2) * sortOrder;
				}
			};
			// sort by the Line(s) where the anomaly occures
			case 1: return new TableColumnSorter(column, sortOrder) {
				public int compare(Viewer viewer, Object e1, Object e2) {
					int m1_l1 = 0;
					int m1_l2 = 0;
					int m2_l1 = 0;
					int m2_l2 = 0;
					
					m1_l1 = ((IMarker) e1).getAttribute(
						IMarker.LINE_NUMBER, 0);
					m1_l2 = ((IMarker) e1).getAttribute(
						PMDPlugin.KEY_MARKERATT_LINE2, 0);
					m2_l1 = ((IMarker) e2).getAttribute(
						IMarker.LINE_NUMBER, 0);
					m2_l2 = ((IMarker) e2).getAttribute(
						PMDPlugin.KEY_MARKERATT_LINE2, 0);
					
					Integer line1 = new Integer(
						(m1_l1 < m1_l2) ? (m1_l1) : (m1_l2));
					Integer line2 = new Integer(
						(m2_l1 < m2_l2) ? (m2_l1) : (m2_l2));
					
					return line1.compareTo(line2) * sortOrder;
				}
			};
			// sort by the Variable's Name
			case 2: return new TableColumnSorter(column, sortOrder) {
				public int compare(Viewer viewer, Object e1, Object e2) {
					String var1 = ((IMarker) e1).getAttribute(
						PMDPlugin.KEY_MARKERATT_VARIABLE, "");
					String var2 = ((IMarker) e2).getAttribute(
						PMDPlugin.KEY_MARKERATT_VARIABLE, "");
					
					return var1.compareToIgnoreCase(var2) * sortOrder;
				}
			};
		}
		
		return null;
	}
	
	/**
	 * Gives an Input to the Table 
	 * 
	 * @param pmdMethod
	 * @param javaMethod
	 */
	public void setData(SimpleNode pmdMethod, IMethod javaMethod) {
		setContentProvider(new DataflowAnomalyTableContentProvider());
		setLabelProvider(new DataflowAnomalyTableLabelProvider());
		
		setInput(new DataflowMethodRecord(javaMethod, pmdMethod));
	}
	
	/**
	 * Shows or hides the Table 
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		getTable().setVisible(visible);
	}
}
