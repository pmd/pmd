package net.sourceforge.pmd.eclipse.ui.views;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.cmd.AbstractDefaultCommand;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractPMDPagebookView extends PageBookView {

    protected ViewMemento memento;
	
    public static FileRecord tryForFileRecordFrom(IWorkbenchPart part) {
    	
    	if (part instanceof IEditorPart) {
            // If there is a file opened in the editor, we create a record for it
            IEditorInput input = ((IEditorPart) part).getEditorInput();
            if (input != null && input instanceof IFileEditorInput) {
                IFile file = ((IFileEditorInput) input).getFile();
                return AbstractDefaultCommand.isJavaFile(file) ? new FileRecord(file) : null; 
            	}
    		}
       return null;
    }
    
	protected AbstractPMDPagebookView() {
	}

    protected abstract String pageMessageId();
    
    protected abstract String mementoFileId();
    
    protected boolean hasMemento() {
    	return memento != null;
    }
    
    protected IWorkbenchPage getSitePage() {
    	return getSite().getPage();
    }
    
    /* @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite) */
    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);

        memento = new ViewMemento(mementoFileId());		// load Memento from a File, if existing
    }
	
    protected void save(String mementoId, List<Integer> integerList) {
    	  memento.putList(mementoId, integerList);
    }
    
    protected List<Integer> getIntegerList(String mementoId) {
    	return memento == null ? Collections.EMPTY_LIST : memento.getIntegerList(mementoId);
    }
    
    /* @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IPartListener) */
    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
        partActivated(part);
    }
    
    /* @see org.eclipse.ui.part.PageBookView#getBootstrapPart() */
    @Override
    protected IWorkbenchPart getBootstrapPart() {
        IWorkbenchPage page = getSite().getPage();
        return page == null ? null : page.getActiveEditor();
    }
    
    /* @see org.eclipse.ui.part.PageBookView#isImportant(org.eclipse.ui.IWorkbenchPart) */
    @Override
    protected boolean isImportant(IWorkbenchPart part) {
        // We only care about the editor
        return part instanceof IEditorPart;
    }
    
    /* @see org.eclipse.ui.part.PageBookView#createDefaultPage(org.eclipse.ui.part.PageBook) */
    protected IPage createDefaultPage(PageBook book) {
        // builds a message page showing a text
        MessagePage page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage(getString(pageMessageId()));
        return page;
    }
    
    protected static String getString(String textId) {
    	return PMDPlugin.getDefault().getStringTable().getString(textId);
    }
    
    @Override
    public void dispose() {
        memento.save(mementoFileId());

        super.dispose();
    }
}
