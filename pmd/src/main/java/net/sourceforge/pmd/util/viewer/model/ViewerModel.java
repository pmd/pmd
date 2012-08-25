package net.sourceforge.pmd.util.viewer.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.DocumentNavigator;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ParseException;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

public class ViewerModel {

    private List<ViewerModelListener> listeners;
    private Node rootNode;
    private List<Node> evaluationResults;

    public ViewerModel() {
	listeners = new ArrayList<ViewerModelListener>(5);
    }

    public Node getRootNode() {
	return rootNode;
    }

    /**
     * commits source code to the model.
     * all existing source will be replaced
     */
    public void commitSource(String source, LanguageVersion languageVersion) {
	LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
	ASTCompilationUnit compilationUnit = (ASTCompilationUnit) languageVersionHandler
		.getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(source));
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
    public void evaluateXPathExpression(String xPath, Object evaluator) throws ParseException, JaxenException {
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
    public List<Node> getLastEvaluationResults() {
	return evaluationResults;
    }

    /**
     * selects the given node in the AST
     *
     * @param node     node to be selected
     * @param selector object which requests the selection
     */
    public void selectNode(Node node, Object selector) {
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
	    listeners.get(i).viewerModelChanged(e);
	}
    }
}
