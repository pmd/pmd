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
package net.sourceforge.pmd.runtime.cmd;

/**
 * This class is intended to hold informations for future marker creation.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/05/22 21:37:35  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.2  2003/11/30 22:57:43  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.1.2.1  2003/11/03 14:40:17  phherlin
 * Refactoring to remove usage of Eclipse internal APIs
 *
 */
public class MarkerInfo {
    private String type;
    private String[] attributeNames;
    private Object[] attributeValues;
    
    /**
     * @return
     */
    public String[] getAttributeNames() {
        return attributeNames;
    }

    /**
     * @return
     */
    public Object[] getAttributeValues() {
        return attributeValues;
    }

    /**
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * @param strings
     */
    public void setAttributeNames(String[] strings) {
        attributeNames = strings;
    }

    /**
     * @param objects
     */
    public void setAttributeValues(Object[] objects) {
        attributeValues = objects;
    }

    /**
     * @param string
     */
    public void setType(String string) {
        type = string;
    }

}
