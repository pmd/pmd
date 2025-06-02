/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.w3c.dom.Document;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionDefinition;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.lang.rule.xpath.internal.DomainConversion;
import net.sourceforge.pmd.lang.rule.xpath.internal.SaxonExtensionFunctionDefinitionAdapter;
import net.sourceforge.pmd.lang.xml.ast.XmlNode;
import net.sourceforge.pmd.lang.xml.ast.internal.XmlParserImpl.RootXmlNode;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.DataKey;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

import net.sf.saxon.Configuration;
import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.AtomicSequence;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.NamespaceUri;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.sxpath.IndependentContext;
import net.sf.saxon.sxpath.XPathDynamicContext;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.sxpath.XPathStaticContext;
import net.sf.saxon.sxpath.XPathVariable;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.wrapper.AbstractNodeWrapper;

final class SaxonDomXPathQuery {

    private static final NamePool NAME_POOL = new NamePool();

    private static final SimpleDataKey<DocumentWrapper> SAXON_DOM_WRAPPER
        = DataMap.simpleDataKey("pmd.saxon.dom.wrapper");

    /** The XPath expression as a string. */
    private final String xpath;
    private final XPathHandler xpathHandler;
    /** The executable XPath expression. */
    private final XPathExpressionWithProperties xpathExpression;


    private final Configuration configuration;

    SaxonDomXPathQuery(String xpath,
                       String defaultNsUri,
                       List<PropertyDescriptor<?>> properties,
                       XPathHandler xpathHandler) {
        this.xpath = xpath;
        this.xpathHandler = xpathHandler;
        configuration = new Configuration();
        configuration.setNamePool(NAME_POOL);
        xpathExpression = makeXPathExpression(this.xpath, defaultNsUri, properties);
    }

    private XPathExpressionWithProperties makeXPathExpression(String xpath, String defaultUri, List<PropertyDescriptor<?>> properties) {
        final IndependentContext xpathStaticContext = new IndependentContext(configuration);
        xpathStaticContext.declareNamespace("fn", NamespaceUri.FN);
        xpathStaticContext.setDefaultElementNamespace(NamespaceUri.of(defaultUri));

        for (XPathFunctionDefinition xpathFun : xpathHandler.getRegisteredExtensionFunctions()) {
            ExtensionFunctionDefinition fun = new SaxonExtensionFunctionDefinitionAdapter(xpathFun);
            StructuredQName qname = fun.getFunctionQName();
            xpathStaticContext.declareNamespace(qname.getPrefix(), qname.getNamespaceUri());
            this.configuration.registerExtensionFunction(fun);
        }

        Map<PropertyDescriptor<?>, XPathVariable> xpathVariables = declareXPathVariables(properties, xpathStaticContext);

        try {
            final XPathEvaluator xpathEvaluator = new XPathEvaluator(configuration);
            xpathEvaluator.setStaticContext(xpathStaticContext);
            XPathExpression expression = xpathEvaluator.createExpression(xpath);
            return new XPathExpressionWithProperties(
                expression,
                xpathVariables
            );
        } catch (final XPathException e) {
            throw new ContextedRuntimeException(e)
                .addContextValue("XPath", xpath);
        }
    }

    private Map<PropertyDescriptor<?>, XPathVariable> declareXPathVariables(List<PropertyDescriptor<?>> accessibleProperties, XPathStaticContext xpathStaticContext) {
        Map<PropertyDescriptor<?>, XPathVariable> xpathVariables = new HashMap<>();
        for (final PropertyDescriptor<?> propertyDescriptor : accessibleProperties) {
            final String name = propertyDescriptor.name();
            if (!isExcludedProperty(name)) {
                final XPathVariable xpathVariable = xpathStaticContext.declareVariable(null, name);
                xpathVariables.put(propertyDescriptor, xpathVariable);
            }
        }
        return Collections.unmodifiableMap(xpathVariables);
    }

    private boolean isExcludedProperty(String name) {
        return "xpath".equals(name)
               || "defaultNsUri".equals(name)
               || "violationSuppressRegex".equals(name)
               || "violationSuppressXPath".equals(name);
    }

    @Override
    public String toString() {
        return xpath;
    }

    public List<Node> evaluate(RootXmlNode root, PropertySource propertyValues) {
        DocumentWrapper wrapper = getSaxonDomWrapper(root);

        try {
            List<Node> result = new ArrayList<>();
            for (Item item : this.xpathExpression.evaluate(wrapper, propertyValues)) {
                if (item instanceof AbstractNodeWrapper) {
                    AbstractNodeWrapper nodeInfo = (AbstractNodeWrapper) item;
                    Object domNode = nodeInfo.getUnderlyingNode();
                    if (domNode instanceof org.w3c.dom.Node) {
                        XmlNode wrapped = root.wrap((org.w3c.dom.Node) domNode);
                        result.add(wrapped);
                    }
                }
            }
            return result;
        } catch (XPathException e) {
            throw new ContextedRuntimeException(e)
                .addContextValue("XPath", xpath);
        }

    }

    private DocumentWrapper getSaxonDomWrapper(RootXmlNode node) {
        DataMap<DataKey<?, ?>> userMap = node.getUserMap();
        if (userMap.isSet(SAXON_DOM_WRAPPER)) {
            return userMap.get(SAXON_DOM_WRAPPER);
        }
        Document domRoot = node.getNode();
        DocumentWrapper wrapper = new DocumentWrapper(
            domRoot, domRoot.getBaseURI(), configuration
        );
        userMap.set(SAXON_DOM_WRAPPER, wrapper);
        return wrapper;
    }

    static final class XPathExpressionWithProperties {

        final XPathExpression expr;
        final Map<PropertyDescriptor<?>, XPathVariable> xpathVariables;

        XPathExpressionWithProperties(XPathExpression expr, Map<PropertyDescriptor<?>, XPathVariable> xpathVariables) {
            this.expr = expr;
            this.xpathVariables = xpathVariables;
        }

        private List<Item> evaluate(final DocumentWrapper elementNode, PropertySource properties) throws XPathException {
            XPathDynamicContext dynamicContext = createDynamicContext(elementNode, properties);
            return expr.evaluate(dynamicContext);
        }

        private XPathDynamicContext createDynamicContext(final DocumentWrapper elementNode, PropertySource properties) throws XPathException {
            final XPathDynamicContext dynamicContext = expr.createDynamicContext(elementNode.getRootNode());

            // Set variable values on the dynamic context
            for (final Entry<PropertyDescriptor<?>, XPathVariable> entry : xpathVariables.entrySet()) {
                AtomicSequence saxonValue = getSaxonValue(properties, entry);
                XPathVariable variable = entry.getValue();
                try {
                    dynamicContext.setVariable(variable, saxonValue);
                } catch (XPathException e) {
                    throw new ContextedRuntimeException(e)
                        .addContextValue("Variable", variable);
                }
            }
            return dynamicContext;
        }

        private static AtomicSequence getSaxonValue(PropertySource properties, Entry<PropertyDescriptor<?>, XPathVariable> entry) {
            Object value = properties.getProperty(entry.getKey());
            Objects.requireNonNull(value, "null property value for " + entry.getKey());
            return DomainConversion.convert(value);
        }
    }

}
