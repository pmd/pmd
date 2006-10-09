/*
 * Created on 8 mai 2005
 *
 * Copyright (c) 2005, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.ui.model.FileRecord;
import net.sourceforge.pmd.ui.model.PackageRecord;
import net.sourceforge.pmd.ui.model.ProjectRecord;
import net.sourceforge.pmd.ui.model.RootRecord;
import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.ui.views.actions.CalculateStatisticsAction;
import net.sourceforge.pmd.ui.views.actions.CollapseAllAction;
import net.sourceforge.pmd.ui.views.actions.PackageSwitchAction;
import net.sourceforge.pmd.ui.views.actions.PriorityFilterAction;
import net.sourceforge.pmd.ui.views.actions.ProjectFilterAction;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

/**
 * A View for PMD-Violations, provides an Overview as well as statistical
 * Information
 * 
 * @author SebastianRaffel ( 08.05.2005 ), Philippe Herlin, Sven Jacob
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.3  2006/10/09 13:26:40  phherlin
 * Review Sebastian code... and fix most PMD warnings
 *
 */
public class ViolationOverview extends ViewPart implements IDoubleClickListener, ISelectionChangedListener, ISelectionProvider {
    private TreeViewer treeViewer;
    private ViolationOverviewContentProvider contentProvider;
    private ViolationOverviewLabelProvider labelProvider;
    private PriorityFilter priorityFilter;
    private ProjectFilter projectFilter;

    private RootRecord root;
    private AbstractPMDRecord currentProject;
    private ViewMemento memento;

    private PriorityFilterAction[] priorityActions;
    private boolean packageFiltered;
    protected Integer[] columnWidths;
    protected int[] columnSortOrder = { 0, 0, 1, -1, -1, -1, 1 };
    protected int currentSortedColumn;

    protected final static String PACKAGE_SWITCH = "packageSwitch";
    protected final static String PRIORITY_LIST = "priorityFilterList";
    protected final static String PROJECT_LIST = "projectFilterList";
    protected final static String COLUMN_WIDTHS = "tableColumnWidths";
    protected final static String COLUMN_SORTER = "tableColumnSorter";

    /**
     * @see org.eclipse.ui.ViewPart#init(org.eclipse.ui.IViewSite)
     */
    public void init(IViewSite site) throws PartInitException {
        super.init(site);

        // init the View, create Content-, LabelProvider and Filters
        // this is called before createPartControl()
        this.root = (RootRecord) getInitialInput();
        this.contentProvider = new ViolationOverviewContentProvider(this);
        this.labelProvider = new ViolationOverviewLabelProvider(this);
        this.priorityFilter = new PriorityFilter();
        this.projectFilter = new ProjectFilter();

        // we can load the Memento here
        this.memento = new ViewMemento(PMDUiConstants.MEMENTO_OVERVIEW_FILE);
        if (this.memento != null) {
            rememberFilterSettings();
        }

    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        final int treeStyle = SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;
        this.treeViewer = new TreeViewer(parent, treeStyle);
        this.treeViewer.setUseHashlookup(true);
        this.treeViewer.getTree().setHeaderVisible(true);
        this.treeViewer.getTree().setLinesVisible(true);

        // set Content- and LabelProvider as well as Filters
        this.treeViewer.setContentProvider(contentProvider);
        this.treeViewer.setLabelProvider(labelProvider);
        this.treeViewer.addFilter(priorityFilter);
        this.treeViewer.addFilter(projectFilter);

        // create the necessary Stuff
        setupActions();
        createColumns(this.treeViewer.getTree());
        createActionBars();
        createDropDownMenu();
        createContextMenu();

        // put in the Input
        // and add Listeners
        this.treeViewer.setInput(root);
        this.treeViewer.addDoubleClickListener(this);
        this.treeViewer.addSelectionChangedListener(this);
        getSite().setSelectionProvider(this);

        // load the State from a Memento into the View if there is one
        if (this.memento != null) {
            rememberTreeSettings();
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        this.memento.putList(PRIORITY_LIST, this.priorityFilter.getPriorityFilterList());

        // on Dispose of the View we save its State into a Memento

        // we save the filtered Projects
        final List projects = this.projectFilter.getProjectFilterList();
        final List projectNames = new ArrayList();
        for (int k = 0; k < projects.size(); k++) {
            final AbstractPMDRecord project = (AbstractPMDRecord) projects.get(k);
            projectNames.add(project.getName());
        }
        this.memento.putList(PROJECT_LIST, projectNames);

        // ... the Columns Widths
        final List widthList = Arrays.asList(columnWidths);
        this.memento.putList(COLUMN_WIDTHS, widthList);

        // ... what Element is sorted in what way
        final Integer[] sorterProps = new Integer[] { new Integer(currentSortedColumn),
                new Integer(columnSortOrder[currentSortedColumn]) };
        final List sorterList = Arrays.asList(sorterProps);
        memento.putList(COLUMN_SORTER, sorterList);

        // ... and how we should display the Elements
        this.memento.putInteger(PACKAGE_SWITCH, this.packageFiltered ? 1 : 0);

        this.memento.save(PMDUiConstants.MEMENTO_OVERVIEW_FILE);

        super.dispose();
    }

    /**
     * Creates the initial Input In General this is a RootRecord for the
     * WorkspaceRoot
     * 
     * @return an Input-Object
     */
    private Object getInitialInput() {
        return new RootRecord(ResourcesPlugin.getWorkspace().getRoot());
    }

    /**
     * Creates the Table's Columns
     * 
     * @param tree
     */
    private void createColumns(Tree tree) {
        // the "+"-Sign for expanding Packages
        final TreeColumn plusColumn = new TreeColumn(tree, SWT.RIGHT);
        plusColumn.setWidth(20);
        plusColumn.setResizable(false);

        // shows the Image
        final TreeColumn imageColumn = new TreeColumn(tree, SWT.CENTER);
        imageColumn.setWidth(20);
        imageColumn.setResizable(false);

        // shows the Elements Name
        final TreeColumn elementColumn = new TreeColumn(tree, SWT.LEFT);
        elementColumn.setText(getString(StringKeys.MSGKEY_VIEW_OVERVIEW_COLUMN_ELEMENT));
        elementColumn.setWidth(200);

        // Number of Violations
        final TreeColumn vioTotalColumn = new TreeColumn(tree, SWT.LEFT);
        vioTotalColumn.setText(getString(StringKeys.MSGKEY_VIEW_OVERVIEW_COLUMN_VIO_TOTAL));
        vioTotalColumn.setWidth(100);

        // Violations / Lines of code
        final TreeColumn vioLocColumn = new TreeColumn(tree, SWT.LEFT);
        vioLocColumn.setText(getString(StringKeys.MSGKEY_VIEW_OVERVIEW_COLUMN_VIO_LOC));
        vioLocColumn.setWidth(100);

        // Violations / Number of Methods
        final TreeColumn vioMethodColumn = new TreeColumn(tree, SWT.LEFT);
        vioMethodColumn.setText(getString(StringKeys.MSGKEY_VIEW_OVERVIEW_COLUMN_VIO_METHOD));
        vioMethodColumn.setWidth(100);

        // Projects Name
        final TreeColumn projectColumn = new TreeColumn(tree, SWT.LEFT);
        projectColumn.setText(getString(StringKeys.MSGKEY_VIEW_OVERVIEW_COLUMN_PROJECT));
        projectColumn.setWidth(100);

        // creates the Sorter and ResizeListener
        createColumnAdapters(this.treeViewer.getTree());
        getViewerSorter(3);
    }

    /**
     * Creates Adapter for sorting and resizing the Columns
     * 
     * @param tree
     */
    private void createColumnAdapters(Tree tree) {
        final TreeColumn[] columns = tree.getColumns();
        this.columnWidths = new Integer[columns.length];

        this.columnWidths[0] = new Integer(columns[0].getWidth());
        this.columnWidths[1] = new Integer(columns[1].getWidth());
        for (int k = 2; k < columns.length; k++) {
            this.columnWidths[k] = new Integer(columns[k].getWidth()); // NOPMD by Herlin on 09/10/06 15:02
            final int i = k;

            // each Column gets a SelectionAdapter
            // on Selection the Column is sorted
            columns[k].addSelectionListener(new SelectionAdapter() { // NOPMD by Herlin on 09/10/06 15:02
                public void widgetSelected(SelectionEvent e) {
                    currentSortedColumn = i;
                    columnSortOrder[currentSortedColumn] *= -1;
                    treeViewer.setSorter(getViewerSorter(currentSortedColumn));
                }
            });

            // the ResizeListener saves the current Width
            // for storing it easily into a Memento later
            columns[k].addControlListener(new ControlAdapter() { // NOPMD by Herlin on 09/10/06 15:02
                public void controlResized(ControlEvent e) {
                    columnWidths[i] = new Integer(treeViewer.getTree().getColumn(i).getWidth());
                }
            });
        }
    }

    /**
     * Creates the ActionBars
     */
    private void createActionBars() {
        final IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();

        // Action for calculating the #violations/loc
        final Action calculateStats = new CalculateStatisticsAction(this);
        manager.add(calculateStats);

        // Action for switching from Packages to Files only
        final Action switchPackagesAction = new PackageSwitchAction(this);
        switchPackagesAction.setChecked(this.packageFiltered);
        manager.add(switchPackagesAction);
        manager.add(new Separator());

        // the PriorityFilter-Actions
        for (int i = 0; i < this.priorityActions.length; i++) {
            manager.add(this.priorityActions[i]);
        }
        manager.add(new Separator());

        // the CollapseAll-Action
        final Action collapseAllAction = new CollapseAllAction(this);
        manager.add(collapseAllAction);
    }

    /**
     * Creates the DropDownMenu
     */
    private void createDropDownMenu() {
        final IMenuManager manager = getViewSite().getActionBars().getMenuManager();
        manager.removeAll();

        // both, Context- and DropDownMenu contain the same
        // SubMenu for filtering Projects
        createProjectFilterMenu(manager);
    }

    /**
     * Creates the Context Menu
     */
    private void createContextMenu() {
        final MenuManager manager = new MenuManager();
        manager.setRemoveAllWhenShown(true);
        manager.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                MenuManager submenuManager;

                // one SubMenu for filtering Projects
                submenuManager = new MenuManager(getString(StringKeys.MSGKEY_VIEW_MENU_RESOURCE_FILTER));
                createProjectFilterMenu(submenuManager);
                manager.add(submenuManager);

                // ... another one for filtering Priorities
                submenuManager = new MenuManager(getString(StringKeys.MSGKEY_VIEW_MENU_PRIORITY_FILTER));
                for (int i = 0; i < priorityActions.length; i++) {
                    submenuManager.add(priorityActions[i]);
                }
                manager.add(submenuManager);

                // addtions Action: Clear PMD Violations
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end"));
            }
        });

        final Tree tree = this.treeViewer.getTree();
        tree.setMenu(manager.createContextMenu(tree));
        getSite().registerContextMenu(manager, this.treeViewer);
    }

    /**
     * Return the ViewerSorter for a Column
     * 
     * @param column, the Number of the Column in the Table
     * @return the ViewerSorter for the column
     */
    private ViewerSorter getViewerSorter(int columnNr) {
        final TreeColumn column = this.treeViewer.getTree().getColumn(columnNr);
        final int sortOrder = this.columnSortOrder[columnNr];
        ViewerSorter viewerSorter = null;

        switch (columnNr) {

        // sorts by Number of Violations
        case 3:
            viewerSorter = newViolationsCountSorter(column, sortOrder);
            break;

        // sorts by Violations per LOC
        case 4:
            viewerSorter = newViolationsPerLOCSorter(column, sortOrder);
            break;

        // sorts by Violations per Number of Methods
        case 5:
            viewerSorter = newViolationsPerMethodsCount(column, sortOrder);
            break;

        // sorts by Name of Project
        case 6:
            viewerSorter = newProjectNameSorter(column, sortOrder);
            break;

        // sorts the Packages and Files by Name
        case 2:
        default:
            viewerSorter = newPackagesSorter(column, sortOrder);
            break;
        }

        return viewerSorter;
    }

    /**
     * Setup the Actions for the ActionBars
     */
    protected void setupActions() {
        final Integer[] priorities = PMDUiPlugin.getDefault().getPriorityValues();
        this.priorityActions = new PriorityFilterAction[priorities.length];

        // create the Actions for the PriorityFilter
        for (int i = 0; i < priorities.length; i++) {
            this.priorityActions[i] = new PriorityFilterAction(priorities[i], this); // NOPMD by Herlin on 09/10/06 15:02

            if (this.priorityFilter.getPriorityFilterList().contains(priorities[i])) {
                this.priorityActions[i].setChecked(true);
            }
        }
    }

    /**
     * Create the Menu for filtering Projects
     * 
     * @param manager, the MenuManager
     */
    protected void createProjectFilterMenu(IMenuManager manager) {
        final List projectFilterList = this.projectFilter.getProjectFilterList();
        final List projectList = new ArrayList();
        if (this.root != null) {
            // We get a List of all Projects
            final AbstractPMDRecord[] projects = this.root.getChildren();
            for (int i = 0; i < projects.length; i++) {
                final ProjectRecord project = (ProjectRecord) projects[i];
                // if the Project contains Errors,
                // we add a FilterAction for it
                if (project.hasMarkers()) {
                    final Action projectFilterAction = new ProjectFilterAction(project, this); // NOPMD by Herlin on 09/10/06 15:03

                    // if it is not already in the List,
                    // we set it as "visible"
                    if (!projectFilterList.contains(projects[i])) { // NOPMD by Herlin on 09/10/06 15:04
                        projectFilterAction.setChecked(true);
                    }

                    manager.add(projectFilterAction);
                    projectList.add(project);
                }
            }
            manager.add(new Separator());

            // this Action filters the Project the
            // currently selected Element belongs to
            final Action currentProjectAction = new Action() {
                public void run() {
                    projectFilter.setProjectFilterList(projectList);
                    projectFilter.removeProjectFromList(currentProject);
                    refresh();
                }
            };
            currentProjectAction.setText(getString(StringKeys.MSGKEY_VIEW_ACTION_CURRENT_PROJECT));

            manager.add(currentProjectAction);
        }
    }

    /**
     * Gets the Violations that are filtered, meaning, if e.g. the Priorities 4
     * and 5 are filtered, this Function returns the Number of all Priority 1,2
     * and 3-Markers
     * 
     * @param element
     * @return the Number of visible Violations for the given Element
     */
    public int getFilteredViolations(Object element) {
        IMarker[] markers;
        int violations = 0;
        final List filterList = this.priorityFilter.getPriorityFilterList();

        // for both, PackageRecord and FileRecord
        // we go through the FilterList and get every Marker
        // of every Priority in the List, and add them

        if (element instanceof PackageRecord) {
            final PackageRecord packageRec = (PackageRecord) element;
            for (int i = 0; i < filterList.size(); i++) {
                final Integer priority = (Integer) filterList.get(i);
                markers = packageRec.findMarkersByAttribute(PMDUiConstants.KEY_MARKERATT_PRIORITY, priority);
                if (markers != null) {
                    violations += markers.length;
                }
            }
        } else if (element instanceof FileRecord) {
            final FileRecord fileRec = (FileRecord) element;
            for (int i = 0; i < filterList.size(); i++) {
                final Integer priority = (Integer) filterList.get(i);
                markers = fileRec.findMarkersByAttribute(PMDUiConstants.KEY_MARKERATT_PRIORITY, priority);
                if (markers != null) {
                    violations += markers.length;
                }
            }
        }

        return violations;
    }

    /**
     * Sets the View to show Packages and files (false) or Files only (true)
     * 
     * @param packageFiltered
     */
    public void setPackageFiltered(boolean packageFiltered) {
        this.packageFiltered = packageFiltered;
    }

    /**
     * Returns the State, if Packages are to filter
     * 
     * @return true, if only Files should be seen, false if Packages and Files
     *         should be displayed
     */
    public boolean isPackageFiltered() {
        return this.packageFiltered;
    }

    /**
     * Sets the Widths of the Columns
     */
    public void setColumnWidths() {
        if (!this.treeViewer.getTree().isDisposed()) {
            final TreeColumn[] columns = this.treeViewer.getTree().getColumns();
            for (int k = 0; k < this.columnWidths.length; k++) {
                columns[k].setWidth(this.columnWidths[k].intValue());
            }
        }
    }

    /**
     * Sets the Properties for Sorting
     * 
     * @param properties, an Array with Properties, the First Value is the
     *            Number of the sorted Column, the Second one is the Direction
     *            (-1 or 1)
     */
    public void setSorterProperties(Integer[] properties) { // NOPMD by Herlin on 09/10/06 15:03
        this.currentSortedColumn = properties[0].intValue();
        this.columnSortOrder[this.currentSortedColumn] = properties[1].intValue();

        this.treeViewer.setSorter(getViewerSorter(this.currentSortedColumn));
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setFocus() {
        this.treeViewer.getTree().setFocus();
    }

    /**
     * @return the viewer
     */
    public TreeViewer getViewer() {
        return this.treeViewer;
    }

    /**
     * Refresh the View (and its Elements)
     */
    public void refresh() {
        if (!this.treeViewer.getControl().isDisposed()) {
            this.treeViewer.getControl().setRedraw(false);
            this.treeViewer.refresh();
            createDropDownMenu();
            this.treeViewer.getControl().setRedraw(true);
        }
    }

    /**
     * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
     */
    public void doubleClick(DoubleClickEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        final Object object = selection.getFirstElement();

        // on DoubleClick on a PackageRecord
        // it displays the underlying FileRecords
        if (object instanceof PackageRecord) {
            final PackageRecord packageRec = (PackageRecord) object;
            if (this.treeViewer.getExpandedState(packageRec)) {
                this.treeViewer.collapseToLevel(packageRec, TreeViewer.ALL_LEVELS);
            } else {
                this.treeViewer.expandToLevel(packageRec, 1);
            }
        } else if (object instanceof FileRecord) {
            try {
                // ... on a FileRecord, it opens the corresponding File
                final IFile file = (IFile) ((FileRecord) object).getResource();
                IDE.openEditor(getSite().getPage(), file);
            } catch (PartInitException pie) {
                PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_VIEW_EXCEPTION + this.toString(), pie);
            }
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged(SelectionChangedEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        final Object object = selection.getFirstElement();

        // get the Project of the current Selection
        // this is used with the ProjectFilter

        AbstractPMDRecord project = null;
        if (object instanceof PackageRecord) {
            project = ((PackageRecord) object).getParent();
        } else if (object instanceof FileRecord) {
            project = ((FileRecord) object).getParent().getParent();
        }

        if (project != null) {
            this.currentProject = project;
        }

    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        this.treeViewer.addSelectionChangedListener(listener);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    public ISelection getSelection() {
        return this.treeViewer.getSelection();
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        this.treeViewer.removeSelectionChangedListener(listener);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection) {
        this.treeViewer.setSelection(selection);
    }

    /**
     * Helper method to return an NLS string from its key
     */
    private String getString(String key) {
        return PMDUiPlugin.getDefault().getStringTable().getString(key);
    }

    /**
     * Apply the memento for filters. Before calling this private method, one
     * must be sure memento is not null.
     * 
     */
    private void rememberFilterSettings() {

        // Provide the Filters with their last State
        final List priorityList = this.memento.getIntegerList(PRIORITY_LIST);
        if (priorityList != null) {
            this.priorityFilter.setPriorityFilterList(priorityList);
        }

        final List projectNames = this.memento.getStringList(PROJECT_LIST);
        if (projectNames != null) {
            final List projectList = new ArrayList();
            for (int k = 0; k < projectNames.size(); k++) {
                final AbstractPMDRecord project = this.root.findResourceByName(projectNames.get(k).toString(),
                        AbstractPMDRecord.TYPE_PROJECT);
                if (project != null) {
                    projectList.add(project);
                }
            }

            this.projectFilter.setProjectFilterList(projectList);
        }

        final Integer packageFiltered = this.memento.getInteger(PACKAGE_SWITCH);
        if ((packageFiltered != null) && (packageFiltered.intValue() == 1)) {
            this.packageFiltered = true;
        }
    }

    /**
     * Apply the memento for tree options. Before calling this private method,
     * one must be sure memento is not null.
     * 
     */
    private void rememberTreeSettings() {

        // the Memento sets the Widths of Columns
        final List widthList = this.memento.getIntegerList(COLUMN_WIDTHS);
        if (widthList != null) {
            this.columnWidths = new Integer[widthList.size()];
            widthList.toArray(this.columnWidths);
            setColumnWidths();
        }

        // ... and also the Sorter
        final List sorterList = this.memento.getIntegerList(COLUMN_SORTER);
        if (sorterList != null) {
            final Integer[] sorterProps = new Integer[sorterList.size()];
            sorterList.toArray(sorterProps);
            setSorterProperties(sorterProps);
        }
    }

    /**
     * Build a viewer sorter for the packages/files column.
     * 
     * @param column
     * @param sortOrder
     * @return
     */
    private ViewerSorter newPackagesSorter(TreeColumn column, final int sortOrder) {
        return new TableColumnSorter(column, sortOrder) {
            public int compare(Viewer viewer, Object e1, Object e2) {
                String name1 = "";
                String name2 = "";

                if ((e1 instanceof PackageRecord) && (e2 instanceof PackageRecord)) {
                    name1 = ((PackageRecord) e1).getName();
                    name2 = ((PackageRecord) e2).getName();
                } else if ((e1 instanceof FileRecord) && (e2 instanceof FileRecord)) {
                    name1 = ((FileRecord) e1).getName();
                    name2 = ((FileRecord) e2).getName();
                }

                return name1.compareToIgnoreCase(name2) * sortOrder;
            }
        };
    }

    /**
     * Build a sorter for the numbers of violations column.
     * 
     * @param column
     * @param sortOrder
     * @return
     */
    private ViewerSorter newViolationsCountSorter(TreeColumn column, final int sortOrder) {
        return new TableColumnSorter(column, sortOrder) {
            public int compare(Viewer viewer, Object e1, Object e2) {
                final int vio1 = getFilteredViolations(e1);
                final int vio2 = getFilteredViolations(e2);

                return new Integer(vio1).compareTo(new Integer(vio2)) * sortOrder;
            }
        };
    }

    /**
     * Build a sorter for the violations per LOC column.
     * 
     * @param column
     * @param sortOrder
     * @return
     */
    private ViewerSorter newViolationsPerLOCSorter(TreeColumn column, final int sortOrder) {
        return new TableColumnSorter(column, sortOrder) {
            public int compare(Viewer viewer, Object e1, Object e2) {
                final int vio1 = getFilteredViolations(e1);
                final int vio2 = getFilteredViolations(e2);
                int loc1 = 0;
                int loc2 = 0;

                if ((e1 instanceof PackageRecord) && (e2 instanceof PackageRecord)) {
                    final PackageRecord pack1 = ((PackageRecord) e1);
                    final PackageRecord pack2 = ((PackageRecord) e2);

                    final Object[] files1 = pack1.getChildren();
                    for (int i = 0; i < files1.length; i++) {
                        loc1 += ((FileRecord) files1[i]).getLinesOfCode();
                    }

                    final Object[] files2 = pack2.getChildren();
                    for (int j = 0; j < files2.length; j++) {
                        loc2 += ((FileRecord) files2[j]).getLinesOfCode();
                    }
                } else if ((e1 instanceof FileRecord) && (e2 instanceof FileRecord)) {
                    loc1 = ((FileRecord) e1).getLinesOfCode();
                    loc2 = ((FileRecord) e2).getLinesOfCode();
                }

                final Float vioPerLoc1 = new Float((float) vio1 / loc1);
                final Float vioPerLoc2 = new Float((float) vio2 / loc2);

                return vioPerLoc1.compareTo(vioPerLoc2) * sortOrder;
            }
        };
    }

    /**
     * Build a sorter for the violations per numbers of methods column.
     * 
     * @param column
     * @param sortOrder
     * @return
     */
    private ViewerSorter newViolationsPerMethodsCount(TreeColumn column, final int sortOrder) {
        return new TableColumnSorter(column, sortOrder) {
            public int compare(Viewer viewer, Object e1, Object e2) {
                final int vio1 = getFilteredViolations(e1);
                final int vio2 = getFilteredViolations(e2);
                int numMethods1 = 0;
                int numMethods2 = 0;

                if ((e1 instanceof PackageRecord) && (e2 instanceof PackageRecord)) {
                    final PackageRecord pack1 = ((PackageRecord) e1);
                    final PackageRecord pack2 = ((PackageRecord) e2);

                    final Object[] files1 = pack1.getChildren();
                    for (int i = 0; i < files1.length; i++) {
                        numMethods1 += ((FileRecord) files1[i]).getNumberOfMethods();
                    }

                    final Object[] files2 = pack2.getChildren();
                    for (int j = 0; j < files2.length; j++) {
                        numMethods2 += ((FileRecord) files2[j]).getNumberOfMethods();
                    }
                } else if ((e1 instanceof FileRecord) && (e2 instanceof FileRecord)) {
                    numMethods1 = ((FileRecord) e1).getNumberOfMethods();
                    numMethods2 = ((FileRecord) e2).getNumberOfMethods();
                }

                Float vioPerMethod1;
                if (numMethods1 == 0) {
                    vioPerMethod1 = new Float(0.0f);
                } else {
                    vioPerMethod1 = new Float((float) vio1 / numMethods1);
                }

                Float vioPerMethod2;
                if (numMethods2 == 0) {
                    vioPerMethod2 = new Float(0.0f);
                } else {
                    vioPerMethod2 = new Float((float) vio2 / numMethods2);
                }

                return vioPerMethod1.compareTo(vioPerMethod2) * sortOrder;
            }
        };
    }

    /**
     * Build a sorter for the project name column.
     * 
     * @param column
     * @param sortOrder
     * @return
     */
    private ViewerSorter newProjectNameSorter(TreeColumn column, final int sortOrder) {
        return new TableColumnSorter(column, sortOrder) {
            public int compare(Viewer viewer, Object e1, Object e2) {
                AbstractPMDRecord project1 = null;
                AbstractPMDRecord project2 = null;

                if ((e1 instanceof PackageRecord) && (e2 instanceof PackageRecord)) {
                    project1 = ((PackageRecord) e1).getParent();
                    project2 = ((PackageRecord) e2).getParent();
                } else if ((e1 instanceof FileRecord) && (e2 instanceof FileRecord)) {
                    project1 = ((FileRecord) e1).getParent().getParent();
                    project2 = ((FileRecord) e2).getParent().getParent();
                }

                return (project1.getName()).compareToIgnoreCase(project2.getName()) * sortOrder;
            }
        };
    }
}
