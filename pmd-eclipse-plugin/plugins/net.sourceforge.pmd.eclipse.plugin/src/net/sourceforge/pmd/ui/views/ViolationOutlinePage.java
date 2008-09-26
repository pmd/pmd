package net.sourceforge.pmd.ui.views;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.ui.model.FileRecord;
import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.ui.views.actions.RemoveViolationAction;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.Page;

/**
 * Creates a Page for the Violation Outline
 * 
 * @author SebastianRaffel ( 08.05.2005 )
 */
public class ViolationOutlinePage extends Page implements IPage, ISelectionChangedListener {

    private TableViewer tableViewer;
    private ViolationOutline violationOutline;
    private ViewerFilter viewerFilter;
    private FileRecord resource;

    protected final Integer[] columnWidths = {new Integer(20), new Integer(170), new Integer(40)};
    protected final int[] columnSortOrder = { 1, 1, 1 };
    protected int currentSortedColumn;

    /**
     * Constructor
     * 
     * @param resourceRecord, the FileRecord
     * @param outline, the parent Outline
     */
    public ViolationOutlinePage(FileRecord resourceRecord, ViolationOutline outline) {

        resource = resourceRecord;
        violationOutline = outline;

        ViewerFilter[] filters = outline.getFilters();
        for (int i = 0; i < filters.length; i++) {
            if (filters[i] instanceof PriorityFilter)
                viewerFilter = filters[i];
        }
    }

    /* @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite) */
    public void createControl(Composite parent) {
        int tableStyle = SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;
        tableViewer = new TableViewer(parent, tableStyle);
        tableViewer.setUseHashlookup(true);
        tableViewer.getTable().setHeaderVisible(true);

        // create the Table
        createColumns(tableViewer.getTable());
        createActionBars();
        violationOutline.createContextMenu(tableViewer);

        // set the Input
        tableViewer.setContentProvider(new ViolationOutlineContentProvider(this));
        tableViewer.setLabelProvider(new ViolationOutlineLabelProvider());
        tableViewer.setInput(resource);

        // add the Filter and Listener
        tableViewer.addFilter(viewerFilter);
        tableViewer.addSelectionChangedListener(this);
    }

    /**
     * Create the Columns
     * 
     * @param table
     */
    private void createColumns(Table viewerTable) {
        // Image for the Priority
        final TableColumn priorityColumn = new TableColumn(viewerTable, SWT.LEFT);
        priorityColumn.setWidth(20);
        priorityColumn.setResizable(false);

        // the Error-Message
        final TableColumn messageColumn = new TableColumn(viewerTable, SWT.LEFT);
        messageColumn.setWidth(170);
        messageColumn.setText(getString(StringKeys.MSGKEY_VIEW_OUTLINE_COLUMN_MESSAGE));

        // the Line, the Error occured
        final TableColumn lineColumn = new TableColumn(viewerTable, SWT.RIGHT);
        lineColumn.setWidth(40);
        lineColumn.setText(getString(StringKeys.MSGKEY_VIEW_OUTLINE_COLUMN_LINE));

        createColumnAdapters(tableViewer.getTable());
        tableViewer.setSorter(getViewerSorter(0));
    }

    /**
     * Creates Adapter for sorting and resizing the Columns
     * 
     * @param table
     */
    private void createColumnAdapters(Table table) {
        TableColumn[] columns = table.getColumns();

        for (int k = 0; k < columns.length; k++) {
            columnWidths[k] = new Integer(columns[k].getWidth());
            final int i = k;
            // the Sorter
            columns[k].addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    currentSortedColumn = i;
                    columnSortOrder[currentSortedColumn] *= -1;
                    tableViewer.setSorter(getViewerSorter(currentSortedColumn));
                }
            });
            // the Resizing
            columns[k].addControlListener(new ControlAdapter() {
                public void controlResized(ControlEvent e) {
                    columnWidths[i] = new Integer(tableViewer.getTable().getColumn(i).getWidth());
                }
            });
        }
    }

    /**
     * Creates the ActionBars
     */
    private void createActionBars() {
        IToolBarManager manager = getSite().getActionBars().getToolBarManager();

        Action removeViolationAction = new RemoveViolationAction(tableViewer);
        manager.add(removeViolationAction);
        manager.add(new Separator());
    }

    /**
     * Returns the ViewerSorter
     * 
     * @param column, the Number of the Column
     * @return, the Sorter for this Column
     */
    private ViewerSorter getViewerSorter(int columnNr) {
        TableColumn column = tableViewer.getTable().getColumn(columnNr);
        final int sortOrder = columnSortOrder[columnNr];

        switch (columnNr) {
        // sorts by the Priority
        case 0:
            return new TableColumnSorter(column, sortOrder) {
                public int compare(Viewer viewer, Object e1, Object e2) {
                    IMarker marker1 = (IMarker) e1;
                    IMarker marker2 = (IMarker) e2;

                    Integer prio1 = new Integer(0);
                    Integer prio2 = new Integer(0);

                    try {
                        prio1 = (Integer) marker1.getAttribute(PMDUiConstants.KEY_MARKERATT_PRIORITY);
                        prio2 = (Integer) marker2.getAttribute(PMDUiConstants.KEY_MARKERATT_PRIORITY);

                        if (prio1.equals(prio2)) {
                            prio1 = (Integer) marker1.getAttribute(IMarker.LINE_NUMBER);
                            prio2 = (Integer) marker2.getAttribute(IMarker.LINE_NUMBER);
                        }
                    } catch (CoreException ce) {
                        PMDPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION, ce);
                    }

                    return prio1 == null ? 0 : prio1.compareTo(prio2) * sortOrder;
                }
            };
        // sorts by the Message
        case 1:
            return new TableColumnSorter(column, sortOrder) {
                public int compare(Viewer viewer, Object e1, Object e2) {
                    String message1 = "";
                    String message2 = "";

                    try {
                        message1 = String.valueOf(((IMarker) e1).getAttribute(IMarker.MESSAGE));
                        message2 = String.valueOf(((IMarker) e2).getAttribute(IMarker.MESSAGE));
                    } catch (CoreException ce) {
                        PMDPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION + this.toString(), ce);
                    }

                    return message1 == null ? 0 : message1.compareTo(message2) * sortOrder;
                }
            };
        // sorts by the Line-Number
        case 2:
            return new TableColumnSorter(column, sortOrder) {
                public int compare(Viewer viewer, Object e1, Object e2) {
                    Integer prio1;
                    Integer prio2;
                    try {
                        prio1 = (Integer) ((IMarker) e1).getAttribute(IMarker.LINE_NUMBER);
                        prio2 = (Integer) ((IMarker) e2).getAttribute(IMarker.LINE_NUMBER);
                        return prio1 == null ? 0 : prio1.compareTo(prio2) * sortOrder;
                    } catch (CoreException ce) {
                        ce.printStackTrace();
                    }
                    return 0;
                }
            };
        }
        return null;
    }

    /**
     * @return the Viewer
     */
    public TableViewer getTableViewer() {
        return tableViewer;
    }

    /* @see org.eclipse.ui.part.IPage#getControl() */
    public Control getControl() {
        return tableViewer.getControl();
    }

    /* @see org.eclipse.ui.part.IPage#setFocus() */
    public void setFocus() {
        tableViewer.getTable().setFocus();
    }

    /**
     * @return the underlying FileRecord
     */
    public FileRecord getResource() {
        return resource;
    }

    /**
     * Sets the Column's Widths
     * 
     * @param widths, an Integer-Array containg the Widths
     */
    public void setColumnWidths(Integer[] widths) {
        if (tableViewer.getTable().isDisposed())
            return;

        TableColumn[] columns = tableViewer.getTable().getColumns();
        for (int k = 0; k < widths.length; k++) {
            if (widths[k] == null) {
                widths[k] = k == 0 ? new Integer(20) : k == 1 ? new Integer(170) : new Integer(40);
            }
            columns[k].setWidth(widths[k].intValue());
        }
        
        System.arraycopy(widths, 0, this.columnWidths, 0, Math.min(this.columnWidths.length, widths.length));
    }

    /**
     * @return an Array wid5th the Column's Widths
     */
    public Integer[] getColumnWidths() {
        return columnWidths;
    }

    /**
     * Sets the Properties to sort by
     * 
     * @param properties, an Integer-Array with the Number of Column to sort by [0] and the Direction to sort by (-1 or 1) [1]
     */
    public void setSorterProperties(Integer[] properties) {
        if (properties.length > 0) {
            this.currentSortedColumn = properties[0].intValue();
            this.columnSortOrder[currentSortedColumn] = properties[1].intValue();
            this.tableViewer.setSorter(getViewerSorter(this.currentSortedColumn));
        }
    }

    /**
     * @return the properties to sort by
     */
    public Integer[] getSorterProperties() {
        return new Integer[] { new Integer(currentSortedColumn), new Integer(columnSortOrder[currentSortedColumn]) };
    }

    /**
     * Refreshes the View
     */
    public void refresh() {
        if (!tableViewer.getControl().isDisposed()) {
            tableViewer.getControl().setRedraw(false);
            tableViewer.refresh();
            tableViewer.getControl().setRedraw(true);
        }

    }

    /* @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent) */
    public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        IMarker marker = (IMarker) selection.getFirstElement();
        if (marker != null) {
            IEditorPart editor = getSite().getPage().getActiveEditor();
            if (editor != null) {
                IEditorInput input = editor.getEditorInput();
                if (input instanceof IFileEditorInput) {
                    IFile file = ((IFileEditorInput) input).getFile();
                    if (marker.getResource().equals(file)) {
                        IDE.gotoMarker(editor, marker);
                    }
                }
            }
        }
    }

    /**
     * Helper method to return an NLS string from its key
     */
    private String getString(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }
}
