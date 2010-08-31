package net.sourceforge.pmd.eclipse.ui.views.ast;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.views.AbstractPMDPagebookView;
import net.sourceforge.pmd.eclipse.ui.views.AbstractStructureInspectorPage;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;

/**
 * 
 * @author Brian Remedios
 */
public class ASTView extends AbstractPMDPagebookView implements IResourceChangeListener {

	public ASTView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		 super.createPartControl(parent);
    }

    protected String pageMessageId() { return StringKeys.VIEW_AST_DEFAULT_TEXT; }

	@Override
	protected String mementoFileId() { return PMDUiConstants.MEMENTO_AST_FILE; }

	@Override
    protected PageRec doCreatePage(IWorkbenchPart part) {
		
        FileRecord resourceRecord = getFileRecordFromWorkbenchPart(part);
        if (resourceRecord != null) {
            resourceRecord.getResource().getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);

            // creates a new DataflowViewPage, when a Resource exists
            ASTViewPage page = new ASTViewPage(part, resourceRecord);
            initPage(page);
            page.createControl(getPageBook());

            return new PageRec(part, page);
        }
        return null;
    }

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		
		FileRecord resourceRecord = getFileRecordFromWorkbenchPart(part);
		if (resourceRecord != null) {
			resourceRecord.getResource().getWorkspace().removeResourceChangeListener(this);
		}

		ASTViewPage page = (ASTViewPage) pageRecord.page;

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

    /**
     * @return the currently displayed Page
     */
    private ASTViewPage getCurrentASTViewPage() {
        IPage page = super.getCurrentPage();
        if (!(page instanceof ASTViewPage))
            return null;

        return (ASTViewPage) page;
    }
    
    private IPath getResourcePath() {
    	AbstractStructureInspectorPage page = getCurrentASTViewPage();
    	FileRecord record = page.getFileRecord();
    	IResource resource = record.getResource();
    	return resource.getFullPath();
    }
    
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
     * Refresh, reloads the View with a given new resource.
     * @param newResource new resource for the current active page.
     */
    private void refresh(IResource newResource) {
        ASTViewPage page = getCurrentASTViewPage();
        if (page != null)
            page.refresh(newResource);
    }
}
