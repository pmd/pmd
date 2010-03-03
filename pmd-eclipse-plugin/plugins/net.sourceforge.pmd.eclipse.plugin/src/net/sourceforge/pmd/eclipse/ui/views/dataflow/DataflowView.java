package net.sourceforge.pmd.eclipse.ui.views.dataflow;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.views.AbstractPMDPagebookView;
import net.sourceforge.pmd.eclipse.ui.views.DataflowResizeAction;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;

/**
 * A View that shows DataflowGraph and -Table as well as the Anomaly-List
 *
 * @author SebastianRaffel ( 26.05.2005 ), Sven Jacob ( 19.09.2006 )
 */
public class DataflowView extends AbstractPMDPagebookView implements IResourceChangeListener {

    /* @see org.eclipse.ui.part.PageBookView#createPartControl(org.eclipse.ui.part.PageBook) */

    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
    }
    
    protected String pageMessageId() { return StringKeys.MSGKEY_VIEW_DATAFLOW_DEFAULT_TEXT; }

	@Override
	protected String mementoFileId() { return PMDUiConstants.MEMENTO_DATAFLOW_FILE; }
	
    /* @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart) */
    @Override
    protected PageRec doCreatePage(IWorkbenchPart part) {
        FileRecord resourceRecord = getFileRecordFromWorkbenchPart(part);
        if (resourceRecord != null) {
            resourceRecord.getResource().getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);

            // creates a new DataflowViewPage, when a Resource exists
            DataflowViewPage page = new DataflowViewPage(part, resourceRecord);
            initPage(page);
            page.createControl(getPageBook());

            return new PageRec(part, page);
        }
        return null;
    }
    
    /* @see org.eclipse.ui.part.PageBookView#doDestroyPage(org.eclipse.ui.IWorkbenchPart, org.eclipse.ui.part.PageBookView.PageRec) */
    @Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {

		FileRecord resourceRecord = getFileRecordFromWorkbenchPart(part);
		if (resourceRecord != null) {
			resourceRecord.getResource().getWorkspace().removeResourceChangeListener(this);
		}

		DataflowViewPage page = (DataflowViewPage) pageRecord.page;

		if (page != null) {
			page.dispose();
		}

		pageRecord.dispose();
	}

    /* @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IPartListener) */
    @Override
    public void partActivated(IWorkbenchPart part) {
        IWorkbenchPart activePart = getSitePage().getActivePart();
        if (activePart == null) getSitePage().activate(this);
        super.partActivated(part);
    }

    private IPath getResourcePath() {
    	DataflowViewPage page = getCurrentDataflowViewPage();
    	FileRecord record = page.getFileRecord();
    	IResource resource = record.getResource();
    	return resource.getFullPath();
    }
    
    /* @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent) */
    public void resourceChanged(IResourceChangeEvent event) {
        try {
            event.getDelta().accept(new IResourceDeltaVisitor() {
                public boolean visit(final IResourceDelta delta) throws CoreException {
                    // find the resource for the path of the current page
                    IPath path = getResourcePath();
                    if (delta.getFullPath().equals(path)) {
                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {
                                refresh(delta.getResource());
                            }
                        });

                        return false;
                    }
                    return true;
                }

            });
        } catch (CoreException e) {
            PMDPlugin.getDefault().logError(
                    StringKeys.MSGKEY_ERROR_CORE_EXCEPTION, e);
        }

    }

    /**
     * @return the currently displayed Page
     */
    private DataflowViewPage getCurrentDataflowViewPage() {
        IPage page = super.getCurrentPage();
        if (!(page instanceof DataflowViewPage))
            return null;

        return (DataflowViewPage) page;
    }
    
    /**
     * Gets the fileRecord from the currently active editor (if it is one).
     * @param part IWorkbenchPart
     * @return a new FileRecord
     */
    public FileRecord getFileRecordFromWorkbenchPart(IWorkbenchPart part) {
    	
    	FileRecord record = tryForFileRecordFrom(part);
    	if (record != null) return record; 	

        // We also want to get the editors when it's not active
        // so we pretend, that the editor has been activated
        IEditorPart editorPart = getSite().getPage().getActiveEditor();
        return editorPart == null ? null : getFileRecordFromWorkbenchPart(editorPart);
    }

    /**
     * Refresh, reloads the View with a given new resource.
     * @param newResource new resource for the current active page.
     */
    private void refresh(IResource newResource) {
        DataflowViewPage page = getCurrentDataflowViewPage();
        if (page != null)
            page.refresh(newResource);
    }

}
