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
package net.sourceforge.pmd.ui.quickfix;

import net.sourceforge.pmd.quickfix.Fix;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

/**
 * Sample implementation of a fix that delete the line where the violation occurs.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2006/10/10 22:31:01  phherlin
 * Fix other PMD warnings
 *
 * Revision 1.1  2006/05/22 21:23:59  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.2  2003/11/30 22:57:43  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.1.2.2  2003/11/30 21:15:18  phherlin
 * Using Fix interface from PMD core
 *
 * Revision 1.1.2.1  2003/11/04 23:32:21  phherlin
 * Previewing Quick Fixes
 *
 */
public class DeleteLineFix implements Fix {

    /**
     * @see net.sourceforge.pmd.eclipse.Fix#fix(java.lang.String, int)
     */
    public String fix(String sourceCode, int lineNumber) {
        final Document document = new Document(sourceCode);
        try {
            final int offset = document.getLineOffset(lineNumber - 1);
            final int length = document.getLineLength(lineNumber - 1);
            document.replace(offset, length, "");
        } catch (BadLocationException e) { // NOPMD by Herlin on 11/10/06 00:20
            //ignoring that exception
        }
        
        return document.get();
    }

    /**
     * @see net.sourceforge.pmd.eclipse.Fix#getLabel()
     */
    public String getLabel() {
        return "Delete the line";
    }

}
