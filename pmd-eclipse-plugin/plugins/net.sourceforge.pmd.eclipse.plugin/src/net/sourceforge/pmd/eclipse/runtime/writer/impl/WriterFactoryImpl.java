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

import net.sourceforge.pmd.eclipse.runtime.writer.IAstWriter;
import net.sourceforge.pmd.eclipse.runtime.writer.IRuleSetWriter;
import net.sourceforge.pmd.eclipse.runtime.writer.IWriterFactory;

/**
 * The writer factory produces writers such as the one for the ruleset file.
 * This class is the abstract base class for writer factories.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/05/22 21:37:35  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 *
 */
public class WriterFactoryImpl implements IWriterFactory {

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.writer.IWriterFactory#getRuleSetWriter()
     */
    public IRuleSetWriter getRuleSetWriter() {
        return new RuleSetWriterImpl();
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.writer.IWriterFactory#getAstWriter()
     */
    public IAstWriter getAstWriter() {
        return new AstWriterImpl();
    }
}
