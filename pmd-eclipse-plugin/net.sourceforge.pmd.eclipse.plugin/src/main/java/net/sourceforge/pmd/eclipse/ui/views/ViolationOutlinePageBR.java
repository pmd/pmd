package net.sourceforge.pmd.eclipse.ui.views;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.ItemColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.preferences.br.BasicTableManager;
import net.sourceforge.pmd.eclipse.ui.preferences.br.MarkerColumnsUI;
import net.sourceforge.pmd.eclipse.ui.views.actions.RemoveViolationAction;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.Page;

/**
 * Creates a Page for the Violation Outline
 * 
 * @author Brian Remedios
 */
public class ViolationOutlinePageBR extends Page implements IPage, ISelectionChangedListener, RefreshableTablePage {

    private TableViewer					tableViewer;
    private ViolationOutline			violationOutline;
    private ViewerFilter				viewerFilter;
    private FileRecord					resource;
    protected int 						currentSortedColumn;
    private BasicTableManager<IMarker>	tableManager;

	public static final ItemColumnDescriptor<?,IMarker>[] AvailableColumns = new ItemColumnDescriptor[] {
		MarkerColumnsUI.priority,
		MarkerColumnsUI.lineNumber,
//		MarkerColumnsUI.done,
		MarkerColumnsUI.created,
		MarkerColumnsUI.ruleName,
		MarkerColumnsUI.message
		};
	
    /**
     * Constructor
     * 
     * @param resourceRecord, the FileRecord
     * @param outline, the parent Outline
     */
    public ViolationOutlinePageBR(FileRecord resourceRecord, ViolationOutline outline) {

        resource = resourceRecord;
        violationOutline = outline;

        ViewerFilter[] filters = outline.getFilters();
        for (int i = 0; i < filters.length; i++) {
            if (filters[i] instanceof PriorityFilter)
                viewerFilter = filters[i];
        }
    }

    public TableViewer tableViewer() { return tableViewer; }
    
    public void createControl(Composite parent) {
    	
    	tableManager = new BasicTableManager<IMarker>("rscViolations", PMDPlugin.getDefault().loadPreferences(), AvailableColumns);
        tableViewer = tableManager.buildTableViewer(parent);

        tableManager.setupColumns(AvailableColumns);
        tableManager.setTableMenu(violationOutline.createContextMenu(tableViewer));

        // create the Table
        createActionBars();
        
        // set the Input
        tableViewer.setContentProvider(new ViolationOutlineContentProvider(this));

        tableViewer.setInput(resource);

        // add the Filter and Listener
        tableViewer.addFilter(viewerFilter);
        tableViewer.addSelectionChangedListener(this);
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
        if (marker == null) return;
            
        IEditorPart editor = getSite().getPage().getActiveEditor();
        if (editor == null) return;
            
        IEditorInput input = editor.getEditorInput();
        if (input instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput) input).getFile();
            if (marker.getResource().equals(file)) {
               IDE.gotoMarker(editor, marker);
               }
            }
    }

}
