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

/**
 * The writer factory produces writers such as the one for the ruleset file.
 * This class is the abstract base class for writer factories.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2003/10/27 20:14:13  phherlin
 * Refactoring AST generation. Using a ASTWriter.
 *
 * Revision 1.1  2003/10/16 22:26:37  phherlin
 * Fix bug #810858.
 * Complete refactoring of rule set generation. Using a DOM tree and the Xerces 2 serializer.
 *
 */
public abstract class WriterAbstractFactory implements WriterFactory {
    private static WriterAbstractFactory defaultWriterFactory = new DefaultWriterFactory();

    /**
     * Return a writer factory. The current implementation return the default writer
     * factory
     * @return a default writer factory
     */
    public static WriterAbstractFactory getFactory() {
        return defaultWriterFactory;
    }
    
    /**
     * Return a ruleset writer
     * @return a ruleset writer
     */
    public abstract RuleSetWriter getRuleSetWriter();

    /**
     * Return a ast writer
     * @return a ast writer
     */
    public abstract ASTWriter getASTWriter();

}
