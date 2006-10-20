package net.sourceforge.pmd.util.viewer.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.TargetJDKVersion;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.DocumentNavigator;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

public class ViewerModel {
	
    private List		listeners;
    private SimpleNode	rootNode;
    private List		evaluationResults;

    public ViewerModel() {
        listeners = new ArrayList(5);
    }

    public SimpleNode getRootNode() {
        return rootNode;
    }

    /**
     * commits source code to the model.
     * all existing source will be replaced
     */
    public void commitSource(String source, TargetJDKVersion jdk) {
        ASTCompilationUnit compilationUnit = jdk.createParser(new StringReader(source)).CompilationUnit();
        rootNode = compilationUnit;
        fireViewerModelEvent(new ViewerModelEvent(this, ViewerModelEvent.CODE_RECOMPILED));
    }

    /**
     * determines whether the model has a compiled tree at it's disposal
     *
     * @return true if there is an AST, false otherwise
     */
    public boolean hasCompiledTree() {
        return rootNode != null;
    }

    /**
     * evaluates the given XPath expression against the current tree
     *
     * @param xPath     XPath expression to be evaluated
     * @param evaluator object which requests the evaluation
     */
    public void evaluateXPathExpression(String xPath, Object evaluator)
            throws ParseException, JaxenException {
        XPath xpath = new BaseXPath(xPath, new DocumentNavigator());
        evaluationResults = xpath.selectNodes(rootNode);
        fireViewerModelEvent(new ViewerModelEvent(evaluator, ViewerModelEvent.PATH_EXPRESSION_EVALUATED));
    }

    /**
     * retrieves the results of last evaluation
     *
     * @return a list containing the nodes selected by the last XPath expression
     *         <p/>
     *         evaluation
     */
    public List getLastEvaluationResults() {
        return evaluationResults;
    }

    /**
     * selects the given node in the AST
     *
     * @param node     node to be selected
     * @param selector object which requests the selection
     */
    public void selectNode(SimpleNode node, Object selector) {
        fireViewerModelEvent(new ViewerModelEvent(selector, ViewerModelEvent.NODE_SELECTED, node));
    }

    /**
     * appends the given fragment to the XPath expression
     *
     * @param pathFragment fragment to be added
     * @param appender     object that is trying to append the fragment
     */
    public void appendToXPathExpression(String pathFragment, Object appender) {
        fireViewerModelEvent(new ViewerModelEvent(appender, ViewerModelEvent.PATH_EXPRESSION_APPENDED, pathFragment));
    }

    public void addViewerModelListener(ViewerModelListener l) {
        listeners.add(l);
    }

    public void removeViewerModelListener(ViewerModelListener l) {
        listeners.remove(l);
    }

    protected void fireViewerModelEvent(ViewerModelEvent e) {
        for (int i = 0; i < listeners.size(); i++) {
            ((ViewerModelListener) listeners.get(i)).viewerModelChanged(e);
        }
    }
}
