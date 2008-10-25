package net.sourceforge.pmd.eclipse.ui.views;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

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
 * @author SebastianRaffel ( 06.06.2005 )
 */
public class DataflowAnomalyTableViewer extends TableViewer {
    protected Integer[] columnWidths;
    protected int[] columnSortOrder = { 1, 1, 1 };
    protected int currentSortedColumn;

    public DataflowAnomalyTableViewer(Composite parent, int style) {
        super(parent, style | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
        setUseHashlookup(true);

        getTable().setHeaderVisible(true);
        getTable().setLinesVisible(true);

        createColumns(getTable());
        GridData tableData = new GridData(GridData.FILL_BOTH);
        getTable().setLayoutData(tableData);
        GridLayout tableLayout = new GridLayout(1, false);
        tableLayout.horizontalSpacing = tableLayout.verticalSpacing = 0;
        getTable().setLayout(tableLayout);
    }
    
    /**
     * Cresate the Columns for th Table
     * 
     * @param table
     */
    private void createColumns(Table table) {
        // type of Anomaly
        final TableColumn typeColumn = new TableColumn(table, SWT.LEFT);
        typeColumn.setWidth(80);
        typeColumn.setText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_TABLE_COLUMN_TYPE));
        typeColumn.setToolTipText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_TABLE_COLUMN_TYPE_TOOLTIP));
        
        // Line(s) where the Anomaly occures
        final TableColumn lineColumn = new TableColumn(table, SWT.RIGHT);
        lineColumn.setWidth(100);
        lineColumn.setText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_TABLE_COLUMN_LINE));

        // Variable
        final TableColumn varColumn = new TableColumn(table, SWT.RIGHT);
        varColumn.setWidth(70);
        varColumn.setText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_TABLE_COLUMN_VARIABLE));

        // Method
        final TableColumn methodColumn = new TableColumn(table, SWT.RIGHT);
        methodColumn.setWidth(100);
        methodColumn.setText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_TABLE_COLUMN_METHOD));

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
        final TableColumn[] columns = table.getColumns();
        columnWidths = new Integer[columns.length];

        for (int k = 0; k < columns.length; k++) {
            columnWidths[k] = Integer.valueOf(columns[k].getWidth());
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
                    columnWidths[i] = Integer.valueOf(getTable().getColumn(i).getWidth());
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
        final TableColumn column = getTable().getColumn(columnNr);
        final int sortOrder = columnSortOrder[columnNr];
        TableColumnSorter sorter = null;
        
        switch (columnNr) {
        // sort by Anomaly-Type-Name
        case 0:
            sorter = new TableColumnSorter(column, sortOrder) {
                public int compare(Viewer viewer, Object e1, Object e2) {
                    int result = 0;
                    if (e1 instanceof RuleViolation && e2 instanceof RuleViolation) {
                        final RuleViolation violation1 = (RuleViolation) e1;
                        final RuleViolation violation2 = (RuleViolation) e2;
                        final String message1 = violation1.getDescription();
                        final String message2 = violation2.getDescription();

                        if (message1.equalsIgnoreCase(message2)) {
                            final int m1_l1 = violation1.getBeginLine();
                            final int m1_l2 = violation2.getBeginLine();
                            final int m2_l1 = violation1.getEndLine();
                            final int m2_l2 = violation2.getEndLine();

                            final Integer line1 = Integer.valueOf((m1_l1 < m1_l2) ? (m1_l1) : (m1_l2));
                            final Integer line2 = Integer.valueOf((m2_l1 < m2_l2) ? (m2_l1) : (m2_l2));

                            result = line1.compareTo(line2) * sortOrder;
                        } else {
                            result = message1.compareToIgnoreCase(message2) * sortOrder;
                        }    
                    } 
                    return result;
                }
            };
            break;

        case 1:
            // sort by the Line(s) where the anomaly occures
            sorter = new TableColumnSorter(column, sortOrder) {
                public int compare(Viewer viewer, Object e1, Object e2) {
                    int result = 0;
                    if (e1 instanceof RuleViolation && e2 instanceof RuleViolation) {
                        final RuleViolation violation1 = (RuleViolation) e1;
                        final RuleViolation violation2 = (RuleViolation) e2;

                        final int m1_l1 = violation1.getBeginLine();
                        final int m1_l2 = violation2.getBeginLine();
                        final int m2_l1 = violation1.getEndLine();
                        final int m2_l2 = violation2.getEndLine();
    
                        final Integer line1 = Integer.valueOf((m1_l1 < m1_l2) ? (m1_l1) : (m1_l2));
                        final Integer line2 = Integer.valueOf((m2_l1 < m2_l2) ? (m2_l1) : (m2_l2));
                        result = line1.compareTo(line2) * sortOrder;
                    }
                    return result;
                }
            };
            break;

        case 2:
            // sort by the Variable's Name
            sorter =  new TableColumnSorter(column, sortOrder) {
                public int compare(Viewer viewer, Object e1, Object e2) {
                    int result = 0;
                    if (e1 instanceof RuleViolation && e2 instanceof RuleViolation) {
                        final RuleViolation violation1 = (RuleViolation) e1;
                        final RuleViolation violation2 = (RuleViolation) e2;
    
                        final String var1 = violation1.getVariableName();
                        final String var2 = violation2.getVariableName();
                        result = var1.compareToIgnoreCase(var2) * sortOrder;
                    }
                    return result;
                }
            };
            break;
        
        default:
            // do nothing
        }

        return sorter;
    }

    /**
     * Shows or hides the Table
     * 
     * @param visible
     */
    public void setVisible(boolean visible) {
        getTable().setVisible(visible);
        ((GridData)getTable().getLayoutData()).exclude = !visible;
    }

    /**
     * Helper method to return an NLS string from its key
     */
    private String getString(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }
}
