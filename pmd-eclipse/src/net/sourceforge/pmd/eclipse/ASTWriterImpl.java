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
package net.sourceforge.pmd.eclipse;

import java.io.IOException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.SimpleNode;

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
 * Revision 1.1  2003/10/27 20:14:13  phherlin
 * Refactoring AST generation. Using a ASTWriter.
 *
 */
public class ASTWriterImpl implements ASTWriter {

    /**
     * @see net.sourceforge.pmd.eclipse.ASTWriter#write(java.io.Writer, net.sourceforge.pmd.ast.ASTCompilationUnit)
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
     * Return a default node element
     * @param doc the generated document
     * @param simpleNode a simple node
     * @return a default node element
     */
    private Element getElement(Document doc, SimpleNode simpleNode) {
        Element simpleNodeElement = doc.createElement(simpleNode.toString());
        simpleNodeElement.setAttribute("beginColumn", String.valueOf(simpleNode.getBeginColumn()));
        simpleNodeElement.setAttribute("beginLine", String.valueOf(simpleNode.getBeginLine()));
        simpleNodeElement.setAttribute("endColumn", String.valueOf(simpleNode.getEndColumn()));
        simpleNodeElement.setAttribute("endLine", String.valueOf(simpleNode.getEndLine()));
        
        if (simpleNode.getImage() != null) {
            simpleNodeElement.setAttribute("image", simpleNode.getImage());
        }

        for (int i = 0; i < simpleNode.jjtGetNumChildren(); i++) {
            Element element = getElement(doc, (SimpleNode) simpleNode.jjtGetChild(i));
            simpleNodeElement.appendChild(element);
        }
        
        return simpleNodeElement;
    }

    /**
     * Return a type element
     * @param doc the generated document
     * @param type a type node
     * @return a type element
     */    
    private Element getElement(Document doc, ASTType type) {
        Element typeElement = getElement(doc, (SimpleNode) type);
        if (type.isArray()) {
            typeElement.setAttribute("isArray", "true");
            typeElement.setAttribute("dimensions", String.valueOf(type.getDimensions()));
        }

        return typeElement;
    }

}
