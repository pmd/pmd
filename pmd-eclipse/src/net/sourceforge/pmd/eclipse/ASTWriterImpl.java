/*
 * <copyright> Copyright 1997-2003 PMD for Eclipse Development team under
 * sponsorship of the Defense Advanced Research Projects Agency (DARPA).
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Cougaar Open Source License as published by DARPA on
 * the Cougaar Open Source Website (www.cougaar.org).
 * 
 * THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS PROVIDED "AS
 * IS" WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR IMPLIED, INCLUDING
 * (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE, AND WITHOUT ANY WARRANTIES AS TO NON-INFRINGEMENT.
 * IN NO EVENT SHALL COPYRIGHT HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF
 * DATA OR PROFITS, TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE
 * USE OR PERFORMANCE OF THE COUGAAR SOFTWARE.
 * 
 * </copyright>
 */
package net.sourceforge.pmd.eclipse;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.serialize.DOMSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implements a default AST Writer
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2003/10/30 23:36:29  phherlin
 * Fixing bugs
 * #819518 : AST writes out method return types incorrectly
 * #820241 : VariableDeclaration doesn't show variable modifiers
 * Revision 1.1 2003/10/27 20:14:13 phherlin
 * Refactoring AST generation. Using a ASTWriter.
 *  
 */
public class ASTWriterImpl implements ASTWriter {
    private static Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.ASTWriterImpl");

    /**
	 * @see net.sourceforge.pmd.eclipse.ASTWriter#write(java.io.Writer,
	 *      net.sourceforge.pmd.ast.ASTCompilationUnit)
	 */
    public void write(Writer writer, ASTCompilationUnit compilationUnit) throws PMDEclipseException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();

            Element compilationUnitElement = getElement(doc, compilationUnit);
            doc.appendChild(compilationUnitElement);

            OutputFormat outputFormat = new OutputFormat(doc, "UTF-8", true);
            DOMSerializer serializer = new XMLSerializer(writer, outputFormat);
            serializer.serialize(doc);

        } catch (DOMException e) {
            throw new PMDEclipseException(e);
        } catch (FactoryConfigurationError e) {
            throw new PMDEclipseException(e);
        } catch (ParserConfigurationException e) {
            throw new PMDEclipseException(e);
        } catch (IOException e) {
            throw new PMDEclipseException(e);
        }
    }

    /**
	 * Transform a ast node to a xml element
	 * 
	 * @param doc
	 *            the generated document
	 * @param simpleNode
	 *            a ast node
	 * @return a xml element
	 */
    private Element getElement(Document doc, SimpleNode simpleNode) {
        log.debug("creating element " + simpleNode);
        Element simpleNodeElement = doc.createElement(simpleNode.toString());

        addAttributes(simpleNodeElement, simpleNode);

        for (int i = 0; i < simpleNode.jjtGetNumChildren(); i++) {
            Node child = simpleNode.jjtGetChild(i);
            Element element = getElement(doc, (SimpleNode) child);
            simpleNodeElement.appendChild(element);
        }

        return simpleNodeElement;
    }

    /**
	 * Add attributes to element by introspecting the node. This way, the
	 * abstract tree can evolve indepently from the way it is persisted
	 * 
	 * @param element
	 *            a xml element
	 * @param simpleNode
	 *            a ast node
	 */
    private void addAttributes(Element element, SimpleNode simpleNode) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(simpleNode.getClass());
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < descriptors.length; i++) {
                String attributeName = descriptors[i].getName();
                if (!attributeName.equals("class") && !attributeName.equals("scope")) {
                    log.debug("   processing attribute " + descriptors[i].getName());
                    Method getter = descriptors[i].getReadMethod();
                    Object result = getter.invoke(simpleNode, null);
                    if (result != null) {
                        log.debug("      added");
                        element.setAttribute(descriptors[i].getName(), result.toString());
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Error introspecting properties. Ignored", e);
        }
    }

}
