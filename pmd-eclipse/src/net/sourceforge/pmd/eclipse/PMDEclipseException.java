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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * An exception to encapsulate exceptions thrown by various plug-in components
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2003/11/30 22:57:43  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.1.2.1  2003/10/29 13:22:33  phherlin
 * Fix JDK1.3 runtime problem (Thanks to Eduard Naum)
 *
 * Revision 1.1  2003/10/14 21:56:47  phherlin
 * Creation
 *
 */
public class PMDEclipseException extends Exception {
    private Throwable cause;   // Waiting for JDK 1.4 !

    /**
     * Default Constructor
     */
    public PMDEclipseException() {
        super();
    }

    /**
     * Constructor with a message
     * @param message exception message
     */
    public PMDEclipseException(String message) {
        super(message);
    }

    /**
     * Constructor with a message and a root exception
     * @param message exception message
     * @param cause root exception
     */
    public PMDEclipseException(String message, Throwable cause) {
        super(message);
        setCause(cause);
    }

    /**
     * Constructor with a root exception
     * @param cause a root exception
     */
    public PMDEclipseException(Throwable cause) {
        setCause(cause);
    }

    /**
     * @return
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * @param throwable
     */
    public void setCause(Throwable throwable) {
        cause = throwable;
    }

    /**
     * @see java.lang.Throwable#printStackTrace()
     */
    public void printStackTrace() {
        super.printStackTrace();
        if (getCause() != null) {
            System.err.println("Caused by:");
            getCause().printStackTrace();
        }
    }

    /**
     * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
     */
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        if (getCause() != null) {
            s.println("Caused by:");
            getCause().printStackTrace(s);
        }
    }

    /**
     * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
     */
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        if (getCause() != null) {
            s.println("Caused by:");
            getCause().printStackTrace(s);
        }
    }

}
