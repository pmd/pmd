/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.xpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.saxon.om.ValueRepresentation;
import net.sf.saxon.sxpath.AbstractStaticContext;
import net.sf.saxon.sxpath.IndependentContext;
import net.sf.saxon.sxpath.XPathDynamicContext;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.sxpath.XPathStaticContext;
import net.sf.saxon.sxpath.XPathVariable;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.StringValue;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.saxon.DocumentNode;
import net.sourceforge.pmd.lang.ast.xpath.saxon.ElementNode;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.lang.xpath.Initializer;

/**
 * This is a Saxon based XPathRule query.
 */
public class SaxonXPathRuleQuery extends AbstractXPathRuleQuery {

    // Mapping from Node name to applicable XPath queries
    private XPathExpression xpathExpression;
    private List<XPathVariable> xpathVariables;
    private static final Map<Node, DocumentNode> CACHE = new HashMap<>();

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
                        PropertyDescriptor<?> propertyDescriptor = entry.getKey();
                        if (propertyDescriptor instanceof PropertyDescriptorWrapper) {
                            propertyDescriptor = ((PropertyDescriptorWrapper) propertyDescriptor)
                                    .getPropertyDescriptor();
                        }
                        Object value = entry.getValue();
                        ValueRepresentation valueRepresentation;

                        // TODO Need to handle null values?
                        // TODO Need to handle more PropertyDescriptors, is
                        // there an easy factory in Saxon we can use for this?
                        if (propertyDescriptor instanceof StringProperty) {
                            valueRepresentation = new StringValue((String) value);
                        } else if (propertyDescriptor instanceof BooleanProperty) {
                            valueRepresentation = BooleanValue.get(((Boolean) value).booleanValue());
                        } else if (propertyDescriptor instanceof IntegerProperty) {
                            valueRepresentation = Int64Value.makeIntegerValue((Integer) value);
                        } else if (propertyDescriptor instanceof EnumeratedProperty) {
                            if (value instanceof String) {
                                valueRepresentation = new StringValue((String) value);
                            } else {
                                throw new RuntimeException(
                                        "Unable to create ValueRepresentaton for non-String EnumeratedProperty value: "
                                                + value);
                            }
                        } else {
                            throw new RuntimeException("Unable to create ValueRepresentaton for PropertyDescriptor: "
                                    + propertyDescriptor);
                        }
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

    private DocumentNode getDocumentNode(Node node) {
        // Get the root AST node
        Node root = node;
        while (root.jjtGetParent() != null) {
            root = root.jjtGetParent();
        }

        // Cache DocumentNode trees, so that different XPath queries can re-use
        // them.
        // Ideally this would be an LRU cache.
        DocumentNode documentNode;
        synchronized (CACHE) {
            documentNode = CACHE.get(root);
            if (documentNode == null) {
                documentNode = new DocumentNode(root);
                if (CACHE.size() > 20) {
                    CACHE.clear();
                }
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
}
