package net.sourceforge.pmd.eclipse.ui.views.dataflow;

import java.util.Iterator;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.runtime.cmd.ReviewResourceForRuleCommand;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.views.AbstractStructureInspectorPage;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.controversial.DataflowAnomalyAnalysisRule;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * A page for the dataflow - view.
 *
 * @author Sven Jacob
 *
 */
public class DataflowViewPage extends AbstractStructureInspectorPage implements IPropertyListener {
	
    private Composite 					dfaFrame;
    private Button 						switchButton;

    protected DataflowGraphViewer 		graphViewer;
    protected DataflowAnomalyTableViewer tableViewer;

    private boolean isTableShown;
    private boolean isTableRefreshed;

    /**
     * Constructor
     * @param part
     * @param record the FileRecord
     */
    public DataflowViewPage(IWorkbenchPart part, FileRecord record) {
        super(part, record);
       
    }

    /* @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite) */
    @Override
    public void createControl(Composite parent) {
        dfaFrame = new Composite(parent, SWT.NONE);

        // //////////////////////////////////////////////////
        // upper title area

        Composite titleArea = new Composite(dfaFrame, SWT.NONE);
        GridData tableData = new GridData(GridData.FILL_HORIZONTAL);
        tableData.horizontalSpan = 2;
        titleArea.setLayoutData(tableData);
        titleArea.setLayout(new GridLayout(4, false));

        Label methodLabel = new Label(titleArea, 0);
        methodLabel.setText("Method: ");
        
        buildMethodSelector(titleArea);

        // a label for the spacing
        Label label = new Label(titleArea, SWT.NONE);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // the Button for showing or hiding the Anomaly-List
        switchButton = new Button(titleArea, SWT.RIGHT);
        switchButton.setLayoutData(new GridData(130, 25));
        switchButton.addSelectionListener(new SelectionAdapter() {        	
        	public void widgetSelected(SelectionEvent se) {
	            isTableShown = !isTableShown;
	            showTableArea(isTableShown);
	
	            if (!isTableShown) {
	                if (graphViewer == null || graphViewer.getGraph() == null) {
	                    return;
	                }
	
	                final DataflowGraph graph = graphViewer.getGraph();
	                if (graph.isMarked()) {
	                    graph.demark();
	                }
	            }
        	}}
        );
        
        switchButton.setText(getString(StringKeys.VIEW_DATAFLOW_SWITCHBUTTON_SHOW));

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
        isTableRefreshed = false;

        GridLayout mainLayout = new GridLayout(2, true);
        mainLayout.horizontalSpacing = mainLayout.verticalSpacing = 7;
        mainLayout.marginWidth = 3; 	
        mainLayout.marginHeight = 3;
        dfaFrame.setLayout(mainLayout);

        // hide the table
        showTableArea(false);
        showFirstMethod();
    }

    /* @see org.eclipse.ui.part.IPage#getControl() */
    @Override
    public Control getControl() {
        return dfaFrame;
    }

    /**
     * Shows the DataflowGraph (and Dataflow-Anomalies) for a Method.
     *
     * @param pmdMethod Method to show in the graph
     */
    protected void showMethod(final ASTMethodDeclaration pmdMethod) {
        if (pmdMethod != null) {

            String resourceString = getDocument().get();
            // give the Data to the GraphViewer
            graphViewer.setVisible(true);
            graphViewer.setData(pmdMethod, resourceString);
            graphViewer.addMouseListener(new MouseAdapter() {

                public void mouseDown(MouseEvent e) {
                   int row = (int)((double)e.y / DataflowGraphViewer.ROW_HEIGHT);
                   graphViewer.getGraph().demark();
                   graphViewer.getGraph().markNode(row);
                   highlightLine( pmdMethod.getDataFlowNode().getFlow().get(row).getLine()-1 );
                   tableViewer.getTable().deselectAll();
                }
            });
            showTableArea(isTableShown);
        }
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
            switchButton.setText(getString(StringKeys.VIEW_DATAFLOW_SWITCHBUTTON_HIDE));

            // refresh the table if it isn't refreshed yet.
            if (!isTableRefreshed) {
                refreshDFATable(getResource());
            }
        } else {
            ((GridData) graphViewer.getLayoutData()).horizontalSpan = 2;
            switchButton.setText(getString(StringKeys.VIEW_DATAFLOW_SWITCHBUTTON_SHOW));
        }

        // lay out to update the View
        dfaFrame.layout(true, true);
    }
    
    /* @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent) */
	public void selectionChanged(SelectionChangedEvent event) {

		RuleViolation violation = selectedViolationFrom(event);
		if (violation == null) return;
		
		String varName = violation.getVariableName();
		if (StringUtil.isEmpty(varName)) return;
		
		int beginLine = violation.getBeginLine();
		int endLine = violation.getEndLine();

		if (beginLine != 0 && endLine != 0) {
			try {
				int offset = getDocument().getLineOffset(violation.getBeginLine() - 1);
				int length = getDocument().getLineOffset(violation.getEndLine()) - offset;
				highlight(offset, length);
			} catch (BadLocationException ble) {
				logError(StringKeys.ERROR_RUNTIME_EXCEPTION	+ "Exception when selecting a line in the editor", ble);
			}

			// showMethodToMarker(marker);
			showMethodToViolation(violation);

			// then we calculate and color _a possible_ Path
			// for this Error in the Dataflow
			DataflowGraph graph = graphViewer.getGraph();
			if (!graphViewer.isDisposed() && graph != null) {
				graph.markPath(beginLine, endLine, varName);
			}
		}
	}

    /**
     * Refreshes the page with a new resource.
     * @param newResource new resource for the page
     */
    public void refresh(IResource newResource) {
    	super.refresh(newResource);

        if (isTableShown) {
            refreshDFATable(newResource);
        } else {
            isTableRefreshed = false;
        }

        refreshMethodSelector();
    }

    /**
     * Executes a command to refresh the DFA table.
     * After execution {@link #refresh(IResource)} will be called.
     * @param newResource the new resource
     */
    public void refreshDFATable(IResource newResource) {
        isTableRefreshed = true;
        try {
            ReviewResourceForRuleCommand cmd = new ReviewResourceForRuleCommand();
            DataflowAnomalyAnalysisRule rule = new DataflowAnomalyAnalysisRule();
            rule.setUsesDFA();
            cmd.setUserInitiated(false);
            cmd.setRule(rule);
            cmd.setResource(newResource);
            cmd.addPropertyListener(this);
            cmd.performExecute();
        } catch (CommandException e) {
        	logErrorByKey(StringKeys.ERROR_PMD_EXCEPTION, e);
        }
    }

    /**
     * If the review is ready propertyChanged with the results will be called.
     */
    public void propertyChanged(Object source, int propId) {
        if (source instanceof Iterator<?>
                && propId == PMDRuntimeConstants.PROPERTY_REVIEW) {
            tableViewer.setInput(source);
        }
    }
    
}
