package net.sourceforge.pmd.ui.views;

import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.model.FileRecord;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

/**
 * A View that shows DataflowGraph and -Table as well as the Anomaly-List
 * 
 * @author SebastianRaffel ( 26.05.2005 ), Sven Jacob ( 19.09.2006 )
 */
public class DataflowView extends PageBookView implements IResourceChangeListener {
    
    /* @see org.eclipse.ui.part.PageBookView#createPartControl(org.eclipse.ui.part.PageBook) */
    public void createPartControl(Composite parent) {      
        super.createPartControl(parent);
    }
   
    /* @see org.eclipse.ui.part.PageBookView#createDefaultPage(org.eclipse.ui.part.PageBook) */
    protected IPage createDefaultPage(PageBook book) {
        // builds a message page showing a text
        MessagePage page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage(PMDUiPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_DATAFLOW_DEFAULT_TEXT));
        return page;
    }

    /* @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart) */
    protected PageRec doCreatePage(IWorkbenchPart part) {
        FileRecord resourceRecord = getFileRecordFromWorkbenchPart(part);
        if (resourceRecord != null) {
            resourceRecord.getResource().getWorkspace().addResourceChangeListener(
                    this, IResourceChangeEvent.POST_CHANGE);
            
            // creates a new DataflowViewPage, when a Resource exists
            DataflowViewPage page = new DataflowViewPage(part, resourceRecord);
            initPage(page);
            page.createControl(getPageBook());
            
            return new PageRec(part, page);
        }
        return null;
    }

    /* @see org.eclipse.ui.part.PageBookView#doDestroyPage(org.eclipse.ui.IWorkbenchPart, org.eclipse.ui.part.PageBookView.PageRec) */
    protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {      
        DataflowViewPage page = (DataflowViewPage) pageRecord.page;
        
        if (page != null) {
            page.dispose();
        }

        pageRecord.dispose();
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
        IWorkbenchPart activePart = getSite().getPage().getActivePart();
        if (activePart == null)
            getSite().getPage().activate(this);
        super.partActivated(part);
    }

    /* @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IPartListener) */
    public void partBroughtToTop(IWorkbenchPart part) {
        partActivated(part);
    }
    
    /* @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent) */
    public void resourceChanged(IResourceChangeEvent event) {        
        try {
            event.getDelta().accept(new IResourceDeltaVisitor() {
                public boolean visit(final IResourceDelta delta) throws CoreException {
                    // find the resource for the path of the current page
                    IPath path = getCurrentDataflowViewPage().getFileRecord().getResource().getFullPath();
                    if(delta.getFullPath().equals(path)) {
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
            PMDUiPlugin.getDefault().logError(
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
     * Gets the fileRecord from the currently active editor.
     * @param part IWorkbenchPart
     * @return a new FileRecord
     */
    private FileRecord getFileRecordFromWorkbenchPart(IWorkbenchPart part) {
        if (part instanceof IEditorPart) {
            // If there is a file opened in the editor, we create a record for it
            IEditorInput input = ((IEditorPart) part).getEditorInput();
            if ((input != null) && (input instanceof IFileEditorInput)) {
                IResource res = ((IFileEditorInput) input).getFile();
                if (res.getFileExtension().equalsIgnoreCase("java"))
                    return new FileRecord(res);
                else
                    return null;
            }
        } else {
            // We also want to get the editors when it's not active
            // so we pretend, that the editor has been activated
            IEditorPart editorPart = getSite().getPage().getActiveEditor();
            if (editorPart != null) {
                return getFileRecordFromWorkbenchPart((IWorkbenchPart) editorPart);
            }
        }
        return null;
    }
  
    /**
     * Refreshs, reloads the View with a given new resource.
     * @param newResource new resource for the current active page.
     */
    private void refresh(IResource newResource) {
        DataflowViewPage page = getCurrentDataflowViewPage();
        if (page != null)
            page.refresh(newResource);
    }
}
