/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

import net.sf.saxon.Configuration;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.LocalVariableReference;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.AtomicSequence;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.sxpath.IndependentContext;
import net.sf.saxon.sxpath.XPathDynamicContext;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
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
    private static final SimpleDataKey<AstTreeInfo> SAXON_TREE_CACHE_KEY = DataMap.simpleDataKey("saxon.tree");

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
            final AstTreeInfo documentNode = getDocumentNodeForRootNode(node);
            documentNode.setAttrCtx(attrCtx); //

            // Map AST Node -> Saxon Node
            final XPathDynamicContext xpathDynamicContext = xpathExpression.createDynamicContext(documentNode.findWrapperFor(node));

            final List<AstElementNode> nodes = new ArrayList<>();
            List<Expression> expressions = getXPathExpressionForNodeOrDefault(node.getXPathNodeName());
            for (Expression expression : expressions) {
                SequenceIterator iterator = expression.iterate(xpathDynamicContext.getXPathContextObject());
                Item current = iterator.next();
                while (current instanceof AstElementNode) {
                    nodes.add((AstElementNode) current);
                    current = iterator.next();
                }
            }

            /*
             Map List of Saxon Nodes -> List of AST Nodes, which were detected to match the XPath expression
             (i.e. violation found)
              */
            final List<Node> results = new ArrayList<>(nodes.size());
            for (final AstElementNode elementNode : nodes) {
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
     * Gets the DocumentNode representation for the whole AST in which the node is, that is, if the node is not the root
     * of the AST, then the AST is traversed all the way up until the root node is found. If the DocumentNode was
     * cached because this method was previously called, then a new DocumentNode will not be instanced.
     *
     * @param node the node from which the root node will be looked for.
     *
     * @return the DocumentNode representing the whole AST
     */
    private AstTreeInfo getDocumentNodeForRootNode(final Node node) {
        final RootNode root = node.getRoot();

        DataMap<DataKey<?, ?>> userMap = root.getUserMap();
        AstTreeInfo docNode = userMap.get(SAXON_TREE_CACHE_KEY);
        if (docNode == null) {
            docNode = new AstTreeInfo(root, configuration);
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
            this.configuration = xpathEvaluator.getConfiguration();
            StaticContextWithProperties staticCtx = new StaticContextWithProperties(configuration);

            for (final PropertyDescriptor<?> propertyDescriptor : properties.keySet()) {
                final String name = propertyDescriptor.name();
                if (!"xpath".equals(name) && !XPathRule.VERSION_DESCRIPTOR.name().equals(name)) {
                    staticCtx.declareProperty(propertyDescriptor);
                }
            }

            xpathEvaluator.setStaticContext(staticCtx);
            configuration.setNamePool(getNamePool());

            for (ExtensionFunctionDefinition fun : xPathHandler.getRegisteredExtensionFunctions()) {
                StructuredQName qname = fun.getFunctionQName();
                staticCtx.declareNamespace(qname.getPrefix(), qname.getURI());
                configuration.registerExtensionFunction(fun);
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

    public static NamePool getNamePool() {
        return NAME_POOL;
    }


    final class StaticContextWithProperties extends IndependentContext {

        private final Map<StructuredQName, PropertyDescriptor<?>> propertiesByName = new HashMap<>();

        public StaticContextWithProperties(Configuration config) {
            super(config);
        }

        public void declareProperty(PropertyDescriptor<?> prop) {
            XPathVariable var = declareVariable(null, prop.name());
            propertiesByName.put(var.getVariableQName(), prop);
        }

        @Override
        public Expression bindVariable(StructuredQName qName) throws XPathException {
            LocalVariableReference local = (LocalVariableReference) super.bindVariable(qName);
            PropertyDescriptor<?> prop = propertiesByName.get(qName);
            if (prop == null || prop.defaultValue() == null) {
                return local;
            }
            Object actualValue = properties.getOrDefault(prop, prop.defaultValue());
            AtomicSequence converted = DomainConversion.convert(actualValue);
            local.setStaticType(DomainConversion.typeOf(converted), converted, 0);
            return local;
        }
    }
}
