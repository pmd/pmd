/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.xpath.PmdXPathException;
import net.sourceforge.pmd.lang.rule.xpath.PmdXPathException.Phase;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionDefinition;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

import net.sf.saxon.Configuration;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.LocalVariableReference;
import net.sf.saxon.expr.StaticContext;
import net.sf.saxon.expr.StringLiteral;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.lib.NamespaceConstant;
import net.sf.saxon.om.AtomicSequence;
import net.sf.saxon.om.EmptyAtomicSequence;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.sxpath.IndependentContext;
import net.sf.saxon.sxpath.XPathDynamicContext;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.sxpath.XPathVariable;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BigDecimalValue;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.SequenceExtent;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;


/**
 * This is a Saxon based XPathRule query.
 */
public class SaxonXPathRuleQuery {

    /**
     * Special nodeName that references the root expression.
     */
    static final String AST_ROOT = "_AST_ROOT_";

    private static final Logger LOG = LoggerFactory.getLogger(SaxonXPathRuleQuery.class);

    private static final NamePool NAME_POOL = new NamePool();

    /** Cache key for the wrapped tree for saxon. */
    private static final SimpleDataKey<AstTreeInfo> SAXON_TREE_CACHE_KEY = DataMap.simpleDataKey("saxon.tree");

    private final String xpathExpr;
    @SuppressWarnings("PMD") // may be useful later, idk
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
     * Representation of an XPath query, created at {@link #initialize()} using {@link #xpathExpr}.
     */
    XPathExpression xpathExpression;

    private final DeprecatedAttrLogger attrCtx;


    public SaxonXPathRuleQuery(String xpathExpr,
                               XPathVersion version,
                               Map<PropertyDescriptor<?>, Object> properties,
                               XPathHandler xPathHandler,
                               DeprecatedAttrLogger logger) throws PmdXPathException {
        this.xpathExpr = xpathExpr;
        this.version = version;
        this.properties = properties;
        this.xPathHandler = xPathHandler;
        this.attrCtx = logger;
        try {
            initialize();
        } catch (XPathException e) {
            throw wrapException(e, Phase.INITIALIZATION);
        }
    }


    public String getXpathExpression() {
        return xpathExpr;
    }


    public List<String> getRuleChainVisits() {
        return rulechainQueries;
    }


    public List<Node> evaluate(final Node node) {
        final AstTreeInfo documentNode = getDocumentNodeForRootNode(node);
        documentNode.setAttrCtx(attrCtx);
        try {

            // Map AST Node -> Saxon Node
            final XPathDynamicContext xpathDynamicContext = xpathExpression.createDynamicContext(documentNode.findWrapperFor(node));

            // XPath 2.0 sequences may contain duplicates
            final Set<Node> results = new LinkedHashSet<>();
            List<Expression> expressions = getExpressionsForLocalNameOrDefault(node.getXPathNodeName());
            for (Expression expression : expressions) {
                @SuppressWarnings("PMD.CloseResource")
                SequenceIterator iterator = expression.iterate(xpathDynamicContext.getXPathContextObject());
                Item current = iterator.next();
                while (current != null) {
                    if (current instanceof AstNodeOwner) {
                        results.add(((AstNodeOwner) current).getUnderlyingNode());
                    } else {
                        throw new XPathException("XPath rule expression returned a non-node (" + current.getClass() + "): " + current);
                    }
                    current = iterator.next();
                }
            }

            final List<Node> sortedRes = new ArrayList<>(results);
            sortedRes.sort(RuleChainAnalyzer.documentOrderComparator());
            return sortedRes;
        } catch (final XPathException e) {
            throw wrapException(e, Phase.EVALUATION);
        } finally {
            documentNode.setAttrCtx(DeprecatedAttrLogger.noop());
        }
    }

    private ContextedRuntimeException wrapException(XPathException e, Phase phase) {
        return new PmdXPathException(e, phase, xpathExpr, version);
    }

    // test only
    List<Expression> getExpressionsForLocalNameOrDefault(String nodeName) {
        List<Expression> expressions = nodeNameToXPaths.get(nodeName);
        if (expressions != null) {
            return expressions;
        }
        return nodeNameToXPaths.get(AST_ROOT);
    }

    // test only
    Expression getFallbackExpr() {
        return nodeNameToXPaths.get(SaxonXPathRuleQuery.AST_ROOT).get(0);
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
        return root.getUserMap().computeIfAbsent(SAXON_TREE_CACHE_KEY, () -> new AstTreeInfo(root, configuration));
    }


    private void addExpressionForNode(String nodeName, Expression expression) {
        nodeNameToXPaths.computeIfAbsent(nodeName, n -> new ArrayList<>(2)).add(expression);
    }

    private void initialize() throws XPathException {

        this.configuration = Configuration.newConfiguration();
        this.configuration.setNamePool(getNamePool());

        StaticContextWithProperties staticCtx = new StaticContextWithProperties(this.configuration);
        staticCtx.setXPathLanguageLevel(version == XPathVersion.XPATH_3_1 ? 31 : 20);
        staticCtx.declareNamespace("fn", NamespaceConstant.FN);

        for (final PropertyDescriptor<?> propertyDescriptor : properties.keySet()) {
            final String name = propertyDescriptor.name();
            if (!"xpath".equals(name)) {
                staticCtx.declareProperty(propertyDescriptor);
            }
        }

        for (XPathFunctionDefinition xpathFun : xPathHandler.getRegisteredExtensionFunctions()) {
            ExtensionFunctionDefinition fun = convertAbstractXPathFunctionDefinition(xpathFun);
            StructuredQName qname = fun.getFunctionQName();
            staticCtx.declareNamespace(qname.getPrefix(), qname.getURI());
            this.configuration.registerExtensionFunction(fun);
        }

        final XPathEvaluator xpathEvaluator = new XPathEvaluator(configuration);
        xpathEvaluator.setStaticContext(staticCtx);

        xpathExpression = xpathEvaluator.createExpression(xpathExpr);
        analyzeXPathForRuleChain(xpathEvaluator);

    }

    private void analyzeXPathForRuleChain(final XPathEvaluator xpathEvaluator) {
        final Expression expr = xpathExpression.getInternalExpression();

        boolean useRuleChain = true;

        // First step: Split the union venn expressions into single expressions
        Iterable<Expression> subexpressions = SaxonExprTransformations.splitUnions(expr);

        // Second step: Analyze each expression separately
        for (final Expression subexpression : subexpressions) { // final because of checkstyle
            Expression modified = subexpression;
            modified = SaxonExprTransformations.hoistFilters(modified);
            modified = SaxonExprTransformations.reduceRoot(modified);
            modified = SaxonExprTransformations.copyTopLevelLets(modified, expr);
            RuleChainAnalyzer rca = new RuleChainAnalyzer(xpathEvaluator.getConfiguration());
            final Expression finalExpr = rca.visit(modified); // final because of lambda

            if (!rca.getRootElements().isEmpty()) {
                rca.getRootElements().forEach(it -> addExpressionForNode(it, finalExpr));
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
            LOG.debug("Unable to use RuleChain for XPath: {}", xpathExpr);
        }

        // always add fallback expression
        addExpressionForNode(AST_ROOT, xpathExpression.getInternalExpression());
    }

    public static NamePool getNamePool() {
        return NAME_POOL;
    }


    final class StaticContextWithProperties extends IndependentContext {

        private final Map<StructuredQName, PropertyDescriptor<?>> propertiesByName = new HashMap<>();

        StaticContextWithProperties(Configuration config) {
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

            // TODO Saxon optimizer bug (js/codestyle.xml/AssignmentInOperand)
            Object actualValue = properties.getOrDefault(prop, prop.defaultValue());
            AtomicSequence converted = DomainConversion.convert(actualValue);
            local.setStaticType(null, converted, 0);
            return local;
        }
    }

    public static ExtensionFunctionDefinition convertAbstractXPathFunctionDefinition(XPathFunctionDefinition definition) {
        final SequenceType SINGLE_ELEMENT_SEQUENCE_TYPE = NodeKindTest.ELEMENT.one();

        return new ExtensionFunctionDefinition() {
            private SequenceType convertToSequenceType(XPathFunctionDefinition.Type type) {
                switch (type) {
                    case SINGLE_STRING: return SequenceType.SINGLE_STRING;
                    case SINGLE_BOOLEAN: return SequenceType.SINGLE_BOOLEAN;
                    case SINGLE_ELEMENT: return SINGLE_ELEMENT_SEQUENCE_TYPE;
                    case SINGLE_INTEGER: return SequenceType.SINGLE_INTEGER;
                    case STRING_SEQUENCE: return SequenceType.STRING_SEQUENCE;
                    case OPTIONAL_STRING: return SequenceType.OPTIONAL_STRING;
                    case OPTIONAL_DECIMAL: return SequenceType.OPTIONAL_DECIMAL;
                    default:
                        throw new UnsupportedOperationException("Type " + type + " is not supported");
                }
            }

            private SequenceType[] convertToSequenceTypes(XPathFunctionDefinition.Type[] types) {
                SequenceType[] result = new SequenceType[types.length];
                for (int i = 0; i < types.length; i++) {
                    result[i] = convertToSequenceType(types[i]);
                }
                return result;
            }

            @Override
            public StructuredQName getFunctionQName() {
                QName qName = definition.getQName();
                return new StructuredQName(qName.getPrefix(), qName.getNamespaceURI(), qName.getLocalPart());
            }

            @Override
            public SequenceType[] getArgumentTypes() {
                return convertToSequenceTypes(definition.getArgumentTypes());
            }

            @Override
            public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
                return convertToSequenceType(definition.getResultType());
            }

            @Override
            public boolean dependsOnFocus() {
                return definition.dependsOnContext();
            }

            @Override
            public ExtensionFunctionCall makeCallExpression() {
                XPathFunctionDefinition.FunctionCall call = definition.makeCallExpression();
                return new ExtensionFunctionCall() {
                    @Override
                    public Expression rewrite(StaticContext context, Expression[] arguments) throws XPathException {
                        Object[] convertedArguments = new Object[definition.getArgumentTypes().length];
                        for (int i = 0; i < convertedArguments.length; i++) {
                            if (arguments[i] instanceof StringLiteral) {
                                convertedArguments[i] = ((StringLiteral) arguments[i]).getStringValue();
                            }
                        }
                        try {
                            call.staticInit(convertedArguments);
                        } catch (XPathFunctionException e) {
                            XPathException xPathException = new XPathException(e);
                            xPathException.setIsStaticError(true);
                            throw xPathException;
                        }
                        return null;
                    }

                    @Override
                    public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                        Node contextNode = null;
                        if (definition.dependsOnContext()) {
                            contextNode = XPathElementToNodeHelper.itemToNode(context.getContextItem());
                        }
                        Object[] convertedArguments = new Object[definition.getArgumentTypes().length];
                        for (int i = 0; i < convertedArguments.length; i++) {
                            switch (definition.getArgumentTypes()[i]) {
                                case SINGLE_STRING:
                                    convertedArguments[i] = arguments[i].head().getStringValue();
                                    break;
                                case SINGLE_ELEMENT:
                                    convertedArguments[i] = arguments[i].head();
                                    break;
                                default:
                                throw new UnsupportedOperationException("Don't know how to convert argument type " + definition.getArgumentTypes()[i]);
                            }
                        }


                        Object result = null;
                        try {
                            result = call.call(contextNode, convertedArguments);
                        } catch (XPathFunctionException e) {
                            throw new XPathException(e);
                        }
                        Sequence convertedResult = null;
                        switch (definition.getResultType()) {
                            case SINGLE_BOOLEAN:
                                convertedResult = BooleanValue.get((Boolean) result);
                                break;
                            case SINGLE_INTEGER:
                                convertedResult = Int64Value.makeIntegerValue((Integer) result);
                                break;
                            case SINGLE_STRING:
                                convertedResult = new StringValue((String) result);
                                break;
                            case OPTIONAL_STRING:
                                convertedResult = result != null ? new StringValue((String) result) : EmptyAtomicSequence.INSTANCE;
                                break;
                            case STRING_SEQUENCE:
                                convertedResult = result != null ? new SequenceExtent(Arrays.stream((String[]) result).map(StringValue::new).collect(Collectors.toList()))
                                        : EmptySequence.getInstance();
                                break;
                            case OPTIONAL_DECIMAL:
                                convertedResult = result != null && Double.isFinite((Double) result) ? new BigDecimalValue((Double) result)
                                        : EmptySequence.getInstance();
                                break;
                            default:
                                throw new UnsupportedOperationException("Don't know how to convert result type " + definition.getResultType());
                        }
                        return convertedResult;
                    }
                };
            }
        };
    }
}
