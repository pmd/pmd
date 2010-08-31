package net.sourceforge.pmd.eclipse.ui.views.ast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.eclipse.ui.editors.SyntaxManager;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.views.AbstractStructureInspectorPage;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ParseException;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;

/**
 * A combined abstract syntax tree viewer for a whole class or selected methods 
 * and an XPath editor/evaluator on the right that works with it.
 * 
 * @author Brian Remedios
 *
 */
public class ASTViewPage extends AbstractStructureInspectorPage {

	private Composite 	astFrame;

	protected TreeViewer astViewer;

	private TextLayout 	textLayout;
	private Font		renderFont;
	private TextStyle 	labelStyle;
	private TextStyle 	imageStyle;

	private StyledText	xpathField;
	private StyledText  outputField;

	private Node		classNode;
	
	private static Set<String> keywords = new HashSet<String>();

	/**
	 * Constructor
	 * @param part
	 * @param record the FileRecord
	 */
	public ASTViewPage(IWorkbenchPart part, FileRecord record) {
		super(part, record);
	}
	
	public void createControl(Composite parent) {

		astFrame = new Composite(parent, SWT.NONE);        

		GridLayout mainLayout = new GridLayout(3, false);
		astFrame.setLayout(mainLayout);

		Composite titleArea = new Composite(astFrame, SWT.NONE);
		GridData tableData = new GridData(GridData.FILL_HORIZONTAL);
		tableData.horizontalSpan = 2;
		titleArea.setLayoutData(tableData);
		titleArea.setLayout(new GridLayout(4, false));

		Label showLabel = new Label(titleArea, 0);
		showLabel.setText("Show: ");

		final Button classBtn = new Button(titleArea, SWT.RADIO);
		classBtn.setText("Class");
		classBtn.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (classBtn.getSelection()) showClass();
			}
		} );
		
		final Button methodBtn = new Button(titleArea, SWT.RADIO);
		methodBtn.setText("Method");
		methodBtn.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				methodSelector.setEnabled( methodBtn.getSelection() );
				methodPicked();
			}
		} );
		
		buildMethodSelector(titleArea);

		astViewer = new TreeViewer(astFrame);
		astViewer.setContentProvider( new ASTContentProvider() );
		astViewer.setLabelProvider( new ASTLabelProvider() );
		setupListeners(astViewer.getTree());

		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		astViewer.getTree().setLayoutData(data);

		Composite xpathTestPanel = new Composite(astFrame, SWT.NONE);
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		xpathTestPanel.setLayoutData(data);

		GridLayout playLayout = new GridLayout(2, false);
		xpathTestPanel.setLayout(playLayout);

		xpathField = new StyledText(xpathTestPanel, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 1;
		xpathField.setLayoutData(gridData);
		SyntaxManager.adapt(xpathField, "xpath", null);

		Button goButton = new Button(xpathTestPanel, SWT.PUSH);
		goButton.setText("GO");
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = false;
		gridData.horizontalSpan = 1;
		goButton.setLayoutData(gridData);
		goButton.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				evaluateXPath();				
			}
		} );
		
		outputField = new StyledText(xpathTestPanel, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		outputField.setLayoutData(gridData);
		SyntaxManager.adapt(outputField, "xpath", null);
		
		showFirstMethod();
	}
	
	private static void displayOn(Node node, StringBuilder sb) {

		sb.append(node);
		
		String imgData = node.getImage();
//		sb.append( "[" + node.getBeginColumn() +", " + node.getEndColumn() + "] " );
		
		if (imgData != null ) sb.append("  ").append( imgData );
	}

	private void evaluateXPath() {

		outputField.setText("");
		
		if (xpathField.getText().length() == 0) {
			outputField.setText("XPath query field is empty.");
			return;
		}
		
		List<Node> results = null;
		try {
			results = XPathEvaluator.instance.evaluate( 
					getDocument().get(), 
					xpathField.getText(),
					XPathRuleQuery.XPATH_1_0
					);
		} catch (ParseException pe) {
			outputField.setText(pe.fillInStackTrace().getMessage());
			return;
		}
		
		if (results.isEmpty()) {
			outputField.setText("No matching nodes " + System.currentTimeMillis());
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		for (Node node : results) {
			displayOn(node, sb);
			sb.append('\n');
		}
		outputField.setText( sb.toString() );
	}

	private TextLayout layoutFor(TreeItem item) {

		AbstractNode node = (AbstractNode)item.getData();
		String label = node.toString();

		keywords.add(label);

		String extra = node.getImage();

		textLayout.setText(label + (extra == null  ? "" : " " + extra));

		int labelLength = label.length();

		textLayout.setStyle(labelStyle, 0, labelLength);
		if (extra != null) {
			textLayout.setStyle(imageStyle, labelLength, labelLength + extra.length() + 1);
		}

		return textLayout;
	}

	private void setupListeners(Tree tree) {

		setupResources(tree.getDisplay());

		tree.addListener(SWT.PaintItem, new Listener() {
			public void handleEvent(Event event) {
				TextLayout layout = layoutFor((TreeItem)event.item);
				layout.draw(event.gc, event.x, event.y + 5);
			}
		});

		tree.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event e) {
				Rectangle textLayoutBounds = layoutFor((TreeItem)e.item).getBounds();
				e.width = textLayoutBounds.width + 2;
				e.height = textLayoutBounds.height + 2;
			}
		});
	}

	private void setupResources(Display display) {
		textLayout = new TextLayout(display);    	    	
		renderFont = new Font(display, "Tahoma", 10, SWT.NORMAL);
		labelStyle = new TextStyle(renderFont, display.getSystemColor(SWT.COLOR_BLACK), null);
		imageStyle = new TextStyle(renderFont, display.getSystemColor(SWT.COLOR_BLUE), null);
	}

	public void dispose() {
		renderFont.dispose();
	}

	public Control getControl() {
		return astFrame;
	}

	protected void showClass() {
		
		if (classNode == null) {
			String source = getDocument().get();
			classNode = XPathEvaluator.instance.getCompilationUnit(source);
		}
		
		astViewer.setInput(classNode);
		astViewer.expandAll();
	}
	
	protected void showMethod(ASTMethodDeclaration pmdMethod) {

		if (pmdMethod == null) return;

		astViewer.setInput(pmdMethod);
		astViewer.expandAll();
	}

    /**
	 * Shows the DataflowGraph (and Dataflow-Anomalies) for a Method.
	 *
	 * @param pmdMethod Method to show in the graph
	 */
//	protected void showMethod(final ASTMethodDeclaration pmdMethod) {
//		if (pmdMethod != null) {
//
//			final String resourceString = getDocument().get();
//			 give the Data to the GraphViewer
//			            astViewer.setVisible(true);
//			            astViewer.setData(pmdMethod, resourceString);
//			            astViewer.addMouseListener(new MouseAdapter() {
//			
//			                @Override
//			                public void mouseDown(MouseEvent e) {
//			                    if (textEditor != null) {
//			                        final int row = (int)((double)e.y / DataflowGraphViewer.ROW_HEIGHT);
//			                        astViewer.getGraph().demark();
//			                        astViewer.getGraph().markNode(row);
//			                        final int startLine = pmdMethod.getDataFlowNode().getFlow().get(row).getLine()-1;
//			                        int offset = 0;
//			                        int length = 0;
//			                        try {
//			                            offset = getDocument().getLineOffset(startLine);
//			                            length = getDocument().getLineLength(startLine);
//			                        } catch (BadLocationException ble) {
//			                            logError(StringKeys.MSGKEY_ERROR_RUNTIME_EXCEPTION + "Exception when selecting a line in the editor" , ble);
//			                        }
//			                        textEditor.selectAndReveal(offset, length);
//			                        astViewer.getTree().deselectAll();
//			                    }
//			                }
//			            });
//			            showTableArea(isTableShown);
//		}
//	}

	/**
	 * Refreshes the page with a new resource.
	 * @param newResource new resource for the page
	 */
	public void refresh(IResource newResource) {
		if (newResource.getType() == IResource.FILE) {
			// set a new filerecord
			resourceRecord = new FileRecord(newResource);
		}

		classNode = null;
		
		//        if (isTableShown) {
		//            refreshDFATable(newResource);
		//        } else {
		//            this.isTableRefreshed = false;
		//        }

		// refresh the methods and select the old selected method
		final int index = methodSelector.getSelectionIndex();
		refreshPMDMethods();
		showMethod(index);
		methodSelector.select(index);
		
	}

	/**
	 * Executes a command to refresh the DFA table.
	 * After execution {@link #refresh(IResource)} will be called.
	 * @param newResource the new resource
	 */
	//    public void refreshDFATable(IResource newResource) {
	//        this.isTableRefreshed = true;
	//        try {
	//            final ReviewResourceForRuleCommand cmd = new ReviewResourceForRuleCommand();
	//            final DataflowAnomalyAnalysisRule rule = new DataflowAnomalyAnalysisRule();
	//            rule.setUsesDFA();
	//            cmd.setUserInitiated(false);
	//            cmd.setRule(rule);
	//            cmd.setResource(newResource);
	//            cmd.addPropertyListener(this);
	//            cmd.performExecute();
	//        } catch (CommandException e) {
	//        	logErrorByKey(StringKeys.MSGKEY_ERROR_PMD_EXCEPTION, e);
	//        }
	//    }

}
