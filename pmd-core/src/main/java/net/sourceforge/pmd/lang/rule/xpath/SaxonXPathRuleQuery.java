/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.saxon.DocumentNode;
import net.sourceforge.pmd.lang.ast.xpath.saxon.ElementNode;
import net.sourceforge.pmd.lang.xpath.Initializer;
import net.sourceforge.pmd.properties.PropertyDescriptor;

import net.sf.saxon.om.Item;
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


/**
 * This is a Saxon based XPathRule query.
 */
public class SaxonXPathRuleQuery extends AbstractXPathRuleQuery {

    private static final int MAX_CACHE_SIZE = 20;
    private static final Map<Node, DocumentNode> CACHE = new LinkedHashMap<Node, DocumentNode>(MAX_CACHE_SIZE) {
        private static final long serialVersionUID = -7653916493967142443L;


        protected boolean removeEldestEntry(final Map.Entry<Node, DocumentNode> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };
    // Mapping from Node name to applicable XPath queries
    private XPathExpression xpathExpression;
    private List<XPathVariable> xpathVariables;


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupportedVersion(String version) {
        return XPATH_1_0_COMPATIBILITY.equals(version) || XPATH_2_0.equals(version);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Node> evaluate(Node node, RuleContext data) {
        initializeXPathExpression();

        List<Node> results = new ArrayList<>();
        try {
            // Get the DocumentNode for the AST
            DocumentNode documentNode = getDocumentNode(node);

            // Get the corresponding ElementNode for this node.
            ElementNode rootElementNode = documentNode.nodeToElementNode.get(node);

            // Create a dynamic context for this node
            XPathDynamicContext xpathDynamicContext = xpathExpression.createDynamicContext(rootElementNode);

            // Set variable values on the dynamic context
            for (XPathVariable xpathVariable : xpathVariables) {
                String name = xpathVariable.getVariableQName().getLocalName();
                for (Map.Entry<PropertyDescriptor<?>, Object> entry : super.properties.entrySet()) {
                    if (name.equals(entry.getKey().name())) {
                        ValueRepresentation valueRepresentation = getRepresentation(entry.getKey(), entry.getValue());
                        xpathDynamicContext.setVariable(xpathVariable, valueRepresentation);
                    }
                }
            }

            List<ElementNode> nodes = xpathExpression.evaluate(xpathDynamicContext);
            for (ElementNode elementNode : nodes) {
                results.add((Node) elementNode.getUnderlyingNode());
            }
        } catch (XPathException e) {
            throw new RuntimeException(super.xpath + " had problem: " + e.getMessage(), e);
        }
        return results;
    }


    private ValueRepresentation getRepresentation(PropertyDescriptor<?> descriptor, Object value) {
        if (descriptor.isMultiValue()) {
            List<?> val = (List<?>) value;
            if (val.isEmpty()) {
                return EmptySequence.getInstance();
            }
            Item[] converted = new Item[val.size()];
            for (int i = 0; i < val.size(); i++) {
                converted[i] = getAtomicRepresentation(val.get(i));
            }
            return new SequenceExtent(converted);
        } else {
            return getAtomicRepresentation(value);
        }
    }
    

    private DocumentNode getDocumentNode(Node node) {
        // Get the root AST node
        Node root = node;
        while (root.jjtGetParent() != null) {
            root = root.jjtGetParent();
        }

        // Cache DocumentNode trees, so that different XPath queries can re-use them.
        DocumentNode documentNode;
        synchronized (CACHE) {
            documentNode = CACHE.get(root);
            if (documentNode == null) {
                documentNode = new DocumentNode(root);
                CACHE.put(root, documentNode);
            }
        }
        return documentNode;
    }


    private void initializeXPathExpression() {
        if (xpathExpression != null) {
            return;
        }
        try {
            XPathEvaluator xpathEvaluator = new XPathEvaluator();
            XPathStaticContext xpathStaticContext = xpathEvaluator.getStaticContext();

            // Enable XPath 1.0 compatibility
            if (XPATH_1_0_COMPATIBILITY.equals(version)) {
                ((AbstractStaticContext) xpathStaticContext).setBackwardsCompatibilityMode(true);
            }

            // Register PMD functions
            Initializer.initialize((IndependentContext) xpathStaticContext);

            // Create XPathVariables for later use. It is a Saxon quirk that
            // XPathVariables must be defined on the static context, and
            // reused later to associate an actual value on the dynamic context.
            xpathVariables = new ArrayList<>();
            for (PropertyDescriptor<?> propertyDescriptor : super.properties.keySet()) {
                String name = propertyDescriptor.name();
                if (!"xpath".equals(name)) {
                    XPathVariable xpathVariable = xpathStaticContext.declareVariable(null, name);
                    xpathVariables.add(xpathVariable);
                }
            }

            // TODO Come up with a way to make use of RuleChain. I had hacked up
            // an approach which used Jaxen's stuff, but that only works for
            // 1.0 compatibility mode. Rather do it right instead of kludging.
            xpathExpression = xpathEvaluator.createExpression(super.xpath);
        } catch (XPathException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets the Saxon representation of the parameter, if its type corresponds 
     * to an XPath 2.0 atomic datatype.
     *
     * @param value The value to convert
     *
     * @return The converted AtomicValue
     */
    public static AtomicValue getAtomicRepresentation(Object value) {
        if (value == null) {
            return UntypedAtomicValue.ZERO_LENGTH_UNTYPED;
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
        } else {
            // We could maybe use UntypedAtomicValue
            throw new RuntimeException("Unable to create ValueRepresentation for value of type: " + value.getClass());
        }
    }
}
