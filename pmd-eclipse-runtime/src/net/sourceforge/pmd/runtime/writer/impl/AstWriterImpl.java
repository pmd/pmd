/*  
 * <copyright>  
 *  Copyright 1997-2003 PMD for Eclipse Development team
 *  under sponsorship of the Defense Advanced Research Projects  
 *  Agency (DARPA).  
 *   
 *  This program is free software; you can redistribute it and/or modify  
 *  it under the terms of the Cougaar Open Source License as published by  
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).   
 *   
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS   
 *  PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR   
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF   
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT   
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT   
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL   
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,   
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR   
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.   
 *   
 * </copyright>
 */
package net.sourceforge.pmd.runtime.writer.impl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.runtime.writer.IAstWriter;
import net.sourceforge.pmd.runtime.writer.WriterException;

import org.apache.log4j.Logger;
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
 * Revision 1.2  2006/06/20 21:01:49  phherlin
 * Enable PMD and fix error level violations
 *
 * Revision 1.1  2006/05/22 21:37:35  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.5  2006/01/26 23:57:55  phherlin
 * Fix NullPointerException and InvocationTargetException
 *
 * Revision 1.4  2003/12/18 23:58:37  phherlin
 * Fixing malformed UTF-8 characters in generated xml files
 *
 * Revision 1.3  2003/11/30 22:57:43  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.1.2.1  2003/10/30 23:23:01  phherlin
 * Fixing bugs
 * #819518 : AST writes out method return types incorrectly
 * #820241 : VariableDeclaration doesn't show variable modifiers
 *
 * Revision 1.1  2003/10/27 20:14:13  phherlin
 * Refactoring AST generation. Using a IAstWriter.
 *
 */
class AstWriterImpl implements IAstWriter {
    private static final Logger log = Logger.getLogger(AstWriterImpl.class);

    /**
     * @see net.sourceforge.pmd.runtime.writer.IAstWriter#write(java.io.Writer, net.sourceforge.pmd.ast.ASTCompilationUnit)
     */
    public void write(OutputStream outputStream, ASTCompilationUnit compilationUnit) throws WriterException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();

            Element compilationUnitElement = getElement(doc, compilationUnit);
            doc.appendChild(compilationUnitElement);

            OutputFormat outputFormat = new OutputFormat(doc, "UTF-8", true);
            outputFormat.setLineWidth(0);
            DOMSerializer serializer = new XMLSerializer(outputStream, outputFormat);
            serializer.serialize(doc);

        } catch (DOMException e) {
            throw new WriterException(e);
        } catch (FactoryConfigurationError e) {
            throw new WriterException(e);
        } catch (ParserConfigurationException e) {
            throw new WriterException(e);
        } catch (IOException e) {
            throw new WriterException(e);
        }
    }

    /**
     * Transform a ast node to a xml element
     * @param doc the generated document
     * @param simpleNode a ast node
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
     * Add attributes to element by introspecting the node. This way, the abstract
     * tree can evolve indepently from the way it is persisted
     * @param element a xml element
     * @param simpleNode a ast node
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
                    if (getter != null) {
                        try {
                            Object result = getter.invoke(simpleNode, null);
                            if (result != null) {
                                log.debug("      added");
                                element.setAttribute(descriptors[i].getName(), result.toString());
                            } else {
                                log.debug("      not added attribute is null");
                            }
                        } catch (InvocationTargetException e) {
                            log.debug("      not added calling getter has failed");
                        }
                    } else {
                        log.debug("      not added getter is null");
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Error introspecting properties. Ignored", e);
        }
    }

}
