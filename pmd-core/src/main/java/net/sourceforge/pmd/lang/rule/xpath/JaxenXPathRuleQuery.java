/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.internal.ContextualizedNavigator;
import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * This is a Jaxen based XPathRule query.
 *
 * @deprecated Internal API
 */
@Deprecated
@InternalApi
public class JaxenXPathRuleQuery extends AbstractXPathRuleQuery {

    private static final Logger LOG = Logger.getLogger(JaxenXPathRuleQuery.class.getName());

    static final String AST_ROOT = "_AST_ROOT_";

    private InitializationStatus initializationStatus = InitializationStatus.NONE;
    // Mapping from Node name to applicable XPath queries
    Map<String, List<XPath>> nodeNameToXPaths;

    private final DeprecatedAttrLogger attrCtx;

    public JaxenXPathRuleQuery() {
        this(DeprecatedAttrLogger.noop());
    }

    public JaxenXPathRuleQuery(DeprecatedAttrLogger attrCtx) {
        this.attrCtx = attrCtx;
    }

    @Override
    public boolean isSupportedVersion(String version) {
        return XPATH_1_0.equals(version);
    }

    @Override
    public List<Node> evaluate(final Node node, final RuleContext data) {
        final List<Node> results = new ArrayList<>();

        try {
            initializeExpressionIfStatusIsNoneOrPartial(new ContextualizedNavigator(attrCtx));

            List<XPath> xPaths = getXPathsForNodeOrDefault(node.getXPathNodeName());
            for (XPath xpath : xPaths) {
                @SuppressWarnings("unchecked")
                final List<Node> matchedNodes = xpath.selectNodes(node);
                results.addAll(matchedNodes);
            }
        } catch (final JaxenException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    /**
     * Get the XPath queries associated with the node name. If there are none, the XPath queries for the {@link #AST_ROOT}
     * are obtained.
     *
     * @param nodeName the id of the node
     * @return the list of XPath queries that match the node name
     */
    private List<XPath> getXPathsForNodeOrDefault(final String nodeName) {
        List<XPath> xPaths = nodeNameToXPaths.get(nodeName);
        if (xPaths == null) {
            xPaths = nodeNameToXPaths.get(AST_ROOT);
        }
        return xPaths;
    }

    @Override
    public List<String> getRuleChainVisits() {
        try {
            // No Navigator available in this context
            initializeExpressionIfStatusIsNoneOrPartial(null);
            return super.getRuleChainVisits();
        } catch (final JaxenException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     *
     * @param navigator the navigator which is required to be non-null if the {@link #initializationStatus} is PARTIAL.
     * @throws JaxenException
     */
    @SuppressWarnings("unchecked")
    private void initializeExpressionIfStatusIsNoneOrPartial(final Navigator navigator) throws JaxenException {
        if (initializationStatus == InitializationStatus.FULL) {
            return;
        }
        if (initializationStatus == InitializationStatus.PARTIAL && navigator == null) {
            LOG.severe("XPathRule is not initialized because no navigator was provided. "
                    + "Please make sure to implement getXPathHandler in the handler of the language. "
                    + "See also AbstractLanguageVersionHandler.");
            return;
        }
        initializeXPathExpression(navigator);
    }

    private void initializeXPathExpression(final Navigator navigator) throws JaxenException {
        /*
        Attempt to use the RuleChain with this XPath query.

        To do so, the queries should generally look like //TypeA or //TypeA | //TypeB. We will look at the parsed XPath
        AST using the Jaxen APIs to make this determination.

        If the query is not exactly what we are looking for, do not use the
        RuleChain.
        */
        nodeNameToXPaths = new HashMap<>();

        final BaseXPath originalXPath = createXPath(xpath, navigator);
        addQueryToNode(originalXPath, AST_ROOT);

        boolean useRuleChain = true;
        final Deque<Expr> pending = new ArrayDeque<>();
        pending.push(originalXPath.getRootExpr());
        while (!pending.isEmpty()) {
            final Expr node = pending.pop();

            // Need to prove we can handle this part of the query
            boolean valid = false;

            // Must be a LocationPath... that is something like //Type
            if (node instanceof LocationPath) {
                final LocationPath locationPath = (LocationPath) node;
                if (locationPath.isAbsolute()) {
                    // Should be at least two steps
                    @SuppressWarnings("unchecked")
                    final List<Step> steps = locationPath.getSteps();

                    if (steps.size() >= 2) {
                        final Step step1 = steps.get(0);
                        final Step step2 = steps.get(1);
                        // First step should be an AllNodeStep using the
                        // descendant or self axis
                        if (step1 instanceof AllNodeStep
                                && step1.getAxis() == Axis.DESCENDANT_OR_SELF) {
                            // Second step should be a NameStep using the child
                            // axis.
                            if (step2 instanceof NameStep && step2.getAxis() == Axis.CHILD) {
                                // Construct a new expression that is
                                // appropriate for RuleChain use
                                final XPathFactory xpathFactory = new DefaultXPathFactory();

                                // Instead of an absolute location path, we'll
                                // be using a relative path
                                final LocationPath relativeLocationPath = xpathFactory.createRelativeLocationPath();
                                // The first step will be along the self axis
                                final Step allNodeStep = xpathFactory.createAllNodeStep(Axis.SELF);
                                // Retain all predicates from the original name
                                // step
                                @SuppressWarnings("unchecked")
                                final List<Predicate> predicates = step2.getPredicates();

                                for (Predicate predicate : predicates) {
                                    allNodeStep.addPredicate(predicate);
                                }
                                relativeLocationPath.addStep(allNodeStep);

                                // Retain the remaining steps from the original
                                // location path
                                for (int i = 2; i < steps.size(); i++) {
                                    relativeLocationPath.addStep(steps.get(i));
                                }

                                final BaseXPath xpath = createXPath(relativeLocationPath.getText(), navigator);
                                addQueryToNode(xpath, ((NameStep) step2).getLocalName());
                                valid = true;
                            }
                        }
                    }
                }
            } else if (node instanceof UnionExpr) { // Or a UnionExpr, that is
                // something like //TypeA |
                // //TypeB
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
            // Use the RuleChain for all the nodes extracted from the xpath
            // queries
            super.ruleChainVisits.addAll(nodeNameToXPaths.keySet());
        } else {
            // Use original XPath if we cannot use the RuleChain
            nodeNameToXPaths.clear();
            addQueryToNode(originalXPath, AST_ROOT);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Unable to use RuleChain for XPath: " + xpath);
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

    /**
     * Relates an XPath query to a node by adding the query to the {@link #nodeNameToXPaths}.
     *
     * @param xPath    the query to do over a node
     * @param nodeName the node on which to do the query
     */
    private void addQueryToNode(final XPath xPath, final String nodeName) {
        List<XPath> xPathsForNode = nodeNameToXPaths.get(nodeName);
        if (xPathsForNode == null) {
            xPathsForNode = new ArrayList<>();
            nodeNameToXPaths.put(nodeName, xPathsForNode);
        }
        xPathsForNode.add(xPath);
    }

    private BaseXPath createXPath(final String xpathQueryString, final Navigator navigator) throws JaxenException {
        final BaseXPath xpath = new BaseXPath(xpathQueryString, navigator);

        if (properties.size() > 1) {
            final SimpleVariableContext vc = new SimpleVariableContext();
            for (Entry<PropertyDescriptor<?>, Object> e : properties.entrySet()) {
                final String propName = e.getKey().name();
                if (!"xpath".equals(propName)) {
                    final Object value = e.getValue();
                    vc.setVariableValue(propName, value != null ? value.toString() : null);
                }
            }
            xpath.setVariableContext(vc);
        }
        return xpath;
    }


    private enum InitializationStatus {
        NONE, PARTIAL, FULL
    }
}
