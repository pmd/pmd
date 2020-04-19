/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.ast.xpath.saxon.DocumentNode;
import net.sourceforge.pmd.lang.ast.xpath.saxon.ElementNode;
import net.sourceforge.pmd.lang.rule.xpath.internal.RuleChainAnalyzer;
import net.sourceforge.pmd.lang.xpath.Initializer;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.NamespaceConstant;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.ValueRepresentation;
import net.sf.saxon.sxpath.AbstractStaticContext;
import net.sf.saxon.sxpath.IndependentContext;
import net.sf.saxon.sxpath.XPathDynamicContext;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.sxpath.XPathStaticContext;
import net.sf.saxon.sxpath.XPathVariable;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.BigIntegerValue;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.DoubleValue;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.FloatValue;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.SequenceExtent;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.UntypedAtomicValue;
import net.sf.saxon.value.Value;

/**
 * This is a Saxon based XPathRule query.
 *
 * @deprecated Internal API
 */
@Deprecated
@InternalApi
public class SaxonXPathRuleQuery extends AbstractXPathRuleQuery {

    /**
     * Special nodeName that references the root expression.
     */
    static final String AST_ROOT = "_AST_ROOT_";

    private static final Logger LOG = Logger.getLogger(SaxonXPathRuleQuery.class.getName());

    private static final NamePool NAME_POOL = new NamePool();

    /** Cache key for the wrapped tree for saxon. */
    private static final SimpleDataKey<DocumentNode> SAXON_TREE_CACHE_KEY = DataMap.simpleDataKey("saxon.tree");

    /**
     * Contains for each nodeName a sub expression, used for implementing rule chain.
     */
    Map<String, List<Expression>> nodeNameToXPaths = new HashMap<>();

    /**
     * Representation of an XPath query, created at {@link #initializeXPathExpression()} using {@link #xpath}.
     */
    XPathExpression xpathExpression;

    /**
     * Holds the static context later used to match the variables in the dynamic context in
     * {@link #createDynamicContext(ElementNode)}. Created at {@link #initializeXPathExpression()}
     * using the properties descriptors in {@link #properties}.
     */
    private List<XPathVariable> xpathVariables;

    private final DeprecatedAttrLogger attrCtx;

    @Deprecated
    public SaxonXPathRuleQuery() {
        this(DeprecatedAttrLogger.noop());
    }

    public SaxonXPathRuleQuery(DeprecatedAttrLogger attrCtx) {
        this.attrCtx = attrCtx;
    }

    @Override
    public boolean isSupportedVersion(String version) {
        return XPATH_1_0_COMPATIBILITY.equals(version) || XPATH_2_0.equals(version);
    }

    @Override
    public List<Node> evaluate(final Node node, final RuleContext data) {
        initializeXPathExpression();

        try {
            final DocumentNode documentNode = getDocumentNodeForRootNode(node);
            documentNode.setAttrCtx(attrCtx); //

            // Map AST Node -> Saxon Node
            final ElementNode rootElementNode = documentNode.nodeToElementNode.get(node);
            assert rootElementNode != null : "Cannot find " + node;
            final XPathDynamicContext xpathDynamicContext = createDynamicContext(rootElementNode);

            final List<ElementNode> nodes = new LinkedList<>();
            List<Expression> expressions = getXPathExpressionForNodeOrDefault(node.getXPathNodeName());
            for (Expression expression : expressions) {
                SequenceIterator iterator = expression.iterate(xpathDynamicContext.getXPathContextObject());
                Item current = iterator.next();
                while (current != null) {
                    nodes.add((ElementNode) current);
                    current = iterator.next();
                }
            }

            /*
             Map List of Saxon Nodes -> List of AST Nodes, which were detected to match the XPath expression
             (i.e. violation found)
              */
            final List<Node> results = new ArrayList<>(nodes.size());
            for (final ElementNode elementNode : nodes) {
                results.add((Node) elementNode.getUnderlyingNode());
            }
            Collections.sort(results, RuleChainAnalyzer.documentOrderComparator());
            return results;
        } catch (final XPathException e) {
            throw new RuntimeException(super.xpath + " had problem: " + e.getMessage(), e);
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
     * @return the dynamic context on which to run the query
     * @throws XPathException if the supplied value does not conform to the required type of the
     * variable, when setting up the dynamic context; or if the supplied value contains a node that does not belong to
     * this Configuration (or another Configuration that shares the same namePool)
     */
    private XPathDynamicContext createDynamicContext(final ElementNode elementNode) throws XPathException {
        final XPathDynamicContext dynamicContext = xpathExpression.createDynamicContext(elementNode);

        // Set variable values on the dynamic context
        for (final XPathVariable xpathVariable : xpathVariables) {
            final String variableName = xpathVariable.getVariableQName().getLocalName();
            for (final Map.Entry<PropertyDescriptor<?>, Object> entry : super.properties.entrySet()) {
                if (variableName.equals(entry.getKey().name())) {
                    final ValueRepresentation valueRepresentation = getRepresentation(entry.getKey(), entry.getValue());
                    dynamicContext.setVariable(xpathVariable, valueRepresentation);
                }
            }
        }
        return dynamicContext;
    }


    private ValueRepresentation getRepresentation(final PropertyDescriptor<?> descriptor, final Object value) {
        if (descriptor.isMultiValue()) {
            return getSequenceRepresentation((List<?>) value);
        } else {
            return getAtomicRepresentation(value);
        }
    }

    /**
     * Gets the DocumentNode representation for the whole AST in which the node is, that is, if the node is not the root
     * of the AST, then the AST is traversed all the way up until the root node is found. If the DocumentNode was
     * cached because this method was previously called, then a new DocumentNode will not be instanced.
     *
     * @param node the node from which the root node will be looked for.
     * @return the DocumentNode representing the whole AST
     */
    private DocumentNode getDocumentNodeForRootNode(final Node node) {
        final Node root = getRootNode(node);

        DataMap<DataKey<?, ?>> userMap = root.getUserMap();
        DocumentNode docNode = userMap.get(SAXON_TREE_CACHE_KEY);
        if (docNode == null) {
            docNode = new DocumentNode(root, getNamePool());
            userMap.set(SAXON_TREE_CACHE_KEY, docNode);
        }
        return docNode;
    }

    /**
     * Traverse the AST until the root node is found.
     *
     * @param node the node from where to start traversing the tree
     * @return the root node
     */
    private Node getRootNode(final Node node) {
        Node root = node;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return root;
    }

    private void addExpressionForNode(String nodeName, Expression expression) {
        if (!nodeNameToXPaths.containsKey(nodeName)) {
            nodeNameToXPaths.put(nodeName, new LinkedList<Expression>());
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

            // Enable XPath 1.0 compatibility
            if (XPATH_1_0_COMPATIBILITY.equals(version)) {
                ((AbstractStaticContext) xpathStaticContext).setBackwardsCompatibilityMode(true);
            }

            ((IndependentContext) xpathStaticContext).declareNamespace("fn", NamespaceConstant.FN);

            // Register PMD functions
            Initializer.initialize((IndependentContext) xpathStaticContext);

            /*
            Create XPathVariables for later use. It is a Saxon quirk that XPathVariables must be defined on the
            static context, and reused later to associate an actual value on the dynamic context creation, in
            createDynamicContext(ElementNode).
            */
            xpathVariables = new ArrayList<>();
            for (final PropertyDescriptor<?> propertyDescriptor : super.properties.keySet()) {
                final String name = propertyDescriptor.name();
                if (!"xpath".equals(name)) {
                    final XPathVariable xpathVariable = xpathStaticContext.declareVariable(null, name);
                    xpathVariables.add(xpathVariable);
                }
            }

            xpathExpression = xpathEvaluator.createExpression(super.xpath);
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
            super.ruleChainVisits.addAll(nodeNameToXPaths.keySet());
        } else {
            nodeNameToXPaths.clear();
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Unable to use RuleChain for XPath: " + xpath);
            }
        }

        // always add fallback expression
        addExpressionForNode(AST_ROOT, xpathExpression.getInternalExpression());
    }

    /**
     * Gets the Saxon representation of the parameter, if its type corresponds
     * to an XPath 2.0 atomic datatype.
     *
     * @param value The value to convert
     *
     * @return The converted AtomicValue
     */
    public static AtomicValue getAtomicRepresentation(final Object value) {

        /*
        FUTURE When supported, we should consider refactor this implementation to use Pattern Matching
        (see http://openjdk.java.net/jeps/305) so that it looks clearer.
        */
        if (value == null) {
            return UntypedAtomicValue.ZERO_LENGTH_UNTYPED;
        } else if (value instanceof Enum) {
            // enums use their toString
            return new StringValue(value.toString());
        } else if (value instanceof String) {
            return new StringValue((String) value);
        } else if (value instanceof Boolean) {
            return BooleanValue.get((Boolean) value);
        } else if (value instanceof Integer) {
            return Int64Value.makeIntegerValue((Integer) value);
        } else if (value instanceof Long) {
            return new BigIntegerValue((Long) value);
        } else if (value instanceof Double) {
            return new DoubleValue((Double) value);
        } else if (value instanceof Character) {
            return new StringValue(value.toString());
        } else if (value instanceof Float) {
            return new FloatValue((Float) value);
        } else if (value instanceof Pattern) {
            return new StringValue(String.valueOf(value));
        } else {
            // We could maybe use UntypedAtomicValue
            throw new RuntimeException("Unable to create ValueRepresentation for value of type: " + value.getClass());
        }
    }

    public static Value getSequenceRepresentation(List<?> list) {
        if (list == null || list.isEmpty()) {
            return EmptySequence.getInstance();
        }
        final Item[] converted = new Item[list.size()];
        for (int i = 0; i < list.size(); i++) {
            converted[i] = getAtomicRepresentation(list.get(i));
        }
        return new SequenceExtent(converted);
    }

    @Override
    public List<String> getRuleChainVisits() {
        initializeXPathExpression();
        return super.getRuleChainVisits();
    }

    public static NamePool getNamePool() {
        return NAME_POOL;
    }
}
