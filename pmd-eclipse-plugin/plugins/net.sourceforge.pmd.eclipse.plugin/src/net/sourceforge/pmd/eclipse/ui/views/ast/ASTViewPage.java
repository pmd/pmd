package net.sourceforge.pmd.eclipse.ui.views.ast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.eclipse.ui.BasicTableLabelProvider;
import net.sourceforge.pmd.eclipse.ui.editors.SyntaxManager;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.views.AbstractStructureInspectorPage;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ParseException;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;
import org.jaxen.BaseXPath;

/**
 * A combined abstract syntax tree viewer for a whole class or selected methods 
 * and an XPath editor/evaluator on the right that works with it.
 * 
 * @author Brian Remedios
 *
 */
public class ASTViewPage extends AbstractStructureInspectorPage {

	private SashForm 	sashForm;

	protected TreeViewer astViewer;

	private TextLayout 	textLayout;
	private Font		renderFont;
	private Font		italicFont;
	private TextStyle 	labelStyle;
	private TextStyle 	imageStyle;
	private TextStyle 	derivedStyle;

	private StyledText	xpathField;
	private TableViewer resultsViewer;
	private Button		goButton;
	private Node		classNode;
	
//	private static Set<String> keywords = new HashSet<String>();

	private static Set<Class<?>> HiddenNodeTypes;
	static {
		HiddenNodeTypes = new HashSet<Class<?>>();
		HiddenNodeTypes.add(ASTImportDeclaration.class);
	}
	
	public TreeViewer astViewer() {
		return astViewer;
	}
	
	/**
	 *
	 * @param part
	 * @param record the FileRecord
	 */
	public ASTViewPage(IWorkbenchPart part, FileRecord record) {
		super(part, record);
	}
	
	/**
	 * TODO use an adjustable Sash to separate the two sections
	 * TODO add an XPath version combo widget
	 */
	public void createControl(Composite parent) {
		
		sashForm = new SashForm(parent, SWT.HORIZONTAL);        

		Composite astPanel = new Composite(sashForm, SWT.NONE);
		GridLayout mainLayout = new GridLayout(3, false);
		astPanel.setLayout(mainLayout);
		
			Composite titleArea = new Composite(astPanel, SWT.NONE);
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 2;
			titleArea.setLayoutData(gridData);
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
				methodBtn.setSelection(true);
				methodBtn.addSelectionListener( new SelectionAdapter() {
					public void widgetSelected(SelectionEvent se) {
						methodSelector.setEnabled( methodBtn.getSelection() );
						methodPicked();
					}
				} );
			
			buildMethodSelector(titleArea);

			astViewer = new TreeViewer(astPanel, SWT.MULTI | SWT.BORDER);
			astViewer.setContentProvider( new ASTContentProvider() );
			astViewer.setLabelProvider( new ASTLabelProvider() );
			setupListeners(astViewer.getTree());
	
			GridData data = new GridData(GridData.FILL_BOTH);
			data.horizontalSpan = 2;
			astViewer.getTree().setLayoutData(data);

		//==================
		
		Composite xpathTestPanel = new Composite(sashForm, SWT.NONE);
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		xpathTestPanel.setLayoutData(data);

		GridLayout playLayout = new GridLayout(2, false);
		xpathTestPanel.setLayout(playLayout);

			xpathField = new StyledText(xpathTestPanel, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalSpan = 1;
			xpathField.setLayoutData(gridData);
			SyntaxManager.adapt(xpathField, "xpath", null);
	
			addXPathValidator();
			
			goButton = new Button(xpathTestPanel, SWT.PUSH);
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
			
//			outputField = new StyledText(xpathTestPanel, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalSpan = 2;
//			outputField.setLayoutData(gridData);
//			SyntaxManager.adapt(outputField, "xpath", null);
			
			IStructuredContentProvider contentProvider = new IStructuredContentProvider() {
				public void dispose() {	}
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	}
				public Object[] getElements(Object inputElement) { return (Node[])inputElement;	}        	
		        };
		    BasicTableLabelProvider labelProvider = new BasicTableLabelProvider(NodeColumnUI.VisibleColumns);
		        
		    resultsViewer = new TableViewer(xpathTestPanel, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		    Table table = resultsViewer.getTable();
		    table.setLayoutData(gridData);
		        
		    resultsViewer.setLabelProvider(labelProvider);
		    resultsViewer.setContentProvider(contentProvider);
		    table.setHeaderVisible(true);
		    labelProvider.addColumnsTo(table);
					
			registerListeners();
			
			showFirstMethod();
		}
	
	private void addXPathValidator() {
		
		ModifyListener ml = new ModifyListener() {
	          public void modifyText(ModifyEvent event) {           
	          	validateXPath(xpathField.getText());
	          }
	      };
	      
	      xpathField.addModifyListener(ml);	      
	}
	
	private void validateXPath(String xpathString) {
		
		try {
			new BaseXPath(xpathString, null);
			} catch (Exception ex) {
				// TODO add error marker to editor, red-underlining on offending text
				goButton.setEnabled(false);
				return;
			}

		goButton.setEnabled(true);
	}
	
//	private static void displayOn(Node node, StringBuilder sb) {
//
//		sb.append(node);
//		
//		String imgData = node.getImage();
//		
//		if (imgData != null ) sb.append("  ").append( imgData );
//	}

	private void evaluateXPath() {

		if (! setupTest() ) return;
		
		List<Node> results = null;
		try {
			results = XPathEvaluator.instance.evaluate( 
					getDocument().get(), 
					xpathField.getText(),
					XPathRuleQuery.XPATH_1_0	// TODO derive from future combo widget
					);
		} catch (ParseException pe) {
			showError(pe.fillInStackTrace().getMessage());
			return;
		}
		
		show(results);
	}

	private boolean setupTest() {

	//	outputField.setText("");
		resultsViewer.getTable().clearAll();
		
		if (StringUtil.isEmpty(xpathField.getText())) {
			//outputField.setText("XPath query field is empty.");
			return false;
		}
		
		return true;
	}
	
	private void showError(String message) {
		//outputField.setText(message);
	}
	
	private void show(List<Node> results) {
		
		if (results.isEmpty()) {
			//outputField.setText("No matching nodes found");
			return;
		}
		
//		StringBuilder sb = new StringBuilder();
//		for (int i=0; i<results.size(); i++) {
//			displayOn(results.get(i), i+1, sb);
//			sb.append('\n');
//		}
//		outputField.setText( sb.toString() );
		
		resultsViewer.setInput( results.toArray(new Node[results.size()]) );
	}

	private TextLayout layoutFor(TreeItem item) {

		AbstractNode node = (AbstractNode)item.getData();
		String label = node.toString();

		TextStyle extraStyle = imageStyle;
		String extra = NodeImageDeriver.derivedTextFor(node);
		if (extra != null) {
			extraStyle = derivedStyle;
			} else {
				extra = node.getImage();
				}

		textLayout.setText(label + (extra == null  ? "" : " " + extra));

		int labelLength = label.length();

		textLayout.setStyle(labelStyle, 0, labelLength);
		if (extra != null) {
			textLayout.setStyle(extraStyle, labelLength, labelLength + extra.length() + 1);
		}

		return textLayout;
	}

	private void setupListeners(Tree tree) {

		setupResources(tree.getDisplay());

		tree.addListener(SWT.PaintItem, new Listener() {
			public void handleEvent(Event event) {
				TextLayout layout = layoutFor((TreeItem)event.item);
				layout.draw(event.gc, event.x+5, event.y );
		//		event.gc.drawLine(event.x - 55, event.y, event.x - 55, event.y + 20);
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
		
		// TODO take values from the font/color registries and then adapt to changes
		renderFont = new Font(display, "Tahoma", 10, SWT.NORMAL);
		italicFont = new Font(display, "Tahoma", 10, SWT.ITALIC);
		labelStyle = new TextStyle(renderFont, display.getSystemColor(SWT.COLOR_BLACK), null);
		imageStyle = new TextStyle(renderFont, display.getSystemColor(SWT.COLOR_BLUE), null);
		derivedStyle = new TextStyle(italicFont, display.getSystemColor(SWT.COLOR_GRAY), null);
	}

	public void dispose() {
		super.dispose();
		renderFont.dispose();
		italicFont.dispose();
	}

	public Control getControl() {
		return sashForm;
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
		int index = methodSelector.getSelectionIndex();
		refreshPMDMethods();
		showMethod(index);
		methodSelector.select(index);
		
	}

}
