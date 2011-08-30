package net.sourceforge.pmd.eclipse.ui.views.dataflow;

import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.views.AbstractResourceView;
import net.sourceforge.pmd.eclipse.ui.views.AbstractStructureInspectorPage;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;

/**
 * A View that shows DataflowGraph and -Table as well as the Anomaly-List
 *
 * @author SebastianRaffel ( 26.05.2005 ), Sven Jacob ( 19.09.2006 )
 */
public class DataflowView extends AbstractResourceView {

    
    protected String pageMessageId() { return StringKeys.VIEW_DATAFLOW_DEFAULT_TEXT; }

	@Override
	protected String mementoFileId() { return PMDUiConstants.MEMENTO_DATAFLOW_FILE; }
	
    /* @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart) */
    @Override
    protected PageRec doCreatePage(IWorkbenchPart part) {
    	
        FileRecord resourceRecord = getFileRecordFromWorkbenchPart(part);
        if (resourceRecord != null) {
            setupListener(resourceRecord);

            // creates a new DataflowViewPage, when a Resource exists
            DataflowViewPage page = new DataflowViewPage(part, resourceRecord);
            initPage(page);
            page.createControl(getPageBook());

            return new PageRec(part, page);
        }
        return null;
    }

    protected AbstractStructureInspectorPage getCurrentViewPage() {
    	return getCurrentDataflowViewPage();
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

}
