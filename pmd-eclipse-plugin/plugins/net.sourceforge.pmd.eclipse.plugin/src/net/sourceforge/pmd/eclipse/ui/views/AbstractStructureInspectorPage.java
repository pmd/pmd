package net.sourceforge.pmd.eclipse.ui.views;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.runtime.cmd.PMDEngine;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.designer.DFAGraphRule;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractStructureInspectorPage extends Page implements IPropertyListener, ISelectionChangedListener {

	protected Combo						methodSelector;
	protected FileRecord				resourceRecord;
	protected List<ASTMethodDeclaration> pmdMethodList;

	protected ITextEditor 				textEditor;
	
	public static String parameterTypes(ASTMethodDeclaration node) {
	
		StringBuilder sb = new StringBuilder();
		
		for (int ix = 0; ix < node.jjtGetNumChildren(); ix++) {
		    Node sn = node.jjtGetChild(ix);
	    	if (sn instanceof ASTMethodDeclarator) {
	    		List<ASTFormalParameter> allParams = ((ASTMethodDeclarator) sn).findDescendantsOfType(ASTFormalParameter.class);
	    		for (ASTFormalParameter formalParam : allParams) {
	    		    AbstractNode param = formalParam.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
	    		    if (param == null) {
	    		    	param = formalParam.getFirstDescendantOfType(ASTPrimitiveType.class);
	    		    	}
	    		    if (param == null) continue;
	    	    	sb.append( param.getImage() ).append(", ");
	    		}	
	    	}
		}
		
		int length = sb.length();
		return length == 0 ? "" : sb.toString().substring(0, length-2);
	}

	protected AbstractStructureInspectorPage(IWorkbenchPart part, FileRecord record) {
		super();

		resourceRecord = record;
		if (part instanceof ITextEditor) {
			textEditor = (ITextEditor) part;
		}
	}
	
	public void setFocus() {
		methodSelector.setFocus();
	}

	/**
	 * Shows the method that belongs to a violation (to a line).
	 * @param violation RuleViolation
	 */
	protected void showMethodToViolation(RuleViolation violation) {
		final int beginLine = violation.getBeginLine();

		for (int i = 0; i < pmdMethodList.size(); i++) {
			ASTMethodDeclaration pmdMethod = pmdMethodList.get(i);
			if (beginLine >= pmdMethod.getBeginLine() && beginLine <= pmdMethod.getEndLine()) {
				showMethod(pmdMethod);
				// select the method in the combobox
				methodSelector.select(i);
				return;
			}
		}
	}
	
	protected RuleViolation selectedViolationFrom(SelectionChangedEvent event) {

		if (event.getSelection() instanceof IStructuredSelection) {
			final Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();
			return element instanceof RuleViolation ?
				(RuleViolation) element : null;
		}

		return null;	// should never happen
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
				int length = getDocument().getLineOffset(violation.getEndLine())	- offset;
				textEditor.selectAndReveal(offset, length);
			} catch (BadLocationException ble) {
				logError(StringKeys.MSGKEY_ERROR_RUNTIME_EXCEPTION	+ "Exception when selecting a line in the editor", ble);
			}

			// showMethodToMarker(marker);
			showMethodToViolation(violation);

			// then we calculate and color _a possible_ Path
			// for this Error in the Dataflow
			//			final DataflowGraph graph = astViewer.getGraph();
			//			if (!astViewer.isDisposed() && graph != null) {
			//				graph.markPath(beginLine, endLine, varName);
			//			}
		}
	}
	
	/**
	 * If the review is ready propertyChanged with the results will be called.
	 */
	public void propertyChanged(Object source, int propId) {
		if (source instanceof Iterator<?>
		&& propId == PMDRuntimeConstants.PROPERTY_REVIEW) {
			//          tableViewer.setInput(source);
		}
	}
	
	/**
	 * Gets the label of a method for an element of the combobox.
	 * @param pmdMethod the method to create a label for
	 * @return a label for the method
	 */
	String getMethodLabel(ASTMethodDeclaration pmdMethod) {
		return pmdMethod.getMethodName() + "(" + parameterTypes(pmdMethod) + ")";
	}
	
	/**
	 * Refreshes the list of PMD methods for the combobox.
	 * @see #getPMDMethods(IResource)
	 */
	protected void refreshPMDMethods() {
	
		methodSelector.removeAll();
		pmdMethodList = getPMDMethods();
	
		for (ASTMethodDeclaration pmdMethod : pmdMethodList) {
			methodSelector.add(getMethodLabel(pmdMethod));
		}
	}

	public void showFirstMethod() {
		methodSelector.select(0);
		showMethod(0);
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
	protected void showMethod(int index) {
		if (index >= 0 && index < pmdMethodList.size() ) {
			ASTMethodDeclaration method = pmdMethodList.get(index);
			showMethod(method);
		}
	}

	protected abstract void showMethod(ASTMethodDeclaration pmdMethod);
	
	/**
	 * Helper method to return an NLS string from its key.
	 */
	protected static String getString(String key) {
		return PMDPlugin.getDefault().getStringTable().getString(key);
	}

	protected void buildMethodSelector(Composite parent) {
		// the drop down box for showing all methods of the given resource
		methodSelector = new Combo(parent, SWT.LEFT | SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		refreshPMDMethods();
		methodSelector.setText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_CHOOSE_METHOD));
		methodSelector.setLayoutData(new GridData(300, SWT.DEFAULT));
		methodSelector.addSelectionListener( new SelectionAdapter() {
			
			/* @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent) */
			public void widgetDefaultSelected(SelectionEvent e) {
				if (methodSelector.equals(e.widget)) {
					showMethod(methodSelector.getSelectionIndex());
				}
			}
			
			public void widgetSelected(SelectionEvent e) {				
				methodPicked();
			}
		});
	}
	
	public void methodPicked() {
		int index = methodSelector.getSelectionIndex();
		methodSelector.setSelection(new Point(0,0));
		showMethod(index);
	}
	
	/**
	 * Gets a List of all PMD-Methods.
	 *
	 * @return an List of ASTMethodDeclarations
	 */
	private List<ASTMethodDeclaration> getPMDMethods() {
		
		List<ASTMethodDeclaration> methodList = new ArrayList<ASTMethodDeclaration>();
	
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
			new PMDEngine().processFile(reader, rs, ctx);
	
			// the Rule then can give us the Methods
			methodList.addAll(dfaGraphRule.getMethods());
		} catch (PMDException pmde) {
			logError(StringKeys.MSGKEY_ERROR_PMD_EXCEPTION + this.toString(), pmde);
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

	public static void logError(String message, Throwable error) {
		PMDPlugin.getDefault().logError(message, error);
	}

	public static void logErrorByKey(String messageId, Throwable error) {
		PMDPlugin.getDefault().logError(getString(messageId), error);
	}

}