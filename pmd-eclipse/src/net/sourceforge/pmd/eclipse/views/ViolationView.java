package net.sourceforge.pmd.eclipse.views;

import java.util.Iterator;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Implements a view to display violations with more details than
 * in the tasks view.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.4  2003/08/14 16:10:41  phherlin
 * Implementing Review feature (RFE#787086)
 *
 * Revision 1.3  2003/08/13 20:10:20  phherlin
 * Refactoring private->protected to remove warning about non accessible member access in enclosing types
 *
 * Revision 1.2  2003/08/05 19:27:41  phherlin
 * Fixing CoreException when refreshing (Eclipse v3)
 *
 * Revision 1.1  2003/07/07 19:24:54  phherlin
 * Adding PMD violations view
 *
 */
public class ViolationView extends ViewPart implements IOpenListener, ISelectionChangedListener {
    public static final int SORTER_PRIORITY = 1;
    public static final int SORTER_RULE = 2;
    public static final int SORTER_CLASS = 3;
    public static final int SORTER_PACKAGE = 4;
    public static final int SORTER_PROJECT = 5;

    private TableViewer violationTableViewer;
    private IResource focusResource;
    protected int sorterFlag = SORTER_PRIORITY;
    protected IAction projectSelectAction;
    protected IAction fileSelectAction;
    protected IAction errorHighFilterAction;
    protected IAction errorFilterAction;
    protected IAction warningHighFilterAction;
    protected IAction warningFilterAction;
    protected IAction informationFilterAction;
    protected IAction showRuleAction;
    protected IAction removeViolationAction;
    protected IAction reviewAction;

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(Composite)
     */
    public void createPartControl(Composite parent) {
        buildViolationTableViewer(parent);
        setupActions();
        buildContextMenu();
        buildActionBar();
        violationTableViewer.addOpenListener(this);
        violationTableViewer.addSelectionChangedListener(this);
        violationTableViewer.addFilter(new PriorityFilter(this));
        violationTableViewer.addFilter(new ResourceFilter(this));
        violationTableViewer.setSorter(new ViolationSorter(this));
        violationTableViewer.getTable().addKeyListener(new SupprKeyListener(this));
        getSite().getPage().addPartListener(new PartListener(this));
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setFocus() {
        if (!violationTableViewer.getControl().isDisposed()) {
            violationTableViewer.getControl().setFocus();
        }
    }

    /**
     * @see org.eclipse.jface.viewers.IOpenListener#open(OpenEvent)
     */
    public void open(OpenEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        IMarker marker = (IMarker) selection.getFirstElement();

        if (marker != null) {
            IResource resource = marker.getResource();
            if (resource instanceof IFile) {
                IWorkbenchPage page = getSite().getPage();
                try {
                    page.openEditor(marker, OpenStrategy.activateOnOpen());
                } catch (PartInitException e) {
                    PMDPlugin.getDefault().logError("Ignoring exception when trying to open an editor", e);
                }
            }
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
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
                        editor.gotoMarker(marker);
                    }
                }
            }
        }
    }

    /**
     * Force the violation table to refresh
     */
    public void refresh() {
        // Seems this can be called when table is disposed ; so test before
        // refreshing
        if (!violationTableViewer.getControl().isDisposed()) {
            violationTableViewer.getControl().setRedraw(false);
            violationTableViewer.refresh();
            violationTableViewer.getControl().setRedraw(true);
        }
    }

    /**
     * Returns the focusResource.
     * @return IResource
     */
    public IResource getFocusResource() {
        return focusResource;
    }

    /**
     * Update the resource that has currently the focus on the page
     */
    public void setFocusResource(IResource resource) {
        if (resource != null && !resource.equals(focusResource)) {
            boolean updateNeeded = false;
            if (projectSelectAction.isChecked()) {
                IProject oldProject = focusResource == null ? null : focusResource.getProject();
                IProject newProject = resource.getProject();
                boolean projectsEqual = (oldProject == null ? newProject == null : oldProject.equals(newProject));
                updateNeeded = !projectsEqual;
            } else if (fileSelectAction.isChecked()) {
                updateNeeded = true;
            }

            focusResource = resource;

            if (updateNeeded) {
                refresh();
            }
        }
    }
    
    /**
     * Test the file filter
     */
    public boolean isFileSelection() {
        return fileSelectAction.isChecked();
    }

    /**
     * Set the file filter mode
     */
    public void setFileSelection(boolean flChecked) {
        fileSelectAction.setChecked(flChecked);
    }
    
    /**
     * Test the project filter
     */
    public boolean isProjectSelection() {
        return projectSelectAction.isChecked();
    }

    /**
     * Set the project filter mode
     */
    public void setProjectSelection(boolean flChecked) {
        projectSelectAction.setChecked(flChecked);
    }
    
    /**
     * Test the error high filter
     */
    public boolean isErrorHighFilterChecked() {
        return errorHighFilterAction.isChecked();
    }
    
    /**
     * Test the error filter
     */
    public boolean isErrorFilterChecked() {
        return errorFilterAction.isChecked();
    }
    
    /**
     * Test the warning high filter
     */
    public boolean isWarningHighFilterChecked() {
        return warningHighFilterAction.isChecked();
    }
    
    /**
     * Test the warning filter
     */
    public boolean isWarningFilterChecked() {
        return warningFilterAction.isChecked();
    }
    
    /**
     * Test the information filter
     */
    public boolean isInformationFilterChecked() {
        return informationFilterAction.isChecked();
    }

    /**
     * Returns the sorterFlag.
     * @return int
     */
    public int getSorterFlag() {
        return sorterFlag;
    }

    /**
     * Returns the rule from the first selected violation
     */
    public Rule getSelectedViolationRule() {
        Rule rule = null;
        try {
            IMarker[] markers = getSelectedViolations();
            if (markers != null) {
                rule = PMDPlugin.getDefault().getRuleSet().getRuleByName(markers[0].getAttribute(PMDPlugin.KEY_MARKERATT_RULENAME, ""));
            }
        } catch (RuntimeException e) {
            PMDPlugin.getDefault().logError(PMDConstants.MSGKEY_ERROR_RUNTIME_EXCEPTION, e);
        }

        return rule;
    }

    /**
     * Return the selected violation
     */
    public IMarker[] getSelectedViolations() {
        IMarker[] markers = null;
        ISelection selection = violationTableViewer.getSelection();
        if ( (selection != null) && (selection instanceof IStructuredSelection)) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            markers = new IMarker[structuredSelection.size()];
            Iterator i = structuredSelection.iterator();
            int index = 0;
            while (i.hasNext()) {
                markers[index++] = (IMarker) i.next();
            }
        }

        return markers;
    }
    
    /**
     * Remove the selected violation
     */
    public void removeSelectedViolation() {
        IMarker[] markers = getSelectedViolations();
        if (markers != null) {
            try {
                Workspace workspace = (Workspace) ResourcesPlugin.getWorkspace();
                workspace.prepareOperation();
                workspace.beginOperation(true);
                for (int i = 0; i < markers.length; i++) {
                    markers[i].delete();
                }
                workspace.endOperation(false, null);
            } catch (CoreException e) {
                PMDPlugin.getDefault().logError(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION, e);
            }
        }
    }

    /**
     * Return the appropriate resource displayed in the view.
     * Currently, it returns always the workspace root
     */
    private IResource getTargetResource() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    protected String getMessage(String key) {
        return PMDPlugin.getDefault().getMessage(key);
    }

    /**
     * Build the violation table
     */
    private void buildViolationTableViewer(Composite parent) {
        int tableStyle = SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;
        violationTableViewer = new TableViewer(parent, tableStyle);
        violationTableViewer.setUseHashlookup(true);

        Table violationTable = violationTableViewer.getTable();
        TableColumn priorityColumn = new TableColumn(violationTable, SWT.LEFT);
        priorityColumn.setResizable(false);
        priorityColumn.setText("");
        priorityColumn.setWidth(30);
        priorityColumn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (sorterFlag != SORTER_PRIORITY) {
                    sorterFlag = SORTER_PRIORITY;
                    refresh();
                }
            }
        });

        TableColumn messageColumn = new TableColumn(violationTable, SWT.LEFT);
        messageColumn.setResizable(true);
        messageColumn.setText(getMessage(PMDConstants.MSGKEY_VIEW_COLUMN_MESSAGE));
        messageColumn.setWidth(300);

        TableColumn ruleColumn = new TableColumn(violationTable, SWT.LEFT);
        ruleColumn.setResizable(true);
        ruleColumn.setText(getMessage(PMDConstants.MSGKEY_VIEW_COLUMN_RULE));
        ruleColumn.setWidth(100);
        ruleColumn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (sorterFlag != SORTER_RULE) {
                    sorterFlag = SORTER_RULE;
                    refresh();
                }
            }
        });

        TableColumn fileColumn = new TableColumn(violationTable, SWT.LEFT);
        fileColumn.setResizable(true);
        fileColumn.setText(getMessage(PMDConstants.MSGKEY_VIEW_COLUMN_CLASS));
        fileColumn.setWidth(150);
        fileColumn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (sorterFlag != SORTER_CLASS) {
                    sorterFlag = SORTER_CLASS;
                    refresh();
                }
            }
        });

        TableColumn packageColumn = new TableColumn(violationTable, SWT.LEFT);
        packageColumn.setResizable(true);
        packageColumn.setText(getMessage(PMDConstants.MSGKEY_VIEW_COLUMN_PACKAGE));
        packageColumn.setWidth(300);
        packageColumn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (sorterFlag != SORTER_PACKAGE) {
                    sorterFlag = SORTER_PACKAGE;
                    refresh();
                }
            }
        });

        TableColumn projectColumn = new TableColumn(violationTable, SWT.LEFT);
        projectColumn.setResizable(true);
        projectColumn.setText(getMessage(PMDConstants.MSGKEY_VIEW_COLUMN_PROJECT));
        projectColumn.setWidth(80);
        projectColumn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (sorterFlag != SORTER_PROJECT) {
                    sorterFlag = SORTER_PROJECT;
                    refresh();
                }
            }
        });

        TableColumn lineColumn = new TableColumn(violationTable, SWT.LEFT);
        lineColumn.setResizable(false);
        lineColumn.setText(getMessage(PMDConstants.MSGKEY_VIEW_COLUMN_LOCATION));
        lineColumn.setWidth(50);

        violationTable.setLinesVisible(true);
        violationTable.setHeaderVisible(true);

        violationTableViewer.setContentProvider(new ResourceMarkerContentProvider());
        violationTableViewer.setLabelProvider(new MarkerLabelProvider());
        violationTableViewer.setInput(getTargetResource());

    }

    /**
     * Build the table context menu (essentially to enable other plugins to
     * add items
     */
    private void buildContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                MenuManager subMenuMgr = new MenuManager(getMessage(PMDConstants.MSGKEY_VIEW_MENU_RESOURCE_FILTER));
                subMenuMgr.add(projectSelectAction);
                subMenuMgr.add(fileSelectAction);
                manager.add(subMenuMgr);

                subMenuMgr = new MenuManager(getMessage(PMDConstants.MSGKEY_VIEW_MENU_PRIORITY_FILTER));
                subMenuMgr.add(errorHighFilterAction);
                subMenuMgr.add(errorFilterAction);
                subMenuMgr.add(warningHighFilterAction);
                subMenuMgr.add(warningFilterAction);
                subMenuMgr.add(informationFilterAction);
                manager.add(subMenuMgr);

                manager.add(showRuleAction);
                manager.add(removeViolationAction);
                manager.add(reviewAction);

                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end"));
            }
        });

        Table table = violationTableViewer.getTable();
        table.setMenu(menuMgr.createContextMenu(table));

        getSite().registerContextMenu(menuMgr, violationTableViewer);
    }

    /**
     * Build the action bar
     */
    private void buildActionBar() {
        IActionBars actionBars = getViewSite().getActionBars();
        IToolBarManager toolBarMgr = actionBars.getToolBarManager();

        toolBarMgr.add(removeViolationAction);
        
        toolBarMgr.add(new Separator());

        toolBarMgr.add(errorHighFilterAction);
        toolBarMgr.add(errorFilterAction);
        toolBarMgr.add(warningHighFilterAction);
        toolBarMgr.add(warningFilterAction);
        toolBarMgr.add(informationFilterAction);

        toolBarMgr.add(new Separator());

        toolBarMgr.add(projectSelectAction);
        toolBarMgr.add(fileSelectAction);

        toolBarMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        toolBarMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end"));
    }

    /**
     * Build the action bar
     */
    private void setupActions() {
        projectSelectAction = new ProjectSelectionAction(this);
        projectSelectAction.setText(getMessage(PMDConstants.MSGKEY_VIEW_ACTION_PROJECT));
        projectSelectAction.setToolTipText(getMessage(PMDConstants.MSGKEY_VIEW_TOOLTIP_PROJECT));
        projectSelectAction.setImageDescriptor(PMDPlugin.getDefault().getImageDescriptor(PMDPlugin.ICON_PROJECT));

        fileSelectAction = new FileSelectAction(this);
        fileSelectAction.setText(getMessage(PMDConstants.MSGKEY_VIEW_ACTION_FILE));
        fileSelectAction.setToolTipText(getMessage(PMDConstants.MSGKEY_VIEW_TOOLTIP_FILE));
        fileSelectAction.setImageDescriptor(PMDPlugin.getDefault().getImageDescriptor(PMDPlugin.ICON_FILE));

        errorHighFilterAction = new PriorityFilterAction(this, PMDPlugin.SETTINGS_VIEW_ERRORHIGH_FILTER);
        errorHighFilterAction.setText(getMessage(PMDConstants.MSGKEY_VIEW_ACTION_ERRORHIGH));
        errorHighFilterAction.setToolTipText(getMessage(PMDConstants.MSGKEY_VIEW_TOOLTIP_ERRORHIGH_FILTER));
        errorHighFilterAction.setImageDescriptor(PMDPlugin.getDefault().getImageDescriptor(PMDPlugin.ICON_PRIO1));

        errorFilterAction = new PriorityFilterAction(this, PMDPlugin.SETTINGS_VIEW_ERROR_FILTER);
        errorFilterAction.setText(getMessage(PMDConstants.MSGKEY_VIEW_ACTION_ERROR));
        errorFilterAction.setToolTipText(getMessage(PMDConstants.MSGKEY_VIEW_TOOLTIP_ERROR_FILTER));
        errorFilterAction.setImageDescriptor(PMDPlugin.getDefault().getImageDescriptor(PMDPlugin.ICON_PRIO2));

        warningHighFilterAction = new PriorityFilterAction(this, PMDPlugin.SETTINGS_VIEW_WARNINGHIGH_FILTER);
        warningHighFilterAction.setText(getMessage(PMDConstants.MSGKEY_VIEW_ACTION_WARNINGHIGH));
        warningHighFilterAction.setToolTipText(getMessage(PMDConstants.MSGKEY_VIEW_TOOLTIP_WARNINGHIGH_FILTER));
        warningHighFilterAction.setImageDescriptor(PMDPlugin.getDefault().getImageDescriptor(PMDPlugin.ICON_PRIO3));

        warningFilterAction = new PriorityFilterAction(this, PMDPlugin.SETTINGS_VIEW_WARNING_FILTER);
        warningFilterAction.setText(getMessage(PMDConstants.MSGKEY_VIEW_ACTION_WARNING));
        warningFilterAction.setToolTipText(getMessage(PMDConstants.MSGKEY_VIEW_TOOLTIP_WARNING_FILTER));
        warningFilterAction.setImageDescriptor(PMDPlugin.getDefault().getImageDescriptor(PMDPlugin.ICON_PRIO4));

        informationFilterAction = new PriorityFilterAction(this, PMDPlugin.SETTINGS_VIEW_INFORMATION_FILTER);
        informationFilterAction.setText(getMessage(PMDConstants.MSGKEY_VIEW_ACTION_INFORMATION));
        informationFilterAction.setToolTipText(getMessage(PMDConstants.MSGKEY_VIEW_TOOLTIP_INFORMATION_FILTER));
        informationFilterAction.setImageDescriptor(PMDPlugin.getDefault().getImageDescriptor(PMDPlugin.ICON_PRIO5));

        showRuleAction = new ShowRuleAction(this);
        showRuleAction.setText(getMessage(PMDConstants.MSGKEY_VIEW_ACTION_SHOW_RULE));
        showRuleAction.setToolTipText(getMessage(PMDConstants.MSGKEY_VIEW_TOOLTIP_SHOW_RULE));
        
        removeViolationAction = new RemoveViolationAction(this);
        removeViolationAction.setText(getMessage(PMDConstants.MSGKEY_VIEW_ACTION_REMOVE_VIOLATION));
        removeViolationAction.setToolTipText(getMessage(PMDConstants.MSGKEY_VIEW_TOOLTIP_REMOVE_VIOLATION));
        removeViolationAction.setImageDescriptor(PMDPlugin.getDefault().getImageDescriptor(PMDPlugin.ICON_REMVIO));

        reviewAction = new ReviewAction(this);
        reviewAction.setText(getMessage(PMDConstants.MSGKEY_VIEW_ACTION_REVIEW));
        reviewAction.setToolTipText(getMessage(PMDConstants.MSGKEY_VIEW_TOOLTIP_REVIEW));
    }

}
