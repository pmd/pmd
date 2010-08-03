package net.sourceforge.pmd.eclipse.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.plugin.UISettings;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.views.actions.DisableRuleAction;
import net.sourceforge.pmd.eclipse.ui.views.actions.PriorityFilterAction;
import net.sourceforge.pmd.eclipse.ui.views.actions.QuickFixAction;
import net.sourceforge.pmd.eclipse.ui.views.actions.RemoveViolationAction;
import net.sourceforge.pmd.eclipse.ui.views.actions.ReviewAction;
import net.sourceforge.pmd.eclipse.ui.views.actions.ShowRuleAction;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;

/**
 * A View to show a List of PMD-Violations for a File
 *
 * @author SebastianRaffel ( 08.05.2005 )
 */
public class ViolationOutline extends AbstractPMDPagebookView implements ISelectionProvider {

    private FileRecord resourceRecord;
    private PriorityFilter priorityFilter;

    protected final static String PRIORITY_LIST = "priorityFilterList";
    protected final static String COLUMN_WIDTHS = "tableColumnWidths";
    protected final static String COLUMN_SORTER = "tableColumnSorter";

    /* @see org.eclipse.ui.part.PageBookView#createPartControl(org.eclipse.ui.part.PageBook) */
    @Override
    public void createPartControl(Composite parent) {
    	addFilterControls();
        super.createPartControl(parent);
        getSite().setSelectionProvider(this);
    }

    protected String pageMessageId() { return StringKeys.MSGKEY_VIEW_OUTLINE_DEFAULT_TEXT; }
    
    protected String mementoFileId() { return PMDUiConstants.MEMENTO_OUTLINE_FILE; }
    
    /* @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite) */
    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);

        priorityFilter = new PriorityFilter();
        
        List<Integer> priorityList = getIntegerList(PRIORITY_LIST);
        if (!priorityList.isEmpty()) {
                // set the loaded List for the Priority Filter
            priorityFilter.setPriorityFilterList(priorityList);
            }
    }

    @Override
    public void dispose() {
        save(PRIORITY_LIST, priorityFilter.getPriorityFilterList());
       
        super.dispose();
    }

    /* @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart) */
    @Override
    protected PageRec doCreatePage(IWorkbenchPart part) {
        if (resourceRecord != null) {
            // creates a new ViolationOutlinePage, when a Resource exists
            ViolationOutlinePage page = new ViolationOutlinePage(resourceRecord, this);
            if (page instanceof IPageBookViewPage)  initPage(page);
            page.createControl(getPageBook());
            return new PageRec(part, page);
        }
        return null;
    }

    /* @see org.eclipse.ui.part.PageBookView#doDestroyPage(org.eclipse.ui.IWorkbenchPart, org.eclipse.ui.part.PageBookView.PageRec) */
    @Override
    protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
    	
        ViolationOutlinePage page = (ViolationOutlinePage) pageRecord.page;

        // get the State of the destroyed Page for loading it into the
        // next Page -> different Pages look like one
        if (page != null) {
            storeColumnData(page);

            memento.save(PMDUiConstants.ID_OUTLINE);
            page.dispose();
        }

        pageRecord.dispose();
    }

    /**
     * Creates a DropDownMenu for the view
     */
    private void addFilterControls() {
        IMenuManager manager = getViewSite().getActionBars().getMenuManager();
        List<Integer> filterList = priorityFilter.getPriorityFilterList();

        // we add the PriorityFilter-Actions to this Menu
        RulePriority[] priorities = UISettings.currentPriorities(true);
        for (RulePriority  priority : priorities) {
            Action filterAction = new PriorityFilterAction(priority, this);
            if (filterList.contains(priority.getPriority()))
                filterAction.setChecked(true);

            manager.add(filterAction);
        }
    }
    
    /**
     * Creates a Context Menu for the View
     * 
     * @param viewer
     */
    public void createContextMenu(final TableViewer viewer) {
        MenuManager manager = new MenuManager();
        manager.setRemoveAllWhenShown(true);
        // here we add the Context Menus Actions
        manager.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {            	
                buildMenu(manager, viewer);
            }
        });

        Table table = viewer.getTable();
        table.setMenu(manager.createContextMenu(table));
        getSite().registerContextMenu(manager, viewer);
    }

	private void buildMenu(IMenuManager manager, TableViewer viewer) {
		// show the Rule Dialog
        Action showRuleAction = new ShowRuleAction(viewer, getSite().getShell());
        manager.add(showRuleAction);

        // add Review Comment
        ReviewAction reviewAction = new ReviewAction(viewer);
        manager.add(reviewAction);

        // Remove Violation
        RemoveViolationAction removeAction = new RemoveViolationAction(viewer);
        manager.add(removeAction);

        // Disable rule
        DisableRuleAction disableAction = new DisableRuleAction(viewer);
        disableAction.setEnabled(disableAction.hasActiveRules());
        manager.add(disableAction);
        
        // Quick Fix (where possible)
        QuickFixAction quickFixAction = new QuickFixAction(viewer);
        quickFixAction.setEnabled(quickFixAction.hasQuickFix());
        manager.add(quickFixAction);

        // additions Action: Clear reviews
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end"));
	}
	
    /* @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IPartListener) */
    @Override
    public void partActivated(IWorkbenchPart part) {
        // We only care about the editor
        if (part instanceof IEditorPart) {
        	resourceRecord = tryForFileRecordFrom(part);	// If there is a file opened in the editor, we create a record for it
           
        } else {
            // We also want to get the editors when it's not active
            // so we pretend, that the editor has been activated
            IEditorPart editorPart = getSitePage().getActiveEditor();
            if (editorPart != null) {
                partActivated(editorPart);
            }
        }

        IWorkbenchPage page = getSitePage();
        IWorkbenchPart activePart = page.getActivePart();
        if (activePart == null) page.activate(this);
        
        super.partActivated(part);
        refresh();
    }

    /* @see org.eclipse.ui.part.PageBookView#showPageRec(org.eclipse.ui.part.PageBookView.PageRec) */
    @Override
    protected void showPageRec(PageRec pageRec) {
        ViolationOutlinePage oldPage = getCurrentOutlinePage();
        ViolationOutlinePage newPage = null;
        if (pageRec.page instanceof ViolationOutlinePage)
            newPage = (ViolationOutlinePage) pageRec.page;

        // here we change from one Page to another
        // so we get the State of the old Page, put it in a Memento
        // and load it into the new Page, so it looks like the old one
        if (oldPage != newPage) {
            if (oldPage != null) {
                storeColumnData(oldPage);
            }

            // we load the stuff into the new Page
            if (newPage != null) {
                List<Integer> widthList = memento.getIntegerList(COLUMN_WIDTHS);
                if (!widthList.isEmpty()) {
                    Integer[] widthArray = new Integer[3];
                    widthList.toArray(widthArray);
                    newPage.setColumnWidths(widthArray);
                }

                List<Integer> sorterList = memento.getIntegerList(COLUMN_SORTER);
                if (!sorterList.isEmpty()) {
                    Integer[] sorterProps = new Integer[sorterList.size()];
                    sorterList.toArray(sorterProps);
                    newPage.setSorterProperties(sorterProps);
                }
            }
        }

        super.showPageRec(pageRec);
    }

	private void storeColumnData(ViolationOutlinePage page) {
		
		// we care about the column widths
		Integer[] widthArray = page.getColumnWidths();
		List<Integer> widthList = new ArrayList<Integer>(Arrays.asList(widthArray));
		memento.putList(COLUMN_WIDTHS, widthList);

		// ... and what Element is sorted, and in which way
		Integer[] sorterProps = page.getSorterProperties();
		List<Integer> sorterList = new ArrayList<Integer>(Arrays.asList(sorterProps));
		memento.putList(COLUMN_SORTER, sorterList);
	}

    /**
     * @return the currently displayed Page
     */
    private ViolationOutlinePage getCurrentOutlinePage() {
        IPage page = super.getCurrentPage();
        if (!(page instanceof ViolationOutlinePage))
            return null;

        return (ViolationOutlinePage) page;
    }

    /**
     * @return a List of the current ViewerFilters
     */
    public ViewerFilter[] getFilters() {
        return new ViewerFilter[] { priorityFilter };
    }

    /**
     * Refreshes, reloads the View
     */
    public void refresh() {
        ViolationOutlinePage page = getCurrentOutlinePage();
        if (page != null)
            page.refresh();
    }

    /* @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener) */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        ViolationOutlinePage page = getCurrentOutlinePage();
        if (page != null)
            page.getTableViewer().addSelectionChangedListener(listener);
    }

    /* @see org.eclipse.jface.viewers.ISelectionProvider#getSelection() */
    public ISelection getSelection() {
        ViolationOutlinePage page = getCurrentOutlinePage();
        if (page != null)
            return page.getTableViewer().getSelection();
        return null;
    }

    /* @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener) */
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        ViolationOutlinePage page = getCurrentOutlinePage();
        if (page != null)
            page.getTableViewer().removeSelectionChangedListener(listener);
    }

    /* @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection) */
    public void setSelection(ISelection selection) {
        ViolationOutlinePage page = getCurrentOutlinePage();
        if (page != null)
            page.getTableViewer().setSelection(selection);
    }
}