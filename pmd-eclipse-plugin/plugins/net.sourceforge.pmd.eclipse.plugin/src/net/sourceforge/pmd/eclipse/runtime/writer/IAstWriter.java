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
package net.sourceforge.pmd.eclipse.runtime.writer;

import java.io.OutputStream;

import net.sourceforge.pmd.ast.ASTCompilationUnit;

/**
 * Interface of an AST Writer. An IAstWriter is an object used to "serialize" an
 * AST.
 * 
 * @author Philippe Herlin
 *  
 */
public interface IAstWriter {

    /**
	 * Serialize an AST into an output stream
	 * 
	 * @param outputStream
	 *            the target output
	 * @param compilationUnit
	 *            the compilation unit to serialize
	 * @throws WriterException
	 */
    void write(OutputStream outputStream, ASTCompilationUnit compilationUnit) throws WriterException;

}
