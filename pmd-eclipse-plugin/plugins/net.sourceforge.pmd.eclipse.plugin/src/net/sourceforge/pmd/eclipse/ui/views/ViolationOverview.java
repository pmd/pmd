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

package net.sourceforge.pmd.eclipse.ui.views; // NOPMD by Sven on 13.11.06 11:45

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.builder.MarkerUtil;
import net.sourceforge.pmd.eclipse.runtime.cmd.DeleteMarkersCommand;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.model.FileToMarkerRecord;
import net.sourceforge.pmd.eclipse.ui.model.MarkerRecord;
import net.sourceforge.pmd.eclipse.ui.model.PackageRecord;
import net.sourceforge.pmd.eclipse.ui.model.RootRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.util.NumericConstants;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * A View for PMD-Violations, provides an Overview as well as statistical
 * Information
 *
 * @author SebastianRaffel ( 08.05.2005 ), Philippe Herlin, Sven Jacob
 *
 */
public class ViolationOverview extends ViewPart implements ISelectionProvider, ITreeViewerListener { // NOPMD by Sven on 13.11.06 11:45

    private TreeViewer							treeViewer;
    private ViolationOverviewContentProvider	contentProvider;
    private ViolationOverviewLabelProvider		labelProvider;
    private PriorityFilter						priorityFilter;
    private ProjectFilter						projectFilter;
    private ViolationOverviewMenuManager 		menuManager;
    private ViolationOverviewDoubleClickListener doubleClickListener;

    private RootRecord 		root;
    private ViewMemento 	memento;

    protected final Integer[] 	columnWidths = new Integer[5];
    protected final int[] 		columnSortOrder = { 1, -1, -1, -1, 1 };
    protected int 				currentSortedColumn;
    private int 				showType;

    protected final static String PACKAGE_SWITCH = "packageSwitch";
    protected final static String PRIORITY_LIST = "priorityFilterList";
    protected final static String PROJECT_LIST = "projectFilterList";
    protected final static String COLUMN_WIDTHS = "tableColumnWidths";
    protected final static String COLUMN_SORTER = "tableColumnSorter";

    public final static int SHOW_PACKAGES_FILES_MARKERS = 1;	// Shows packages -> files -> markers
    public final static int SHOW_FILES_MARKERS			= 2;	// Shows files -> markers without packages
    public final static int SHOW_MARKERS_FILES			= 3;	// Shows markers -> files without packages

    /**
     * @see org.eclipse.ui.ViewPart#init(org.eclipse.ui.IViewSite)
     */
    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);

        // init the View, create Content-, LabelProvider and Filters
        // this is called before createPartControl()
        root = (RootRecord) getInitialInput();
        contentProvider = new ViolationOverviewContentProvider(this);
        labelProvider = new ViolationOverviewLabelProvider(this);
        priorityFilter = new PriorityFilter();
        projectFilter = new ProjectFilter();
        doubleClickListener = new ViolationOverviewDoubleClickListener(this);
        menuManager = new ViolationOverviewMenuManager(this);

        showType = SHOW_PACKAGES_FILES_MARKERS;

        // we can load the Memento here
        memento = new ViewMemento(PMDUiConstants.MEMENTO_OVERVIEW_FILE);
        if (memento != null) {
            rememberFilterSettings();
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
        treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        treeViewer.setUseHashlookup(true);
        treeViewer.getTree().setHeaderVisible(true);
        treeViewer.getTree().setLinesVisible(true);

        // set Content- and LabelProvider as well as Filters
        treeViewer.setContentProvider(contentProvider);
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.addFilter(priorityFilter);
        treeViewer.addFilter(projectFilter);
        treeViewer.addTreeListener(this);

        // create the necessary stuff
        menuManager.setupActions();
        createColumns(treeViewer.getTree());
        menuManager.createActionBars(getViewSite().getActionBars().getToolBarManager());
        menuManager.createDropDownMenu(getViewSite().getActionBars().getMenuManager());
        menuManager.createContextMenu();

        // put in the Input
        // and add Listeners
        treeViewer.setInput(root);
        treeViewer.addDoubleClickListener(doubleClickListener);
        getSite().setSelectionProvider(this);

        // load the State from a Memento into the View if there is one
        if (memento != null) {
            rememberTreeSettings();
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        memento.putList(PRIORITY_LIST, priorityFilter.getPriorityFilterList());

        // on Dispose of the View we save its State into a Memento

        // we save the filtered Projects
        List<AbstractPMDRecord> projects = projectFilter.getProjectFilterList();
        List<String> projectNames = new ArrayList<String>();
        for (int k = 0; k < projects.size(); k++) {
            AbstractPMDRecord project = projects.get(k);
            projectNames.add(project.getName());
        }
        memento.putList(PROJECT_LIST, projectNames);

        // ... the Columns Widths
        List<Integer> widthList = Arrays.asList(columnWidths);
        memento.putList(COLUMN_WIDTHS, widthList);

        // ... what Element is sorted in what way
        Integer[] sorterProps = new Integer[] { 
        		Integer.valueOf(currentSortedColumn),
                Integer.valueOf(columnSortOrder[currentSortedColumn]) 
                };
        List<Integer> sorterList = Arrays.asList(sorterProps);
        memento.putList(COLUMN_SORTER, sorterList);

        // ... and how we should display the Elements
        memento.putInteger(PACKAGE_SWITCH, getShowType());

        memento.save(PMDUiConstants.MEMENTO_OVERVIEW_FILE);

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
        final TreeColumn plusColumn = new TreeColumn(tree, SWT.LEFT);
        plusColumn.setText(getString(StringKeys.VIEW_OVERVIEW_COLUMN_ELEMENT));
        plusColumn.setWidth(260);

        // Number of Violations
        final TreeColumn vioTotalColumn = new TreeColumn(tree, SWT.RIGHT);
        vioTotalColumn.setText(getString(StringKeys.VIEW_OVERVIEW_COLUMN_VIO_TOTAL));
        vioTotalColumn.setWidth(100);

        // Violations / 1K lines of code (KLOC)
        final TreeColumn vioLocColumn = new TreeColumn(tree, SWT.RIGHT);
        vioLocColumn.setText(getString(StringKeys.VIEW_OVERVIEW_COLUMN_VIO_KLOC));
        vioLocColumn.setWidth(100);

        // Violations / Method
        final TreeColumn vioMethodColumn = new TreeColumn(tree, SWT.RIGHT);
        vioMethodColumn.setText(getString(StringKeys.VIEW_OVERVIEW_COLUMN_VIO_METHOD));
        vioMethodColumn.setWidth(100);

        // Projects Name
        final TreeColumn projectColumn = new TreeColumn(tree, SWT.CENTER);
        projectColumn.setText(getString(StringKeys.VIEW_OVERVIEW_COLUMN_PROJECT));
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
        TreeColumn[] columns = tree.getColumns();

        for (int k = 0; k < columns.length; k++) {
            columnWidths[k] = Integer.valueOf(columns[k].getWidth()); // NOPMD by Herlin on 09/10/06 15:02

            // each Column gets a SelectionAdapter on Selection the Column is sorted
            columns[k].addSelectionListener(new ColumnSelectionAdapter(k));

            // the ResizeListener saves the current Width for storing it easily into a Memento later
            columns[k].addControlListener(new ColumnControlAdapter(k));
        }
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
        case 1:
            viewerSorter = newViolationsCountSorter(column, sortOrder);
            break;

        // sorts by Violations per LOC
        case 2:
            viewerSorter = newViolationsPerLOCSorter(column, sortOrder);
            break;

        // sorts by Violations per Number of Methods
        case 3:
            viewerSorter = newViolationsPerMethodsCount(column, sortOrder);
            break;

        // sorts by Name of Project
        case 4:
            viewerSorter = newProjectNameSorter(column, sortOrder);
            break;

        // sorts the Packages and Files by Name
        case 0:
        default:
            viewerSorter = newPackagesSorter(column, sortOrder);
            break;
        }

        return viewerSorter;
    }

    /**
     * Gets the Violations that are filtered, meaning, if e.g. the Priorities 4
     * and 5 are filtered, this Function returns the Number of all Priority 1,2
     * and 3-Markers
     *
     * @param element
     * @return the Number of visible Violations for the given Element
     */
    public int getNumberOfFilteredViolations(AbstractPMDRecord record) {
        int number = 0;

        List<Integer> filterList = priorityFilter.getPriorityFilterList();
        for (int i = 0; i < filterList.size(); i++) {
            Integer priority = filterList.get(i);
            number += record.getNumberOfViolationsToPriority(
                    priority.intValue(), getShowType() == SHOW_MARKERS_FILES);
        }
        return number;
    }

    /**
     * Sets the show type of packages/files and markers.
     * @param type
     * @see #SHOW_FILES_MARKERS
     * @see #SHOW_MARKERS_FILES
     * @see #SHOW_PACKAGES_FILES_MARKERS
     */
    public void setShowType(int type) {
        showType = type;
    }

    /**
     * @return show type
     */
    public int getShowType() {
        return showType;
    }

    /**
     * Delegate method for {@link ProjectFilter#getProjectFilterList()}.
     * @return project filter list
     */
    public List<AbstractPMDRecord> getProjectFilterList() {
        return projectFilter.getProjectFilterList();
    }

    /**
     * Delegate method for {@link ProjectFilter#getProjectFilterList()}.
     * @return project filter list
     */
    public List<Integer> getPriorityFilterList() {
        return priorityFilter.getPriorityFilterList();
    }

    /**
     * Sets the Widths of the Columns
     */
    public void setColumnWidths() {
        if (!treeViewer.getTree().isDisposed()) {
            TreeColumn[] columns = treeViewer.getTree().getColumns();
            for (int k = 0; k < columnWidths.length; k++) {
                if (columnWidths[k] == null) {
                    columnWidths[k] = Integer.valueOf(75);
                }
                columns[k].setWidth(columnWidths[k].intValue());
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
        if (properties.length > 0) {
            currentSortedColumn = properties[0].intValue();
            columnSortOrder[currentSortedColumn] = properties[1].intValue();
            treeViewer.setSorter(getViewerSorter(currentSortedColumn));
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        treeViewer.getTree().setFocus();
    }

    /**
     * @return the viewer
     */
    public TreeViewer getViewer() {
        return treeViewer;
    }

    public AbstractPMDRecord[] getAllProjects() {
        AbstractPMDRecord[] projects = AbstractPMDRecord.EMPTY_RECORDS;
        if (root != null) {
            projects = root.getChildren();
        }
        return projects;
    }

    /**
     * Refresh the View (and its Elements)
     */
    public void refresh() {
        if (!this.treeViewer.getControl().isDisposed()) {
            //this.treeViewer.getControl().setRedraw(false);
            treeViewer.refresh();
            refreshMenu();
            //this.treeViewer.getControl().setRedraw(true);
        }
    }

    public void refreshMenu() {
        menuManager.createDropDownMenu(getViewSite().getActionBars().getMenuManager());
        menuManager.createContextMenu();
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        treeViewer.addSelectionChangedListener(listener);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    public ISelection getSelection() {
        return treeViewer.getSelection();
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        treeViewer.removeSelectionChangedListener(listener);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection) {
        treeViewer.setSelection(selection);
    }

    /**
     * Helper method to return an NLS string from its key
     */
    private String getString(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }

    /**
     * Apply the memento for filters. Before calling this private method, one
     * must be sure memento is not null.
     *
     */
    private void rememberFilterSettings() {

        // Provide the Filters with their last State
        List<Integer> priorityList = memento.getIntegerList(PRIORITY_LIST);
        if (!priorityList.isEmpty()) {
            priorityFilter.setPriorityFilterList(priorityList);
        }

        List<String> projectNames = memento.getStringList(PROJECT_LIST);
        if (!projectNames.isEmpty()) {
            List<AbstractPMDRecord> projectList = new ArrayList<AbstractPMDRecord>();
            for (int k = 0; k < projectNames.size(); k++) {
                AbstractPMDRecord project = root.findResourceByName(projectNames.get(k).toString(),
                        AbstractPMDRecord.TYPE_PROJECT);
                if (project != null) {
                    projectList.add(project);
                }
            }

            projectFilter.setProjectFilterList(projectList);
        }

        Integer type = memento.getInteger(PACKAGE_SWITCH);
        if (type != null) {
            setShowType(type.intValue());
        }
    }

    /**
     * Apply the memento for tree options. Before calling this private method,
     * one must be sure memento is not null.
     *
     */
    private void rememberTreeSettings() {

        // the Memento sets the Widths of Columns
        List<Integer> widthList = memento.getIntegerList(COLUMN_WIDTHS);
        if (!widthList.isEmpty()) {
            widthList.toArray(this.columnWidths);
            setColumnWidths();
        }

        // ... and also the Sorter
        List<Integer> sorterList = memento.getIntegerList(COLUMN_SORTER);
        if (!sorterList.isEmpty()) {
            Integer[] sorterProps = new Integer[sorterList.size()];
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
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                String name1 = "";
                String name2 = "";

                if (e1 instanceof PackageRecord && e2 instanceof PackageRecord) {
                    name1 = ((PackageRecord) e1).getName();
                    name2 = ((PackageRecord) e2).getName();
                } else if (e1 instanceof FileRecord && e2 instanceof FileRecord) {
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
    private ViewerSorter newViolationsCountSorter(TreeColumn column, final int sortOrder) { // NOPMD by Sven on 13.11.06 11:45
        return new TableColumnSorter(column, sortOrder) {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                int vio1 = 0;
                int vio2 = 0;
                if (e1 instanceof PackageRecord && e2 instanceof PackageRecord
                        || e1 instanceof FileRecord && e2 instanceof FileRecord
                        || e1 instanceof MarkerRecord && e2 instanceof MarkerRecord
                        || e1 instanceof FileToMarkerRecord && e2 instanceof FileToMarkerRecord){
                    vio1 = getNumberOfFilteredViolations((AbstractPMDRecord)e1);
                    vio2 = getNumberOfFilteredViolations((AbstractPMDRecord)e2);
                }
                return Integer.valueOf(vio1).compareTo(Integer.valueOf(vio2)) * sortOrder;
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
    private ViewerSorter newViolationsPerLOCSorter(TreeColumn column, final int sortOrder) { // NOPMD by Sven on 13.11.06 11:45

        return new TableColumnSorter(column, sortOrder) {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) { // NOPMD by Sven on 13.11.06 11:45
                Float vioPerLoc1 = NumericConstants.FLOAT_ZERO;
                Float vioPerLoc2 = NumericConstants.FLOAT_ZERO;
                if (e1 instanceof PackageRecord && e2 instanceof PackageRecord
                        || e1 instanceof FileRecord && e2 instanceof FileRecord
                        || e1 instanceof MarkerRecord && e2 instanceof MarkerRecord
                        || e1 instanceof FileToMarkerRecord && e2 instanceof FileToMarkerRecord){
                    final int vio1 = getNumberOfFilteredViolations((AbstractPMDRecord)e1);
                    final int vio2 = getNumberOfFilteredViolations((AbstractPMDRecord)e2);
                    final int loc1 = getLOC((AbstractPMDRecord)e1);
                    final int loc2 = getLOC((AbstractPMDRecord)e2);
                    if (loc1 > 0) {
                        vioPerLoc1 = Float.valueOf((float) vio1 / loc1);
                    }
                    if (loc2 > 0) {
                        vioPerLoc2 = Float.valueOf((float) vio2 / loc2);
                    }
                }

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
    private ViewerSorter newViolationsPerMethodsCount(TreeColumn column, final int sortOrder) { // NOPMD by Sven on 13.11.06 11:45
        return new TableColumnSorter(column, sortOrder) {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) { // NOPMD by Sven on 13.11.06 11:45

                Float vioPerMethod1 = NumericConstants.FLOAT_ZERO;
                Float vioPerMethod2 = NumericConstants.FLOAT_ZERO;
                if (e1 instanceof PackageRecord && e2 instanceof PackageRecord
                        || e1 instanceof FileRecord && e2 instanceof FileRecord
                        || e1 instanceof MarkerRecord && e2 instanceof MarkerRecord
                        || e1 instanceof FileToMarkerRecord && e2 instanceof FileToMarkerRecord){
                    final int vio1 = getNumberOfFilteredViolations((AbstractPMDRecord)e1);
                    final int vio2 = getNumberOfFilteredViolations((AbstractPMDRecord)e2);
                    final int numMethods1 = getNumberOfMethods((AbstractPMDRecord) e1);
                    final int numMethods2 = getNumberOfMethods((AbstractPMDRecord) e2);
                    if (numMethods1 > 0) {
                        vioPerMethod1 = Float.valueOf((float) vio1 / numMethods1);
                    }
                    if (numMethods2 > 0) {
                        vioPerMethod2 = Float.valueOf((float) vio2 / numMethods2);
                    }
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
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                AbstractPMDRecord project1 = null;
                AbstractPMDRecord project2 = null;
                int result = 0;
                if (e1 instanceof PackageRecord && e2 instanceof PackageRecord) {
                    project1 = ((PackageRecord) e1).getParent();
                    project2 = ((PackageRecord) e2).getParent();
                    result = project1.getName().compareToIgnoreCase(project2.getName()) * sortOrder;
                }
                return result;
            }
        };
    }


    /**
     * @see org.eclipse.jface.viewers.ITreeViewerListener#treeCollapsed(org.eclipse.jface.viewers.TreeExpansionEvent)
     */
    public void treeCollapsed(TreeExpansionEvent event) {
        // do nothing
    }

    /**
     * Calculates the LOC of the expanded file record.
     *
     * @see org.eclipse.jface.viewers.ITreeViewerListener#treeExpanded(org.eclipse.jface.viewers.TreeExpansionEvent)
     */
    public void treeExpanded(TreeExpansionEvent event) {
        Object object = event.getElement();
        if (object instanceof PackageRecord) {
            PackageRecord record = (PackageRecord) object;
            AbstractPMDRecord[] children = record.getChildren();
            for (AbstractPMDRecord element : children) {
                if (element instanceof FileRecord) {
                    FileRecord fileRecord = (FileRecord) element;
                    fileRecord.calculateLinesOfCode();
                    fileRecord.calculateNumberOfMethods();
                }
            }
        }

        // refresh the labels in the table
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                getViewer().refresh();
            }
        });
    }

    /**
     * Deletes markers of an AbstractPMDRecord. This is performed after the action
     * Clear PMD Markers of the contextmenu was called.
     *
     * @param element
     * @throws CoreException
     */
    public void deleteMarkers(AbstractPMDRecord element) throws CoreException {
        if (element instanceof MarkerRecord) {
            MarkerRecord record = (MarkerRecord) element;
            IMarker[] markers = MarkerUtil.EMPTY_MARKERS;

            switch (getShowType()) {
            case SHOW_PACKAGES_FILES_MARKERS:
            case SHOW_FILES_MARKERS:
                // simply get the markers of the marker record
                markers = record.findMarkers();
                break;
            case SHOW_MARKERS_FILES:
                AbstractPMDRecord packRec = record.getParent().getParent();
                markers = packRec.findMarkersByAttribute(PMDUiConstants.KEY_MARKERATT_RULENAME, record.getName());
                break;
            default:
                // do nothing
            }
            deleteMarkers(markers);
        } else if (element instanceof FileToMarkerRecord) {
            FileToMarkerRecord record = (FileToMarkerRecord) element;
            IMarker[] markers = record.findMarkers();
            deleteMarkers(markers);
        } else if (element instanceof AbstractPMDRecord) {
            // simply delete markers from resource
            AbstractPMDRecord record = element;
            MarkerUtil.deleteAllMarkersIn(record.getResource());
        }
    }

    private void deleteMarkers(IMarker[] markers) {
        if (markers.length > 0) {
            DeleteMarkersCommand cmd = new DeleteMarkersCommand();
            cmd.setMarkers(markers);
            try {
                cmd.performExecute();
            } catch (CommandException e) {
                PMDPlugin.getDefault().showError(getString(StringKeys.ERROR_CORE_EXCEPTION), e.getCause());
            }
        }
    }

    /**
     * Gets the correct lines of code depending on the presentation type.
     * @param element AbstractPMDRecord
     * @return lines of code
     */
    public int getLOC(AbstractPMDRecord element) {
        int loc = 0;
        if (element instanceof MarkerRecord && getShowType() == SHOW_MARKERS_FILES) {
            loc = element.getParent().getParent().getLOC();
        } else {
            loc = element.getLOC();
        }
        return loc;
    }

    /**
     * Gets the correct number of methods depending on the presentation type.
     * @param element AbstractPMDRecord
     * @return number of methods
     */
    public int getNumberOfMethods(AbstractPMDRecord element) {
        int numberOfMethods = 0;
        if (element instanceof MarkerRecord && getShowType() == SHOW_MARKERS_FILES) {
            numberOfMethods = element.getParent().getParent().getNumberOfMethods();
        } else {
            numberOfMethods = element.getNumberOfMethods();
        }
        return numberOfMethods;
    }


    /**
     * Private Selection Adapter to handle column resizing.
     */
    private class ColumnSelectionAdapter extends SelectionAdapter {
        private final int column;

        public ColumnSelectionAdapter(int column) {
            super();
            this.column = column;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            columnSortOrder[this.column] *= -1;
            treeViewer.setSorter(getViewerSorter(column));
        }
    }

    /**
     * Private Control Adapter to handle column width changes.
     */
    private class ColumnControlAdapter extends ControlAdapter {
        private final int column;

        public ColumnControlAdapter(int column) {
            super();
            this.column = column;
        }

        @Override
        public void controlResized(ControlEvent e) {
            columnWidths[column] = Integer.valueOf(treeViewer.getTree().getColumn(column).getWidth());
        }
    }


}
