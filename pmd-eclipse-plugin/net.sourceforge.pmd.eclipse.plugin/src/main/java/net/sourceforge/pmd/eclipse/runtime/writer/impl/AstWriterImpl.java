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
package net.sourceforge.pmd.eclipse.runtime.writer.impl;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.FactoryConfigurationError;

import net.sourceforge.pmd.eclipse.runtime.writer.IAstWriter;
import net.sourceforge.pmd.eclipse.runtime.writer.WriterException;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;

import org.apache.xml.serialize.DOMSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

/**
 * Implements a default AST Writer
 *
 * @author Philippe Herlin
 *
 */
class AstWriterImpl implements IAstWriter {
    /**
     * @see net.sourceforge.pmd.eclipse.runtime.writer.IAstWriter#write(java.io.Writer, net.sourceforge.pmd.ast.ASTCompilationUnit)
     */
    public void write(OutputStream outputStream, ASTCompilationUnit compilationUnit) throws WriterException {
	try {
	    Document doc = compilationUnit.getAsDocument();
	    OutputFormat outputFormat = new OutputFormat(doc, "UTF-8", true);
	    outputFormat.setLineWidth(0);
	    DOMSerializer serializer = new XMLSerializer(outputStream, outputFormat);
	    serializer.serialize(doc);
	} catch (DOMException e) {
	    throw new WriterException(e);
	} catch (FactoryConfigurationError e) {
	    throw new WriterException(e);
	} catch (IOException e) {
	    throw new WriterException(e);
	}
    }
}
