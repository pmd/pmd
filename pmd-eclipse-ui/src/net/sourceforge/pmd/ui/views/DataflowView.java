package net.sourceforge.pmd.ui.views;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.util.designer.DFAGraphRule;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

/**
 * A View that shows DataflowGraph and -Table as well as the Anomaly-List
 * 
 * @author SebastianRaffel ( 26.05.2005 )
 */
public class DataflowView extends ViewPart implements ISelectionChangedListener, IPartListener {

    private IResource resource;
    private IMethod javaMethod;
    private ASTMethodDeclaration pmdMethod;
    private IWorkbenchPart activePart;

    private Composite dfaFrame;
    private Label titleLabel;
    private Button switchButton;

    protected Composite titleArea;
    protected DataflowGraphViewer graphViewer;
    protected DataflowAnomalyTableViewer tableViewer;
    protected boolean isTableShown;

    /* @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite) */
    public void createPartControl(Composite parent) {
        dfaFrame = new Composite(parent, SWT.NONE);

        // //////////////////////////////////////////////////
        // the upper Title Area

        titleArea = new Composite(dfaFrame, SWT.NONE);
        GridData tableData = new GridData(GridData.FILL_HORIZONTAL);
        tableData.horizontalSpan = 2;
        titleArea.setLayoutData(tableData);

        // the Label showing the Title of the Method
        titleLabel = new Label(titleArea, SWT.LEFT);
        titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        titleLabel.setText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_DEFAULT_TEXT));

        // the Button for showing or hiding the Anomaly-List
        switchButton = new Button(titleArea, SWT.RIGHT);
        switchButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                isTableShown = !isTableShown;
                showTableArea(isTableShown);

                if (isTableShown == false) {
                    if ((graphViewer == null) || (graphViewer.getGraph() == null))
                        return;

                    DataflowGraph graph = graphViewer.getGraph();
                    if (graph.isMarked())
                        graph.demark();
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        switchButton.setText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_SWITCHBUTTON_SHOW));
        switchButton.setVisible(false);

        titleArea.setLayout(new GridLayout(2, false));

        // //////////////////////////////////////////////////
        // the DataflowGraphViewer (left Part)
        graphViewer = new DataflowGraphViewer(dfaFrame, SWT.NONE);
        graphViewer.setVisible(false);
        graphViewer.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                showTableArea(isTableShown);
            }
        });

        // //////////////////////////////////////////////////
        // the DataflowAnomalyTable (right Part)
        tableViewer = new DataflowAnomalyTableViewer(dfaFrame, SWT.BORDER, this);
        isTableShown = false;
        tableViewer.setVisible(false);

        GridLayout mainLayout = new GridLayout(2, true);
        mainLayout.horizontalSpacing = mainLayout.verticalSpacing = 7;
        mainLayout.marginWidth = mainLayout.marginHeight = 3;
        dfaFrame.setLayout(mainLayout);

        getViewSite().getPage().addPartListener(this);
    }

    /* @see org.eclipse.ui.part.WorkbenchPart#setFocus() */
    public void setFocus() {
        dfaFrame.setFocus();
    }

    /* @see org.eclipse.ui.part.WorkbenchPart#dispose() */
    public void dispose() {
        if (activePart != null) {
            ISelectionProvider provider = activePart.getSite().getSelectionProvider();
            if (provider != null)
                provider.removeSelectionChangedListener(this);
        }
        super.dispose();
    }

    /**
     * Shows the DataflowGraph (and Dataflow-Anomalies) for a Java-Method
     * 
     * @param method
     */
    public void showMethod(IMethod method) {
        javaMethod = method;

        // get the corresponding pmdMethod to the Java-Method
        pmdMethod = getPMDMethodFromJavaMethod(method);

        if (pmdMethod != null) {
            String resourceString = getResourceAsString(method.getResource());
            titleLabel.setText(getMethodName(resourceString, pmdMethod.getBeginLine()));

            // give the Data to the GraphViewer
            graphViewer.setVisible(true);
            graphViewer.setData(pmdMethod, resourceString);

            // give the data to the TableViewer
            showTableArea(isTableShown);
            tableViewer.setData(pmdMethod, javaMethod);

            switchButton.setVisible(true);
        } else {
            // if no pmdMethod could be found, it is a Constructor
            // or no valid Java-Method, so we show some text
            titleLabel.setText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_DEFAULT_TEXT));
            graphViewer.setVisible(false);
            tableViewer.setVisible(false);
            switchButton.setVisible(false);
        }
    }

    /**
     * Returns the ASTMethoddeclaration of the PMD-Model that corresponds to a given Java-Method
     * 
     * @param method
     * @return a PMD-Method
     */
    private ASTMethodDeclaration getPMDMethodFromJavaMethod(IMethod method) {
        ASTMethodDeclaration pmdMethod;

        // check, if the Method is null or a Constructor
        // (PMD does not see Constructors as Methods)
        try {
            if (method == null)
                return null;
            else if (method.isConstructor())
                return null;
        } catch (JavaModelException jme) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_JAVAMODEL_EXCEPTION + this.toString(), jme);
        }

        String resourceString = getResourceAsString(method.getResource());
        // get a List of all PMD-Methods found in the Resource
        ArrayList pmdMethodList = getPMDMethods(method.getResource());

        for (int i = 0; i < pmdMethodList.size(); i++) {
            pmdMethod = (ASTMethodDeclaration) pmdMethodList.get(i);

            // search for the Code-Line the PMD-Method starts with
            String beginLine = getBeginLine(resourceString, pmdMethod.getBeginLine());
            if (beginLine == null)
                continue;

            // compare the Line with the Attributes of the Java-Method
            // if the Name and Number and Type of Params of the Method
            // are equal to those found in the String,
            // it must be the right Method
            if (methodBeginsWithLine(method, beginLine)) {
                return pmdMethod;
            }
        }

        return null;
    }

    /**
     * Gets a List of all PMD-Methods
     * 
     * @param resource
     * @return an ArrayList of ASTMethodDeclarations
     */
    private ArrayList getPMDMethods(IResource resource) {
        String resourceString = getResourceAsString(resource);
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

            StringReader reader = new StringReader(resourceString);

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
     * Returns a Resource (File) as a String
     * 
     * @param resource
     * @return the Content of the File
     */
    protected String getResourceAsString(IResource resource) {
        String fileContents = "";
        try {
            FileReader fileReader = new FileReader(resource.getRawLocation().toFile());
            BufferedReader bReader = new BufferedReader(fileReader);

            // read the File line-wise
            while (bReader.ready()) {
                fileContents += bReader.readLine() + "\n";
            }
        } catch (FileNotFoundException fnfe) {
            PMDUiPlugin.getDefault().logError(
                    StringKeys.MSGKEY_ERROR_IO_EXCEPTION + resource.getRawLocation().toString() + " in " + this.toString(), fnfe);
        } catch (IOException ioe) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_IO_EXCEPTION + this.toString(), ioe);
        }

        return fileContents;
    }

    /**
     * Checks, if an IMethod begins with a certain Line
     * 
     * @param method
     * @param line
     * @return true, if the Method starts with this Line, false otherwise
     */
    protected boolean methodBeginsWithLine(IMethod method, String line) {
        String methodName = method.getElementName();
        String tempString;

        // generally a Method consists of:
        // .... + name +"("+ paramType +" "+ paramName +", "+ ...
        // e.g.:
        // public int function ( int a, int b ) { ...
        // /\
        // this is the given Line

        // split the Line, first Part with Name
        // second Part are the Parameters
        String[] stringParts = line.split("\\(");
        tempString = stringParts[0].trim();

        // trim and cut of everything before the last string
        // which is the Name of the Method
        String stringName = tempString.substring(tempString.lastIndexOf(" ")).trim();

        // compare the Names
        if (!methodName.equalsIgnoreCase(stringName))
            return false;

        // get the Java-Method's Param-Types
        String[] methodParamTypes = method.getParameterTypes();

        // trim the second Part till ")"
        tempString = stringParts[1].trim();
        String stringParamList = tempString.substring(0, tempString.lastIndexOf(")")).trim();

        // empty paramArray
        String[] stringParamArray = {};
        
        // everything between is a ","-seperated List of Params
        if (stringParamList.length() > 0) {
            stringParamArray = stringParamList.split(",");
        }

        // compare the number of Params (if there are Params)
        if ((methodParamTypes.length == 0) && (stringParamList.length() == 0))
            return true;
   
        if (stringParamArray.length != methodParamTypes.length)
            return false;

        // now compare the Param-Types
        for (int k = 0; k < stringParamArray.length; k++) {
            tempString = stringParamArray[k].trim();

            // get the first Part of each String
            // which is the Type like "int" or so
            String paramString = tempString.substring(0, tempString.indexOf(" "));

            // get the Type from the JavaMethod
            // this is a so called Signature,
            // like "QString;" for String or "I" for "int"

            // get it as a String and compare the two
            String type = Signature.toString(methodParamTypes[k]);
            if (!paramString.equalsIgnoreCase(type))
                return false;
        }

        return true;
    }

    /**
     * Gets a Line with the given Number from a String, used to get the Methods beginning Line
     * 
     * @param data
     * @param line
     * @return the Line from the String
     */
    protected String getBeginLine(String data, int line) {
        try {
            LineNumberReader reader = new LineNumberReader(new StringReader(data));
            String retString;

            // read the File till the Line is reached
            while (reader.ready()) {
                retString = reader.readLine();

                // Method-Beginnings can be declared in
                // multiple Lines, because of Whitespaces

                // cut of the WhiteSpaces and search till "{"
                // is reached
                if (reader.getLineNumber() == line) {
                    while (retString.lastIndexOf("{") == -1) {
                        retString += reader.readLine().trim();
                    }
                    return retString;
                }
            }
        } catch (IOException ioe) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_IO_EXCEPTION + this.toString(), ioe);
        }

        return null;
    }

    /**
     * Reads a String until a Line, wehere a Method should begin and determines this Method's Name
     * 
     * @param fileString
     * @param beginLine
     * @return the Name of the Method beginning is the given Line
     */
    protected String getMethodName(String fileString, int beginLine) {
        String methodName = "";
        methodName = getBeginLine(fileString, beginLine);
        methodName = methodName.substring(0, methodName.lastIndexOf(")") + 1).trim();

        return methodName;
    }

    /**
     * Shows or hides the DataflowAnomalyTable, calculates the right Positions
     * 
     * @param isShown, true if the Table should be visible, false otherwise
     */
    protected void showTableArea(boolean isShown) {
        tableViewer.setVisible(isShown);

        // to avoid a little SWT Error, that returns (0,0)
        // when asked for the size of a Component at the wrong time
        if (dfaFrame.getSize().equals(new Point(0, 0)))
            return;

        // calculate the Width and Height of the GraphArea
        // if the AnomalyTable is visible, an Area is 50% of the View

        GridLayout mainLayout = ((GridLayout) dfaFrame.getLayout());
        int graphWidth = dfaFrame.getSize().x - 2 * mainLayout.marginWidth;
        int graphHeight = dfaFrame.getSize().y - titleArea.getSize().y - 2 * mainLayout.marginHeight - mainLayout.verticalSpacing;

        // set the new Size and update the SwitchButton's Label
        if (isShown) {
            ((GridData) graphViewer.getLayoutData()).horizontalSpan = 1;
            switchButton.setText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_SWITCHBUTTON_HIDE));
            graphViewer.setSize((graphWidth - mainLayout.horizontalSpacing) / 2, graphHeight);
        } else {
            ((GridData) graphViewer.getLayoutData()).horizontalSpan = 2;
            switchButton.setText(getString(StringKeys.MSGKEY_VIEW_DATAFLOW_SWITCHBUTTON_SHOW));
            graphViewer.setSize(graphWidth, graphHeight);
        }

        // lay out to update the View
        dfaFrame.layout(true, true);
    }

    /* @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent) */
    public void selectionChanged(SelectionChangedEvent event) {

        // get the Graph
        if (graphViewer.isDisposed())
            return;
        if (graphViewer.getGraph() == null)
            return;
        DataflowGraph graph = graphViewer.getGraph();

        // get the Selection
        if (!(event.getSelection() instanceof IStructuredSelection))
            return;
        Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();

        if (element instanceof IMethod) {
            // this Selection either is an IMethod selected
            // in the Package Explorer or Outline
            showMethod((IMethod) element);
        } else if (element instanceof IMarker) {
            // ... or it is a Marker for a Dataflow-Anomaly
            IMarker marker = (IMarker) element;
            if (marker != null) {
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
                graph.markPath(line1, line2, varName);
            }
        }
    }

    /* @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart) */
    public void partActivated(IWorkbenchPart part) {
        if (part != null) {
            activePart = part;
            ISelectionProvider provider = part.getSite().getSelectionProvider();
            if (provider != null)
                provider.addSelectionChangedListener(this);
        }
    }

    /* @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart) */
    public void partBroughtToTop(IWorkbenchPart part) {
        partActivated(part);
    }

    /* @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart) */
    public void partClosed(IWorkbenchPart part) {
        ISelectionProvider provider = part.getSite().getSelectionProvider();
        if (provider != null)
            provider.removeSelectionChangedListener(this);
        activePart = null;
    }

    /* @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart) */
    public void partDeactivated(IWorkbenchPart part) {
        partClosed(part);
    }

    /* @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart) */
    public void partOpened(IWorkbenchPart part) {
    }

    /**
     * Helper method to return an NLS string from its key
     */
    private String getString(String key) {
        return PMDUiPlugin.getDefault().getStringTable().getString(key);
    }
}
