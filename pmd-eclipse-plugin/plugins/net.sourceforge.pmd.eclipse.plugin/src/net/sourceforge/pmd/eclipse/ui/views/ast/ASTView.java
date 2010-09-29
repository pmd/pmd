package net.sourceforge.pmd.eclipse.ui.views.ast;

import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.views.AbstractResourceView;
import net.sourceforge.pmd.eclipse.ui.views.AbstractStructureInspectorPage;
import net.sourceforge.pmd.eclipse.ui.views.actions.CollapseAllAction;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;

/**
 * 
 * @author Brian Remedios
 */
public class ASTView extends AbstractResourceView {

	private ASTViewPage page;
	
	public ASTView() {
	}

    protected String pageMessageId() { return StringKeys.VIEW_AST_DEFAULT_TEXT; }

	@Override
	protected String mementoFileId() { return PMDUiConstants.MEMENTO_AST_FILE; }

	@Override
    protected PageRec doCreatePage(IWorkbenchPart part) {
		
        FileRecord resourceRecord = getFileRecordFromWorkbenchPart(part);
        if (resourceRecord != null) {
        	setupListener(resourceRecord);

            // creates a new ASTViewPage, when a Resource exists
            page = new ASTViewPage(part, resourceRecord);
            initPage(page);
            page.createControl(getPageBook());
    		addToolbarControls();
    		
            return new PageRec(part, page);
        }
        return null;
    }

    protected AbstractStructureInspectorPage getCurrentViewPage() {
    	return getCurrentASTViewPage();
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


    private void addToolbarControls() {
        IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
        
        Action collapseAllAction = new CollapseAllAction(page.astViewer());
        manager.add(collapseAllAction);
    }
}
