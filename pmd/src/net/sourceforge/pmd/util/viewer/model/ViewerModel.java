package net.sourceforge.pmd.util.viewer.model;

import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import java.io.StringReader;
import java.util.List;
import java.util.Vector;


/**
 * The model for the viewer gui
 * <p/>
 * <p/>
 * This is the model part of MVC
 * </p>
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class ViewerModel {
    private Vector listeners;
    private SimpleNode rootNode;
    private List evaluationResults;

    /**
     * constructs the model
     */
    public ViewerModel() {
        listeners = new Vector(5);
    }

    /**
     * Retrieves AST's root node
     *
     * @return AST's root node
     */
    public SimpleNode getRootNode() {
        return rootNode;
    }

    /**
     * commits source code to the model.
     * <p/>
     * <p/>
     * all existing source will be replaced
     * </p>
     *
     * @param source source to be commited
     */
    public void commitSource(String source) {
        ASTCompilationUnit compilationUnit = new TargetJDK1_4().createParser(new StringReader(source)).CompilationUnit();
        rootNode = compilationUnit;
        fireViewerModelEvent(new ViewerModelEvent(this, ViewerModelEvent.CODE_RECOMPILED));
    }

    /**
     * determines wheteher the model has a compiled tree at it's disposal
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

    /**
     * adds a listener to the model
     *
     * @param l listener to be added
     */
    public void addViewerModelListener(ViewerModelListener l) {
        listeners.add(l);
    }

    /**
     * removes the lisetener from the model
     *
     * @param l listener to be removed
     */
    public void removeViewerModelListener(ViewerModelListener l) {
        listeners.remove(l);
    }

    /**
     * notifes all listener of a change in the model
     *
     * @param e change's reason
     */
    protected void fireViewerModelEvent(ViewerModelEvent e) {
        for (int i = 0; i < listeners.size(); i++) {
            ((ViewerModelListener) listeners.elementAt(i)).viewerModelChanged(e);
        }
    }
}


/*
 * $Log$
 * Revision 1.5  2004/09/27 19:42:52  tomcopeland
 * A ridiculously large checkin, but it's all just code reformatting.  Nothing to see here...
 *
 * Revision 1.4  2004/04/12 17:35:09  tomcopeland
 * Cleaned up imports
 *
 * Revision 1.3  2004/04/12 17:23:29  tomcopeland
 * Moving all explicit JavaParser creations over to a factory-ish sort of thing.  This makes the version of the parser explicit rather than assumed.
 *
 * Revision 1.2  2003/09/23 20:51:06  tomcopeland
 * Cleaned up imports
 *
 * Revision 1.1  2003/09/23 20:32:42  tomcopeland
 * Added Boris Gruschko's new AST/XPath viewer
 *
 * Revision 1.1  2003/09/24 01:33:03  bgr
 * moved to a new package
 *
 * Revision 1.3  2003/09/24 00:40:35  bgr
 * evaluation results browsing added
 *
 * Revision 1.2  2003/09/23 07:52:16  bgr
 * menus added
 *
 * Revision 1.1  2003/09/22 05:21:54  bgr
 * initial commit
 *
 */
