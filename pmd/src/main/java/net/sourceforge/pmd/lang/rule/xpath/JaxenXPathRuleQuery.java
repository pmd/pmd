/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.xpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.jaxen.SimpleVariableContext;
import org.jaxen.XPath;
import org.jaxen.expr.AllNodeStep;
import org.jaxen.expr.DefaultXPathFactory;
import org.jaxen.expr.Expr;
import org.jaxen.expr.LocationPath;
import org.jaxen.expr.NameStep;
import org.jaxen.expr.Predicate;
import org.jaxen.expr.Step;
import org.jaxen.expr.UnionExpr;
import org.jaxen.expr.XPathFactory;
import org.jaxen.saxpath.Axis;

/**
 * This is a Jaxen based XPathRule query.
 */
public class JaxenXPathRuleQuery extends AbstractXPathRuleQuery {

    private static final Logger LOG = Logger.getLogger(JaxenXPathRuleQuery.class.getName());

    private static enum InitializationStatus {
	NONE, PARTIAL, FULL
    };

    // Mapping from Node name to applicable XPath queries
    private InitializationStatus initializationStatus = InitializationStatus.NONE;
    private Map<String, List<XPath>> nodeNameToXPaths;

    private static final String AST_ROOT = "_AST_ROOT_";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupportedVersion(String version) {
	return XPATH_1_0.equals(version);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Node> evaluate(Node node, RuleContext data) {
	List<Node> results = new ArrayList<Node>();
	try {
	    initializeXPathExpression(data.getLanguageVersion().getLanguageVersionHandler().getXPathHandler()
		    .getNavigator());
	    List<XPath> xpaths = nodeNameToXPaths.get(node.toString());
	    if (xpaths == null) {
		xpaths = nodeNameToXPaths.get(AST_ROOT);
	    }
	    for (XPath xpath : xpaths) {
		List<Node> nodes = xpath.selectNodes(node);
		results.addAll(nodes);
	    }
	} catch (JaxenException ex) {
	    throw new RuntimeException(ex);
	}
	return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getRuleChainVisits() {
	try {
	    // No Navigator available in this context
	    initializeXPathExpression(null);
	    return super.getRuleChainVisits();
	} catch (JaxenException ex) {
	    throw new RuntimeException(ex);
	}
    }

    @SuppressWarnings("unchecked")
    private void initializeXPathExpression(Navigator navigator) throws JaxenException {
	if (initializationStatus == InitializationStatus.FULL) {
	    return;
	} else if (initializationStatus == InitializationStatus.PARTIAL && navigator == null) {
	    LOG.severe("XPathRule is not initialized because no navigator was provided. "
	            + "Please make sure to implement getXPathHandler in the handler of the language. "
	            + "See also AbstractLanguageVersionHandler.");
	    return;
	}

	//
	// Attempt to use the RuleChain with this XPath query.  To do so, the queries
	// should generally look like //TypeA or //TypeA | //TypeB.  We will look at the
	// parsed XPath AST using the Jaxen APIs to make this determination.
	// If the query is not exactly what we are looking for, do not use the RuleChain.
	//
	nodeNameToXPaths = new HashMap<String, List<XPath>>();

	BaseXPath originalXPath = createXPath(xpath, navigator);
	indexXPath(originalXPath, AST_ROOT);

	boolean useRuleChain = true;
	Stack<Expr> pending = new Stack<Expr>();
	pending.push(originalXPath.getRootExpr());
	while (!pending.isEmpty()) {
	    Expr node = pending.pop();

	    // Need to prove we can handle this part of the query
	    boolean valid = false;

	    // Must be a LocationPath... that is something like //Type
	    if (node instanceof LocationPath) {
		LocationPath locationPath = (LocationPath) node;
		if (locationPath.isAbsolute()) {
		    // Should be at least two steps
		    List<Step> steps = locationPath.getSteps();
		    if (steps.size() >= 2) {
			Step step1 = steps.get(0);
			Step step2 = steps.get(1);
			// First step should be an AllNodeStep using the descendant or self axis
			if (step1 instanceof AllNodeStep && ((AllNodeStep) step1).getAxis() == Axis.DESCENDANT_OR_SELF) {
			    // Second step should be a NameStep using the child axis.
			    if (step2 instanceof NameStep && ((NameStep) step2).getAxis() == Axis.CHILD) {
				// Construct a new expression that is appropriate for RuleChain use
				XPathFactory xpathFactory = new DefaultXPathFactory();

				// Instead of an absolute location path, we'll be using a relative path
				LocationPath relativeLocationPath = xpathFactory.createRelativeLocationPath();
				// The first step will be along the self axis
				Step allNodeStep = xpathFactory.createAllNodeStep(Axis.SELF);
				// Retain all predicates from the original name step
				for (Iterator<Predicate> i = step2.getPredicates().iterator(); i.hasNext();) {
				    allNodeStep.addPredicate(i.next());
				}
				relativeLocationPath.addStep(allNodeStep);

				// Retain the remaining steps from the original location path
				for (int i = 2; i < steps.size(); i++) {
				    relativeLocationPath.addStep(steps.get(i));
				}

				BaseXPath xpath = createXPath(relativeLocationPath.getText(), navigator);
				indexXPath(xpath, ((NameStep) step2).getLocalName());
				valid = true;
			    }
			}
		    }
		}
	    } else if (node instanceof UnionExpr) { // Or a UnionExpr, that is something like //TypeA | //TypeB
		UnionExpr unionExpr = (UnionExpr) node;
		pending.push(unionExpr.getLHS());
		pending.push(unionExpr.getRHS());
		valid = true;
	    }
	    if (!valid) {
		useRuleChain = false;
		break;
	    }
	}

	if (useRuleChain) {
	    // Use the RuleChain for all the nodes extracted from the xpath queries
	    super.ruleChainVisits.addAll(nodeNameToXPaths.keySet());
	} else {
	    // Use original XPath if we cannot use the RuleChain
	    nodeNameToXPaths.clear();
	    indexXPath(originalXPath, AST_ROOT);
	    if (LOG.isLoggable(Level.FINE)) {
		LOG.log(Level.FINE, "Unable to use RuleChain for for XPath: " + xpath);
	    }
	}

	if (navigator == null) {
	    this.initializationStatus = InitializationStatus.PARTIAL;
	    // Clear the node data, because we did not have a Navigator
	    nodeNameToXPaths = null;
	} else {
	    this.initializationStatus = InitializationStatus.FULL;
	}

    }

    private void indexXPath(XPath xpath, String nodeName) {
	List<XPath> xpaths = nodeNameToXPaths.get(nodeName);
	if (xpaths == null) {
	    xpaths = new ArrayList<XPath>();
	    nodeNameToXPaths.put(nodeName, xpaths);
	}
	xpaths.add(xpath);
    }

    private BaseXPath createXPath(String xpathQueryString, Navigator navigator) throws JaxenException {

    	BaseXPath xpath = new BaseXPath(xpathQueryString, navigator);
    	if (properties.size() > 1) {
    		SimpleVariableContext vc = new SimpleVariableContext();
    		for (Entry<PropertyDescriptor<?>, Object> e : properties.entrySet()) {
    			String propName = e.getKey().name();
    			if (!"xpath".equals(propName)) {
    				Object value = e.getValue();
    				vc.setVariableValue(propName, value != null ? value.toString() : null);
    			}
    		}
    		xpath.setVariableContext(vc);
    	}
    	return xpath;
    }
}
