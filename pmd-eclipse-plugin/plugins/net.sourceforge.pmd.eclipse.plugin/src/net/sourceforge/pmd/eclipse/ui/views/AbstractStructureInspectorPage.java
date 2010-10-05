package net.sourceforge.pmd.eclipse.ui.views;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.cmd.PMDEngine;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.views.ast.ASTUtil;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.designer.DFAGraphRule;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractStructureInspectorPage extends Page implements IPropertyChangeListener, ISelectionChangedListener {

	protected Combo						methodSelector;
	protected FileRecord				resourceRecord;
	protected List<ASTMethodDeclaration> pmdMethodList;

	protected ITextEditor 				textEditor;
	
	protected AbstractStructureInspectorPage(IWorkbenchPart part, FileRecord record) {
		super();

		resourceRecord = record;
		if (part instanceof ITextEditor) {
			textEditor = (ITextEditor) part;
		}
			}
	
	public abstract void refresh(IResource resource);
	
	public void propertyChange(PropertyChangeEvent event) {
		// TODO adapt the editors
		System.out.println("property changed: " + event.getProperty());
	}
	
	public void dispose() {
		super.dispose();
		unregisterListeners();
	}
	
	private static ColorRegistry colorRegistry() {
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
	    ITheme currentTheme = themeManager.getCurrentTheme();
	    return currentTheme.getColorRegistry();
	}
	
	protected void registerListeners() {		
		JFaceResources.getFontRegistry().addListener(this);
		colorRegistry().addListener(this);
	}
	
	protected void unregisterListeners() {		
		JFaceResources.getFontRegistry().removeListener(this);
		colorRegistry().removeListener(this);
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
				logError(StringKeys.ERROR_RUNTIME_EXCEPTION	+ "Exception when selecting a line in the editor", ble);
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
	 * Refreshes the list of PMD methods for the combobox.
	 * @see #getPMDMethods(IResource)
	 */
	protected void refreshPMDMethods() {
	
		methodSelector.removeAll();
		pmdMethodList = getPMDMethods();
	
		for (ASTMethodDeclaration pmdMethod : pmdMethodList) {
			methodSelector.add(ASTUtil.getMethodLabel(pmdMethod, false));
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
		methodSelector = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		refreshPMDMethods();
		methodSelector.setText(getString(StringKeys.VIEW_DATAFLOW_CHOOSE_METHOD));
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
	
			// run PMD using the DFAGraphRule and the Text of the Resource
			new PMDEngine().processFile(reader, rs, ctx);
	
			// the Rule then can give us the Methods
			methodList.addAll(dfaGraphRule.getMethods());
			Collections.sort(methodList, ASTUtil.MethodComparator);
		} catch (PMDException pmde) {
			logError(StringKeys.ERROR_PMD_EXCEPTION + toString(), pmde);
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