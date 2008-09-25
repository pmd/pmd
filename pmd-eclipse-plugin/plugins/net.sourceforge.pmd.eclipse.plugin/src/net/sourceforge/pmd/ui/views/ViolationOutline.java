package net.sourceforge.pmd.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.model.FileRecord;
import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.ui.views.actions.PriorityFilterAction;
import net.sourceforge.pmd.ui.views.actions.QuickFixAction;
import net.sourceforge.pmd.ui.views.actions.RemoveViolationAction;
import net.sourceforge.pmd.ui.views.actions.ReviewAction;
import net.sourceforge.pmd.ui.views.actions.ShowRuleAction;

import org.eclipse.core.resources.IResource;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

/**
 * A View to show a List of PMD-Violations for a File
 * 
 * @author SebastianRaffel ( 08.05.2005 )
 */
public class ViolationOutline extends PageBookView implements ISelectionProvider {

    private FileRecord resourceRecord;
    private PriorityFilter priorityFilter;
    private ViewMemento memento;

    protected final static String PRIORITY_LIST = "priorityFilterList";
    protected final static String COLUMN_WIDTHS = "tableColumnWidths";
    protected final static String COLUMN_SORTER = "tableColumnSorter";

    /* @see org.eclipse.ui.part.PageBookView#createPartControl(org.eclipse.ui.part.PageBook) */
    public void createPartControl(Composite parent) {
        createDropDownMenu();
        super.createPartControl(parent);
        getSite().setSelectionProvider(this);
    }

    /* @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite) */
    public void init(IViewSite site) throws PartInitException {
        super.init(site);

        // load Memento from a File, if existing
        memento = new ViewMemento(PMDUiConstants.MEMENTO_OUTLINE_FILE);
        priorityFilter = new PriorityFilter();
        if (memento != null) {
            List priorityList = memento.getIntegerList(PRIORITY_LIST);
            if (!priorityList.isEmpty()) {
                // set the loaded List for the Priority Filter
                priorityFilter.setPriorityFilterList(priorityList);
            }
        }
    }

    public void dispose() {
        // save the current State in a Memento
        memento.putList(PRIORITY_LIST, priorityFilter.getPriorityFilterList());
        memento.save(PMDUiConstants.MEMENTO_OUTLINE_FILE);

        super.dispose();
    }

    /* @see org.eclipse.ui.part.PageBookView#createDefaultPage(org.eclipse.ui.part.PageBook) */
    protected IPage createDefaultPage(PageBook book) {
        // builds a message page showing a text
        MessagePage page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage(PMDUiPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_OUTLINE_DEFAULT_TEXT));
        return page;
    }

    /* @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart) */
    protected PageRec doCreatePage(IWorkbenchPart part) {
        if (resourceRecord != null) {
            // creates a new ViolationOutlinePage, when a Resource exists
            ViolationOutlinePage page = new ViolationOutlinePage(resourceRecord, this);
            if (page instanceof IPageBookViewPage)
                initPage((IPageBookViewPage) page);
            page.createControl(getPageBook());
            return new PageRec(part, page);
        }
        return null;
    }

    /* @see org.eclipse.ui.part.PageBookView#doDestroyPage(org.eclipse.ui.IWorkbenchPart, org.eclipse.ui.part.PageBookView.PageRec) */
    protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
        ViolationOutlinePage page = (ViolationOutlinePage) pageRecord.page;

        // get the State of the destroyed Page for laoding it into the
        // next Page -> different Pages look like one
        if (page != null) {
            Integer[] widthArray = page.getColumnWidths();
            ArrayList widthList = new ArrayList(Arrays.asList(widthArray));
            memento.putList(COLUMN_WIDTHS, widthList);

            Integer[] sorterProps = page.getSorterProperties();
            ArrayList sorterList = new ArrayList(Arrays.asList(sorterProps));
            memento.putList(COLUMN_SORTER, sorterList);

            memento.save(PMDUiConstants.ID_OUTLINE);
            page.dispose();
        }

        pageRecord.dispose();
    }

    /**
     * Creates a DropDownMenu for the view
     */
    private void createDropDownMenu() {
        IMenuManager manager = getViewSite().getActionBars().getMenuManager();
        List filterList = priorityFilter.getPriorityFilterList();

        // we add the PriorityFilter-Actions to this Menu
        Integer[] priorities = PMDUiPlugin.getDefault().getPriorityValues();
        for (int k = 0; k < priorities.length; k++) {
            Action filterAction = new PriorityFilterAction(priorities[k], this);
            if (filterList.contains(priorities[k]))
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
                // show the Rule Dialog
                Action showRuleAction = new ShowRuleAction(viewer, getSite().getShell());
                manager.add(showRuleAction);

                // add Review Comment
                ReviewAction reviewAction = new ReviewAction(viewer);
                manager.add(reviewAction);

                // Remove Violation
                RemoveViolationAction removeAction = new RemoveViolationAction(viewer);
                manager.add(removeAction);

                // Wuick Fix (where possible)
                QuickFixAction quickFixAction = new QuickFixAction(viewer);
                quickFixAction.setEnabled(quickFixAction.hasQuickFix());
                manager.add(quickFixAction);

                // addtions Action: Clear reviews
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end"));
            }
        });

        Table table = viewer.getTable();
        table.setMenu(manager.createContextMenu(table));
        getSite().registerContextMenu(manager, viewer);
    }

    /* @see org.eclipse.ui.part.PageBookView#getBootstrapPart() */
    protected IWorkbenchPart getBootstrapPart() {
        IWorkbenchPage page = getSite().getPage();
        if (page != null)
            return page.getActiveEditor();
        else
            return null;
    }

    /* @see org.eclipse.ui.part.PageBookView#isImportant(org.eclipse.ui.IWorkbenchPart) */
    protected boolean isImportant(IWorkbenchPart part) {
        // We only care about the editor
        return (part instanceof IEditorPart);
    }

    /* @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IPartListener) */
    public void partActivated(IWorkbenchPart part) {
        // We only care about the editor
        if (part instanceof IEditorPart) {
            // If there is a file opened in the editor, we create a record for it
            IEditorInput input = ((IEditorPart) part).getEditorInput();
            if ((input != null) && (input instanceof IFileEditorInput)) {
                IResource res = ((IFileEditorInput) input).getFile();
                if (res.getFileExtension().equalsIgnoreCase("java"))
                    resourceRecord = new FileRecord(res);
                else
                    resourceRecord = null;
            }
        } else {
            // We also want to get the editors when it's not active
            // so we pretend, that the editor has been activated
            IEditorPart editorPart = getSite().getPage().getActiveEditor();
            if (editorPart != null) {
                partActivated((IWorkbenchPart) editorPart);
            }
        }

        IWorkbenchPart activePart = getSite().getPage().getActivePart();
        if (activePart == null)
            getSite().getPage().activate(this);
        super.partActivated(part);
        refresh();
    }

    /* @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IPartListener) */
    public void partBroughtToTop(IWorkbenchPart part) {
        partActivated(part);
    }

    /* @see org.eclipse.ui.part.PageBookView#showPageRec(org.eclipse.ui.part.PageBookView.PageRec) */
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
                // we care about the Widths of the Columns
                Integer[] widthArray = oldPage.getColumnWidths();
                ArrayList widthList = new ArrayList(Arrays.asList(widthArray));
                memento.putList(COLUMN_WIDTHS, widthList);

                // ... and what Element is sorted, and in which way
                Integer[] sorterProps = oldPage.getSorterProperties();
                ArrayList sorterList = new ArrayList(Arrays.asList(sorterProps));
                memento.putList(COLUMN_SORTER, sorterList);
            }

            // we load the stuff into the new Page
            if (newPage != null) {
                List widthList = memento.getIntegerList(COLUMN_WIDTHS);
                if (!widthList.isEmpty()) {
                    Integer[] widthArray = new Integer[3];
                    widthList.toArray(widthArray);
                    newPage.setColumnWidths(widthArray);
                }

                List sorterList = memento.getIntegerList(COLUMN_SORTER);
                if (!sorterList.isEmpty()) {
                    Integer[] sorterProps = new Integer[sorterList.size()];
                    sorterList.toArray(sorterProps);
                    newPage.setSorterProperties(sorterProps);
                }
            }
        }

        super.showPageRec(pageRec);
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