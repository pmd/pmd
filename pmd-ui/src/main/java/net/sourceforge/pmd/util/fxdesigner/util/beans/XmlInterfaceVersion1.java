/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.ClassUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * V0, really.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public class XmlInterfaceVersion1 extends XmlInterface {


    // names used in the Xml schema
    private static final String SCHEMA_NODE_ELEMENT = "node";
    private static final String SCHEMA_NODESEQ_ELEMENT = "nodeseq";
    private static final String SCHEMA_NODE_CLASS_ATTRIBUTE = "class";
    private static final String SCHEMA_PROPERTY_ELEMENT = "property";
    private static final String SCHEMA_PROPERTY_NAME = "name";
    private static final String SCHEMA_PROPERTY_TYPE = "type";


    public XmlInterfaceVersion1(int revisionNumber) {
        super(revisionNumber);
    }


    private List<Element> getChildrenByTagName(Element element, String tagName) {
        NodeList children = element.getChildNodes();
        List<Element> elts = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE && tagName.equals(children.item(i).getNodeName())) {
                elts.add((Element) children.item(i));
            }
        }

        return elts;
    }


    @Override
    protected SimpleBeanModelNode parseSettingsOwnerNode(Element nodeElement) {
        Class<?> clazz;
        try {
            clazz = Class.forName(nodeElement.getAttribute(SCHEMA_NODE_CLASS_ATTRIBUTE));
        } catch (ClassNotFoundException e) {
            return null;
        }

        SimpleBeanModelNode node = new SimpleBeanModelNode(clazz);

        for (Element setting : getChildrenByTagName(nodeElement, SCHEMA_PROPERTY_ELEMENT)) {
            parseSingleProperty(setting, node);
        }

        for (Element child : getChildrenByTagName(nodeElement, SCHEMA_NODE_ELEMENT)) {
            try {
                if (node.getChildrenByType().get(Class.forName(child.getAttribute(SCHEMA_NODE_CLASS_ATTRIBUTE))) == null) { // FIXME
                    node.addChild(parseSettingsOwnerNode(child));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        for (Element seq : getChildrenByTagName(nodeElement, SCHEMA_NODESEQ_ELEMENT)) {
            parseNodeSeq(seq, node);
        }

        return node;
    }


    private void parseSingleProperty(Element propertyElement, SimpleBeanModelNode owner) {
        String typeName = propertyElement.getAttribute(SCHEMA_PROPERTY_TYPE);
        String name = propertyElement.getAttribute(SCHEMA_PROPERTY_NAME);
        Class<?> type;
        try {
            type = ClassUtils.getClass(typeName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        ConvertUtils.convert(new Object());
        Object value = ConvertUtils.convert(propertyElement.getTextContent(), type);

        owner.addProperty(name, value, type);
    }


    private void parseNodeSeq(Element nodeSeq, SimpleBeanModelNode parent) {
        BeanModelNodeSeq<SimpleBeanModelNode> built = new BeanModelNodeSeq<>(nodeSeq.getAttribute(SCHEMA_PROPERTY_NAME));
        for (Element child : getChildrenByTagName(nodeSeq, SCHEMA_NODE_ELEMENT)) {
            built.addChild(parseSettingsOwnerNode(child));
        }
        parent.addChild(built);
    }


    @Override
    protected BeanNodeVisitor<Element> getDocumentMakerVisitor() {
        return new DocumentMakerVisitor();
    }


    public static class DocumentMakerVisitor extends BeanNodeVisitor<Element> {


        @Override
        public void visit(SimpleBeanModelNode node, Element parent) {
            Element nodeElement = parent.getOwnerDocument().createElement(SCHEMA_NODE_ELEMENT);
            nodeElement.setAttribute(SCHEMA_NODE_CLASS_ATTRIBUTE, node.getNodeType().getCanonicalName());

            for (Entry<String, Object> keyValue : node.getSettingsValues().entrySet()) {
                // I don't think the API is intended to be used like that
                // but ConvertUtils wouldn't use the convertToString methods
                // defined in the converters otherwise.
                // Even when a built-in converter is available, objects are
                // still converted with Object::toString which fucks up the
                // conversion...
                String value = (String) ConvertUtils.lookup(keyValue.getValue().getClass()).convert(String.class, keyValue.getValue());
                if (value == null) {
                    continue;
                }

                Element setting = parent.getOwnerDocument().createElement(SCHEMA_PROPERTY_ELEMENT);
                setting.setAttribute(SCHEMA_PROPERTY_NAME, keyValue.getKey());
                setting.setAttribute(SCHEMA_PROPERTY_TYPE, node.getSettingsTypes().get(keyValue.getKey()).getCanonicalName());
                setting.appendChild(parent.getOwnerDocument().createCDATASection(value));
                nodeElement.appendChild(setting);
            }

            parent.appendChild(nodeElement);
            super.visit(node, nodeElement);
        }


        @Override
        public void visit(BeanModelNodeSeq<?> node, Element parent) {
            Element nodeElement = parent.getOwnerDocument().createElement(SCHEMA_NODESEQ_ELEMENT);
            nodeElement.setAttribute(SCHEMA_PROPERTY_NAME, node.getPropertyName());
            parent.appendChild(nodeElement);
            super.visit(node, nodeElement);
        }
    }
}
