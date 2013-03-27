/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RuntimeRulePropertiesAnalyzer {
    private static final String XPATH_RULE_CLASSNAME = "net.sourceforge.pmd.lang.rule.XPathRule";
    private ClassLoader cl;
    private Class<?> propertySource;
    private Class<?> propertyDesc;
    private Method nameMethod;
    private Method descMethod;
    private Method defaultValueMethod;
    private Field propertiesField;
    private Field propertiesValues;

    public RuntimeRulePropertiesAnalyzer(URL[] runtimeClasspath) {
	init(runtimeClasspath);
    }

    private void init(URL[] runtimeClasspath) {
	try {
	    cl = new URLClassLoader(runtimeClasspath);
	    propertySource = cl.loadClass("net.sourceforge.pmd.AbstractPropertySource");
	    propertyDesc = cl.loadClass("net.sourceforge.pmd.PropertyDescriptor");
	    nameMethod = propertyDesc.getDeclaredMethod("name");
	    descMethod = propertyDesc.getDeclaredMethod("description");
	    defaultValueMethod = propertyDesc.getDeclaredMethod("defaultValue");
	    propertiesField = propertySource.getDeclaredField("propertyDescriptors");
	    propertiesValues = propertySource.getDeclaredField("propertyValuesByDescriptor");
	    propertiesField.setAccessible(true);
	    propertiesValues.setAccessible(true);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Analyzes the class of the given rule definition to find the properties
     * this rule supports.
     * The properties are directly added to the rule node.
     * @param document the document, used to create new property nodes
     * @param rule the rule to analyze
     */
    public void analyze(Document document, Node rule) {
	Node classAttribute = rule.getAttributes().getNamedItem("class");
	if (classAttribute == null) {
	    // some rule definitions, like <rule ref="..."/> have no class attribute 
	    return;
	}
	String classAtt = classAttribute.getTextContent();
	if (XPATH_RULE_CLASSNAME.equals(classAtt)) {
	    // xpath rules are ignored - they have there properties defined already in the rule definition xml
	    return;
	}

	try {
	    Class<?> clazz = cl.loadClass(classAtt);
	    Object ruleInstance = clazz.newInstance();
	    @SuppressWarnings("rawtypes")
	    List properties = (List)propertiesField.get(ruleInstance);
	    @SuppressWarnings("rawtypes")
	    Map values = (Map)propertiesValues.get(ruleInstance);
	    Element propsElem = null;
	    NodeList ruleChilds = rule.getChildNodes();
	    for (int j = 0; j < ruleChilds.getLength(); j++) {
		Node item = ruleChilds.item(j);
		if (item.getNodeType() == Node.ELEMENT_NODE && "properties".equals(item.getNodeName())) {
		    propsElem = (Element)item;
		    break;
		}
	    }
	    if (propsElem == null) {
		propsElem = document.createElement("properties");
		rule.appendChild(propsElem);
	    }

	    for (Object o : properties) {
		Object value = values.get(o);
		if (value == null) {
		    value = defaultValueMethod.invoke(o);
		}

		Element propElem = document.createElement("property");
		propElem.setAttribute("name", (String)nameMethod.invoke(o));
		propElem.setAttribute("description", (String)descMethod.invoke(o));
		if (value != null) {
		    String valueString = String.valueOf(value);
		    if (value.getClass().isArray()) {
		        valueString = Arrays.toString((Object[])value);
		    }
		    propElem.setAttribute("value", valueString);
		}
		propsElem.appendChild(propElem);
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }
}
