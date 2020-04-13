/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.xpath.internal.AstDocument;
import net.sourceforge.pmd.lang.ast.xpath.internal.AstNodeWrapper;
import net.sourceforge.pmd.lang.ast.xpath.internal.DomainConversion;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

import net.sf.saxon.Configuration;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.sxpath.XPathDynamicContext;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.sxpath.XPathStaticContext;
import net.sf.saxon.sxpath.XPathVariable;
import net.sf.saxon.trans.XPathException;


/**
 * This is a Saxon based XPathRule query.
 */
public class SaxonXPathRuleQuery {

    /**
     * Special nodeName that references the root expression.
     */
    static final String AST_ROOT = "_AST_ROOT_";

    private static final Logger LOG = Logger.getLogger(SaxonXPathRuleQuery.class.getName());

    private static final NamePool NAME_POOL = new NamePool();

    /** Cache key for the wrapped tree for saxon. */
    private static final SimpleDataKey<AstDocument> SAXON_TREE_CACHE_KEY = DataMap.simpleDataKey("saxon.tree");

    private final String xpathExpr;
    private final XPathVersion version;
    private final Map<PropertyDescriptor<?>, Object> properties;
    private final XPathHandler xPathHandler;
    private final List<String> rulechainQueries = new ArrayList<>();
    private Configuration configuration;

    /**
     * Contains for each nodeName a sub expression, used for implementing rule chain.
     */
    Map<String, List<Expression>> nodeNameToXPaths = new HashMap<>();

    /**
     * Representation of an XPath query, created at {@link #initializeXPathExpression()} using {@link #xpathExpr}.
     */
    XPathExpression xpathExpression;

    /**
     * Holds the static context later used to match the variables in the dynamic context in
     * {@link #createDynamicContext(AstNodeWrapper)}. Created at {@link #initializeXPathExpression()}
     * using the properties descriptors in {@link #properties}.
     */
    private List<XPathVariable> xpathVariables;

    private final DeprecatedAttrLogger attrCtx;


    public SaxonXPathRuleQuery(String xpathExpr,
                               XPathVersion version,
                               Map<PropertyDescriptor<?>, Object> properties,
                               XPathHandler xPathHandler,
                               DeprecatedAttrLogger logger) {
        this.xpathExpr = xpathExpr;
        this.version = version;
        this.properties = properties;
        this.xPathHandler = xPathHandler;
        this.attrCtx = logger;
    }


    public String getXpathExpression() {
        return xpathExpr;
    }


    public List<String> getRuleChainVisits() {
        initializeXPathExpression();
        return rulechainQueries;
    }


    public List<Node> evaluate(final Node node, final RuleContext data) {
        initializeXPathExpression();

        try {
            final AstDocument documentNode = getDocumentNodeForRootNode(node);
            documentNode.setAttrCtx(attrCtx); //

            // Map AST Node -> Saxon Node
            final AstNodeWrapper rootElementNode = documentNode.getRootNode();
            assert rootElementNode != null : "Cannot find " + node;
            final XPathDynamicContext xpathDynamicContext = createDynamicContext(rootElementNode);

            final List<AstNodeWrapper> nodes = new ArrayList<>();
            List<Expression> expressions = getXPathExpressionForNodeOrDefault(node.getXPathNodeName());
            for (Expression expression : expressions) {
                SequenceIterator iterator = expression.iterate(xpathDynamicContext.getXPathContextObject());
                Item current = iterator.next();
                while (current instanceof AstNodeWrapper) {
                    nodes.add((AstNodeWrapper) current);
                    current = iterator.next();
                }
            }

            /*
             Map List of Saxon Nodes -> List of AST Nodes, which were detected to match the XPath expression
             (i.e. violation found)
              */
            final List<Node> results = new ArrayList<>(nodes.size());
            for (final AstNodeWrapper elementNode : nodes) {
                results.add(elementNode.getUnderlyingNode());
            }
            results.sort(RuleChainAnalyzer.documentOrderComparator());
            return results;
        } catch (final XPathException e) {
            throw new RuntimeException(xpathExpr + " had problem: " + e.getMessage(), e);
        }
    }

    private List<Expression> getXPathExpressionForNodeOrDefault(String nodeName) {
        if (nodeNameToXPaths.containsKey(nodeName)) {
            return nodeNameToXPaths.get(nodeName);
        }
        return nodeNameToXPaths.get(AST_ROOT);
    }

    /**
     * Attempt to create a dynamic context on which to evaluate the {@link #xpathExpression}.
     *
     * @param elementNode the node on which to create the context; generally this node is the root node of the Saxon
     *                    Tree
     *
     * @return the dynamic context on which to run the query
     *
     * @throws XPathException if the supplied value does not conform to the required type of the
     *                        variable, when setting up the dynamic context; or if the supplied value contains a node that does not belong to
     *                        this Configuration (or another Configuration that shares the same namePool)
     */
    private XPathDynamicContext createDynamicContext(final AstNodeWrapper elementNode) throws XPathException {
        final XPathDynamicContext dynamicContext = xpathExpression.createDynamicContext(elementNode);

        // Set variable values on the dynamic context
        for (final XPathVariable xpathVariable : xpathVariables) {
            final String variableName = xpathVariable.getVariableQName().getLocalPart();
            for (final Map.Entry<PropertyDescriptor<?>, Object> entry : properties.entrySet()) {
                if (variableName.equals(entry.getKey().name())) {
                    final Sequence valueRepresentation = getRepresentation(entry.getValue());
                    dynamicContext.setVariable(xpathVariable, valueRepresentation);
                }
            }
        }
        return dynamicContext;
    }


    private Sequence getRepresentation(final Object value) {
        if (value instanceof Collection) {
            return DomainConversion.getSequenceRepresentation((Collection<?>) value);
        } else {
            return DomainConversion.getAtomicRepresentation(value);
        }
    }


    /**
     * Gets the DocumentNode representation for the whole AST in which the node is, that is, if the node is not the root
     * of the AST, then the AST is traversed all the way up until the root node is found. If the DocumentNode was
     * cached because this method was previously called, then a new DocumentNode will not be instanced.
     *
     * @param node the node from which the root node will be looked for.
     *
     * @return the DocumentNode representing the whole AST
     */
    private AstDocument getDocumentNodeForRootNode(final Node node) {
        final RootNode root = node.getRoot();

        DataMap<DataKey<?, ?>> userMap = root.getUserMap();
        AstDocument docNode = userMap.get(SAXON_TREE_CACHE_KEY);
        if (docNode == null) {
            docNode = new AstDocument(root, configuration);
            userMap.set(SAXON_TREE_CACHE_KEY, docNode);
        }
        return docNode;
    }


    private void addExpressionForNode(String nodeName, Expression expression) {
        if (!nodeNameToXPaths.containsKey(nodeName)) {
            nodeNameToXPaths.put(nodeName, new LinkedList<>());
        }
        nodeNameToXPaths.get(nodeName).add(expression);
    }

    /**
     * Initialize the {@link #xpathExpression} and the {@link #xpathVariables}.
     */
    private void initializeXPathExpression() {
        if (xpathExpression != null) {
            return;
        }
        try {
            final XPathEvaluator xpathEvaluator = new XPathEvaluator();
            final XPathStaticContext xpathStaticContext = xpathEvaluator.getStaticContext();
            xpathStaticContext.getConfiguration().setNamePool(getNamePool());

            this.configuration = xpathStaticContext.getConfiguration();


            // TODO ((IndependentContext) xpathStaticContext).declareNamespace();

            xPathHandler.getRegisteredExtensionFunctions().forEach(configuration::registerExtensionFunction);


            /*
            Create XPathVariables for later use. It is a Saxon quirk that XPathVariables must be defined on the
            static context, and reused later to associate an actual value on the dynamic context creation, in
            createDynamicContext(ElementNode).
            */
            xpathVariables = new ArrayList<>();
            for (final PropertyDescriptor<?> propertyDescriptor : properties.keySet()) {
                final String name = propertyDescriptor.name();
                if (!"xpath".equals(name) && !XPathRule.VERSION_DESCRIPTOR.name().equals(name)) {
                    final XPathVariable xpathVariable = xpathStaticContext.declareVariable(null, name);
                    xpathVariables.add(xpathVariable);
                }
            }

            xpathExpression = xpathEvaluator.createExpression(xpathExpr);
            analyzeXPathForRuleChain(xpathEvaluator);
        } catch (final XPathException e) {
            throw new RuntimeException(e);
        }
    }

    private void analyzeXPathForRuleChain(final XPathEvaluator xpathEvaluator) {
        final Expression expr = xpathExpression.getInternalExpression();

        boolean useRuleChain = true;

        // First step: Split the union venn expressions into single expressions
        Iterable<Expression> subexpressions = RuleChainAnalyzer.splitUnions(expr);

        // Second step: Analyze each expression separately
        for (Expression subexpression : subexpressions) {
            RuleChainAnalyzer rca = new RuleChainAnalyzer(xpathEvaluator.getConfiguration());
            Expression modified = rca.visit(subexpression);

            if (rca.getRootElement() != null) {
                addExpressionForNode(rca.getRootElement(), modified);
            } else {
                // couldn't find a root element for the expression, that means, we can't use rule chain at all
                // even though, it would be possible for part of the expression.
                useRuleChain = false;
                break;
            }
        }

        if (useRuleChain) {
            rulechainQueries.addAll(nodeNameToXPaths.keySet());
        } else {
            nodeNameToXPaths.clear();
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Unable to use RuleChain for XPath: " + xpathExpr);
            }
        }

        // always add fallback expression
        addExpressionForNode(AST_ROOT, xpathExpression.getInternalExpression());
    }

}
