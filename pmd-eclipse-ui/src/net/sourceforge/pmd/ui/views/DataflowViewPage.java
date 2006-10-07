package net.sourceforge.pmd.ui.views;

import java.io.StringReader;
import java.util.ArrayList;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.model.FileRecord;
import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.ui.views.actions.ReviewResourceAction;
import net.sourceforge.pmd.util.designer.DFAGraphRule;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * A page for the dataflow - view.
 * 
 * @author Sven Jacob
 *
 */
public class DataflowViewPage extends Page implements IPage, ISelectionChangedListener, SelectionListener {
    private Composite dfaFrame;
    private CCombo methodSelection;
    private Button switchButton;
    private ReviewResourceAction reviewResourceAction;
    
    protected Composite titleArea;
    protected DataflowGraphViewer graphViewer;
    protected DataflowAnomalyTableViewer tableViewer;
    
    private ArrayList pmdMethodList;
    private FileRecord resourceRecord;
    private ITextEditor textEditor;
    
    protected boolean isTableShown;
    
    /**
     * Constructor
     * @param part 
     * @param record the FileRecord
     */
    public DataflowViewPage(IWorkbenchPart part, FileRecord record) {
        this.resourceRecord = record;
        if (part instanceof ITextEditor) {
            textEditor = (ITextEditor) part;
        }
    }
    
    /* @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite) */
    public void createControl(Composite parent) {
        dfaFrame = new Composite(parent, SWT.NONE);

        // Menubar
        IToolBarManager manager = getSite().getActionBars().getToolBarManager();
        reviewResourceAction = new ReviewResourceAction(resourceRecord.getResource());
        manager.add(reviewResourceAction);
        manager.add(new Separator());
        
        // //////////////////////////////////////////////////
        // the upper Title Area

        titleArea = new Composite(dfaFrame, SWT.NONE);
        GridData tableData = new GridData(GridData.FILL_HORIZONTAL);
        tableData.horizontalSpan = 2;
        titleArea.setLayoutData(tableData);        
        titleArea.setLayout(new GridLayout(3, false));
        
        // the drop down box for showing all methods of the given ressource
        methodSelection = new CCombo(titleArea, SWT.LEFT | SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
        refreshPMDMethods();
        methodSelection.setText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_CHOOSE_METHOD));
        methodSelection.setLayoutData(new GridData(200, SWT.DEFAULT));                
        methodSelection.addSelectionListener(this);

        // a label for the spacing
        Label label = new Label(titleArea, SWT.NONE);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        // the Button for showing or hiding the Anomaly-List
        switchButton = new Button(titleArea, SWT.RIGHT);
        switchButton.setLayoutData(new GridData(130, 20));
        switchButton.addSelectionListener(this);   
        switchButton.setText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_SWITCHBUTTON_SHOW));


        // //////////////////////////////////////////////////
        // the DataflowGraphViewer (left Part)
        graphViewer = new DataflowGraphViewer(dfaFrame, SWT.NONE);
        graphViewer.setVisible(false);

        // //////////////////////////////////////////////////
        // the DataflowAnomalyTable (right Part)
        tableViewer = new DataflowAnomalyTableViewer(dfaFrame, SWT.BORDER);
        tableViewer.addSelectionChangedListener(this);
        tableViewer.setContentProvider(new DataflowAnomalyTableContentProvider());
        tableViewer.setLabelProvider(new DataflowAnomalyTableLabelProvider());
        tableViewer.setInput(resourceRecord);

        GridLayout mainLayout = new GridLayout(2, true);
        mainLayout.horizontalSpacing = mainLayout.verticalSpacing = 7;
        mainLayout.marginWidth = 3; mainLayout.marginHeight = 3;
        dfaFrame.setLayout(mainLayout);
        
        // hide the table
        showTableArea(false);
    }
    
    
    /* @see org.eclipse.ui.part.Page#dispose() */
    public void dispose() {
        super.dispose();
    }

    /**
     * Refreshs the list of pmd methods for the combobox.
     * @see #getPMDMethods(IResource)
     */
    private void refreshPMDMethods() {
        pmdMethodList = getPMDMethods();
        methodSelection.removeAll();
        for (int i = 0; i < pmdMethodList.size(); i++) {
            ASTMethodDeclaration pmdMethod = (ASTMethodDeclaration) pmdMethodList.get(i);
            methodSelection.add(getMethodLabel(pmdMethod), i);
        }    
    }

    /**
     * Gets the label of a method for an element of the combobox.
     * @param pmdMethod the method to create a label for
     * @return a label for the method
     */
    private String getMethodLabel(ASTMethodDeclaration pmdMethod) {
        return pmdMethod.getMethodName() + " (line " + pmdMethod.getBeginLine() + "-" + pmdMethod.getEndLine() + ")";
    }

    /* @see org.eclipse.ui.part.IPage#getControl() */
    public Control getControl() {
        return dfaFrame;
    }

    /* @see org.eclipse.ui.part.WorkbenchPart#setFocus() */
    public void setFocus() {
        methodSelection.setFocus();
    }
    
    /**
     * @return the underlying FileRecord
     */
    public FileRecord getFileRecord() {
        return resourceRecord;
    }
    
    /**
     * Shows the DataflowGraph (and Dataflow-Anomalies) for a Method.
     * 
     * @param pmdMethod Method to show in the graph 
     */
    private void showMethod(final ASTMethodDeclaration pmdMethod) {
        if (pmdMethod != null) {

            final String resourceString = getDocument().get();
            // give the Data to the GraphViewer
            graphViewer.setVisible(true);
            graphViewer.setData(pmdMethod, resourceString);
            graphViewer.addMouseListener(new MouseAdapter() {

                public void mouseDown(MouseEvent e) {
                    if (textEditor != null) {
                        int row = (int)((double)e.y / DataflowGraphViewer.rowHeight);
                        graphViewer.getGraph().demark();
                        graphViewer.getGraph().markNode(row);
                        int startLine = ((IDataFlowNode)pmdMethod.getDataFlowNode().getFlow().get(row)).getLine()-1;
                        int offset = 0;
                        int length = 0;
                        try {
                            offset = getDocument().getLineOffset(startLine);
                            length = getDocument().getLineLength(startLine);
                        } catch (BadLocationException ble) {
                            PMDUiPlugin.getDefault().logError(
                                    StringKeys.MSGKEY_ERROR_RUNTIME_EXCEPTION + "Exception when selecting a line in the editor" , ble);
                        }
                        textEditor.selectAndReveal(offset, length);
                        tableViewer.getTable().deselectAll();
                    }
                }
            });
            showTableArea(isTableShown);
        }
    }
    
    /**
     * Shows the method that belongs to a marker (to a line).
     * @param marker IMarker
     */
    private void showMethodToMarker(IMarker marker) {
        int line = marker.getAttribute(IMarker.LINE_NUMBER, 0);
        
        for (int i = 0; i < pmdMethodList.size(); i++) {
            ASTMethodDeclaration pmdMethod = (ASTMethodDeclaration) pmdMethodList.get(i);
            if (line >= pmdMethod.getBeginLine() 
                    && line <= pmdMethod.getEndLine()) {
                showMethod(pmdMethod);
                // select the method in the combobox
                methodSelection.select(i);
                return;
            }
        }
    }

    /**
     * Gets a List of all PMD-Methods.
     * 
     * @return an ArrayList of ASTMethodDeclarations
     */
    private ArrayList getPMDMethods() {
        ArrayList methodList = new ArrayList();

        // we need PMD to run over the given Resource
        // with the DFAGraphRule to get the Methods;
        // PMD needs this Resource as a String
        try {
            DFAGraphRule dfaGraphRule = new DFAGraphRule();
            RuleSet rs = new RuleSet();
            rs.addRule(dfaGraphRule);
            RuleContext ctx = new RuleContext();
            ctx.setSourceCodeFilename("[scratchpad]");

            StringReader reader = new StringReader(getDocument().get());

            // run PMD using the DFAGraphRule
            // and the Text of the Resource
            (new PMD()).processFile(reader, rs, ctx);

            // the Rule then can give us the Methods
            methodList.addAll(dfaGraphRule.getMethods());
        } catch (PMDException pmde) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_PMD_EXCEPTION + this.toString(), pmde);
        }

        return methodList;
    }

    /**
     * Gets the Document of the page.
     * @return instance of IDocument of the page
     */
    public IDocument getDocument() {
        return textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
    }
    
    /**
     * Shows or hides the DataflowAnomalyTable, set the right text for the button.
     * 
     * @param isShown, true if the Table should be visible, false otherwise
     */
    private void showTableArea(boolean isShown) {
        tableViewer.setVisible(isShown);

        // if the AnomalyTable is visible, an Area is 50% of the View
        // set the new Size and update the SwitchButton's Label
        if (isShown) {
            ((GridData) graphViewer.getLayoutData()).horizontalSpan = 1;
            switchButton.setText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_SWITCHBUTTON_HIDE));
        } else {
            ((GridData) graphViewer.getLayoutData()).horizontalSpan = 2;
            switchButton.setText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_SWITCHBUTTON_SHOW));
        }

        // lay out to update the View
        dfaFrame.layout(true, true);
    }

    /**
     * Helper method to return an NLS string from its key.
     */
    private String getString(String key) {
        return PMDUiPlugin.getDefault().getStringTable().getString(key);
    }
    
    /* @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent) */
    public void selectionChanged(SelectionChangedEvent event) {
        // get the Selection
        if (!(event.getSelection() instanceof IStructuredSelection))
            return;
        Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();

        if (element instanceof IMarker) {
            // it should be a marker of the anomaly table
            IMarker marker = (IMarker) element;
            if (marker != null) {
                // gotoMarker in the Editor..
                IEditorPart editor = getSite().getPage().getActiveEditor();
                if (editor != null) {
                    IEditorInput input = editor.getEditorInput();
                    if (input instanceof IFileEditorInput) {
                        IFile file = ((IFileEditorInput) input).getFile();
                        if (marker.getResource().equals(file)) {
                            IDE.gotoMarker(editor, marker);
                        }
                    }
                }
                int line1 = marker.getAttribute(IMarker.LINE_NUMBER, 0);
                int line2 = marker.getAttribute(PMDUiConstants.KEY_MARKERATT_LINE2, 0);
                String varName = marker.getAttribute(PMDUiConstants.KEY_MARKERATT_VARIABLE, "");

                if ((line1 == 0) || (line2 == 0) || (varName == ""))
                    return;

                // then we calculate and color _a possible_ Path
                // for this Error in the Dataflow
                if (graphViewer.isDisposed()) return;
                
                showMethodToMarker(marker);
                DataflowGraph graph = graphViewer.getGraph();
                if (graph != null) {
                    graph.markPath(line1, line2, varName);
                }
            }
        }
    }

    /* @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent) */
    public void widgetDefaultSelected(SelectionEvent e) {
        if (methodSelection.equals(e.widget)) {
            int index = methodSelection.getSelectionIndex();
            if (index != -1) {
                ASTMethodDeclaration method = (ASTMethodDeclaration) pmdMethodList.get(index);
                showMethod(method);
            }
        }
    }

    /* @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent) */
    public void widgetSelected(SelectionEvent e) {
        if (switchButton.equals(e.widget)) {
            // the event can be catched from the switchbutton ...
            isTableShown = !isTableShown;
            showTableArea(isTableShown);
    
            if (isTableShown == false) {
                if ((graphViewer == null) || (graphViewer.getGraph() == null))
                    return;
    
                DataflowGraph graph = graphViewer.getGraph();
                if (graph.isMarked())
                    graph.demark();
            }
        } else if (methodSelection.equals(e.widget)) {
            // ... or from the combobox
            int index = methodSelection.getSelectionIndex();
            methodSelection.setSelection(new Point(0,0));
            if (index != -1) {
                ASTMethodDeclaration method = (ASTMethodDeclaration) pmdMethodList.get(index);
                showMethod(method);
            }
        }
    }

    /**
     * Refreshs the page with a new resource.
     * @param newResource new resource for the page
     */
    public void refresh(IResource newResource) {
        if (newResource.getType() == IResource.FILE) {
            // set a new filerecord
            resourceRecord = new FileRecord(newResource);
            // set the resource for the reviewaction
            reviewResourceAction.setResource(newResource);
        }
        
        // refresh the methods and select the old selected method
        int index = methodSelection.getSelectionIndex();
        refreshPMDMethods();
        if (index != -1) {
            ASTMethodDeclaration method = (ASTMethodDeclaration) pmdMethodList.get(index);
            showMethod(method);
        }       
        methodSelection.select(index);
    }
}
