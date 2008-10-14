package net.sourceforge.pmd.eclipse.ui.views;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.dfa.DaaRule;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.runtime.cmd.ReviewResourceForRuleCommand;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.util.designer.DFAGraphRule;

import org.eclipse.core.resources.IResource;
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
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * A page for the dataflow - view.
 * 
 * @author Sven Jacob
 *
 */
public class DataflowViewPage extends Page implements IPropertyListener, ISelectionChangedListener, SelectionListener {
    private Composite dfaFrame;
    private CCombo methodSelection;
    private Button switchButton;
    
    protected Composite titleArea;
    protected DataflowGraphViewer graphViewer;
    protected DataflowAnomalyTableViewer tableViewer;
    
    private List pmdMethodList;
    private FileRecord resourceRecord;
    private ITextEditor textEditor; // NOPMD by Sven on 09.11.06 22:18
    
    private boolean isTableShown;
    private boolean isTableRefreshed;
    
    /**
     * Constructor
     * @param part 
     * @param record the FileRecord
     */
    public DataflowViewPage(IWorkbenchPart part, FileRecord record) {
        super();
        this.resourceRecord = record;
        if (part instanceof ITextEditor) {
            textEditor = (ITextEditor) part;
        }
    }
    
    /* @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite) */
    public void createControl(Composite parent) {
        dfaFrame = new Composite(parent, SWT.NONE);

        // //////////////////////////////////////////////////
        // the upper Title Area

        titleArea = new Composite(dfaFrame, SWT.NONE);
        final GridData tableData = new GridData(GridData.FILL_HORIZONTAL);
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
        final Label label = new Label(titleArea, SWT.NONE);
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
        this.isTableRefreshed = false;
        
        final GridLayout mainLayout = new GridLayout(2, true);
        mainLayout.horizontalSpacing = mainLayout.verticalSpacing = 7;
        mainLayout.marginWidth = 3; mainLayout.marginHeight = 3;
        dfaFrame.setLayout(mainLayout);
        
        // hide the table
        showTableArea(false);
    }
    
    
    /* @see org.eclipse.ui.part.Page#dispose() */
    public void dispose() {
    }

    /**
     * Refreshs the list of pmd methods for the combobox.
     * @see #getPMDMethods(IResource)
     */
    private void refreshPMDMethods() {
        pmdMethodList = getPMDMethods();
        methodSelection.removeAll();
        for (int i = 0; i < pmdMethodList.size(); i++) {
            final ASTMethodDeclaration pmdMethod = (ASTMethodDeclaration) pmdMethodList.get(i);
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
     * Confort method to show a method.
     * @param index index position of the combobox
     */
    private void showMethod(final int index) {
        if (index >= 0 && index < pmdMethodList.size() ) {
            final ASTMethodDeclaration method = (ASTMethodDeclaration) pmdMethodList.get(index);
            showMethod(method);
        }
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
                        final int row = (int)((double)e.y / DataflowGraphViewer.ROW_HEIGHT);
                        graphViewer.getGraph().demark();
                        graphViewer.getGraph().markNode(row);
                        final int startLine = ((DataFlowNode)pmdMethod.getDataFlowNode().getFlow().get(row)).getLine()-1;
                        int offset = 0;
                        int length = 0;
                        try {
                            offset = getDocument().getLineOffset(startLine);
                            length = getDocument().getLineLength(startLine);
                        } catch (BadLocationException ble) {
                            PMDPlugin.getDefault().logError(
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
     * Shows the method that belongs to a violation (to a line).
     * @param violation RuleViolation
     */
    private void showMethodToViolation(RuleViolation violation) {
        final int beginLine = violation.getBeginLine();
        
        for (int i = 0; i < pmdMethodList.size(); i++) {
            final ASTMethodDeclaration pmdMethod = (ASTMethodDeclaration) pmdMethodList.get(i);
            if (beginLine >= pmdMethod.getBeginLine() 
                    && beginLine <= pmdMethod.getEndLine()) {
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
    private List getPMDMethods() {
        final List methodList = new ArrayList();

        // we need PMD to run over the given Resource
        // with the DFAGraphRule to get the Methods;
        // PMD needs this Resource as a String
        try {
            final DFAGraphRule dfaGraphRule = new DFAGraphRule();
            final RuleSet rs = new RuleSet();
            rs.addRule(dfaGraphRule);
            final RuleContext ctx = new RuleContext();
            ctx.setSourceCodeFilename("[scratchpad]");

            final StringReader reader = new StringReader(getDocument().get());

            // run PMD using the DFAGraphRule
            // and the Text of the Resource
            (new PMD()).processFile(reader, rs, ctx);

            // the Rule then can give us the Methods
            methodList.addAll(dfaGraphRule.getMethods());
        } catch (PMDException pmde) {
            PMDPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_PMD_EXCEPTION + this.toString(), pmde);
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

            // refresh the table if it isnt refreshed yet.
            if (!isTableRefreshed) {
                refreshDFATable(this.resourceRecord.getResource());
            }
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
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }
    
    /* @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent) */
    public void selectionChanged(SelectionChangedEvent event) {
        // get the Selection
        if (event.getSelection() instanceof IStructuredSelection) {
            final Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();
            if (element instanceof RuleViolation) {
                final RuleViolation violation = (RuleViolation) element;
                final String varName = violation.getVariableName();
                final int beginLine = violation.getBeginLine();
                final int endLine = violation.getEndLine();

                if ((beginLine != 0) && (endLine != 0) && (!"".equals(varName))) {
                    try {
                        final int offset = getDocument().getLineOffset(violation.getBeginLine()-1);
                        final int length = getDocument().getLineOffset(violation.getEndLine()) - offset;
                        this.textEditor.selectAndReveal(offset, length);
                    } catch (BadLocationException ble) {
                        PMDPlugin.getDefault().logError(
                                StringKeys.MSGKEY_ERROR_RUNTIME_EXCEPTION + "Exception when selecting a line in the editor" , ble);
                    }

                    //showMethodToMarker(marker);
                    showMethodToViolation(violation);

                    // then we calculate and color _a possible_ Path
                    // for this Error in the Dataflow
                    final DataflowGraph graph = graphViewer.getGraph();
                    if (!graphViewer.isDisposed() && graph != null) {
                        graph.markPath(beginLine, endLine, varName);
                    }
                }
            }
        }
    }

    /* @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent) */
    public void widgetDefaultSelected(SelectionEvent e) {
        if (methodSelection.equals(e.widget)) {
            showMethod(methodSelection.getSelectionIndex());
        }
    }

    /* @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent) */
    public void widgetSelected(SelectionEvent e) {
        if (switchButton.equals(e.widget)) {
            // the event can be catched from the switchbutton ...
            isTableShown = !isTableShown;
            showTableArea(isTableShown);
    
            if (!isTableShown) {
                if ((graphViewer == null) || (graphViewer.getGraph() == null)) {
                    return;
                }
    
                final DataflowGraph graph = graphViewer.getGraph();
                if (graph.isMarked()) {
                    graph.demark();
                }
            }
        } else if (methodSelection.equals(e.widget)) {
            // ... or from the combobox
            final int index = methodSelection.getSelectionIndex();
            methodSelection.setSelection(new Point(0,0));
            showMethod(index);
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
        }
        
        if (isTableShown) {
            refreshDFATable(newResource);
        } else {
            this.isTableRefreshed = false;
        }
        
        // refresh the methods and select the old selected method
        final int index = methodSelection.getSelectionIndex();
        refreshPMDMethods();
        showMethod(index);
        methodSelection.select(index);
    }

    /**
     * Executes a command to refresh the dfa table. 
     * After execution {@link #refresh(IResource)} will be called.
     * @param newResource the new resource
     */
    public void refreshDFATable(IResource newResource) {
        this.isTableRefreshed = true;
        try {            
            final ReviewResourceForRuleCommand cmd = new ReviewResourceForRuleCommand();
            final DaaRule rule = new DaaRule();
            rule.setUsesDFA();
            cmd.setUserInitiated(false);
            cmd.setRule(rule);
            cmd.setResource(newResource);
            cmd.addPropertyListener(this);
            cmd.performExecute();
        } catch (CommandException e) {
            PMDPlugin.getDefault().logError(getString(StringKeys.MSGKEY_ERROR_PMD_EXCEPTION), e);
        } 
    }
    
    /**
     * If the review is ready propertyChanged with the results will be called.
     */
    public void propertyChanged(Object source, int propId) {
        if (source instanceof Iterator 
                && propId == PMDRuntimeConstants.PROPERTY_REVIEW) {
            tableViewer.setInput(source);
        }
    }
}
