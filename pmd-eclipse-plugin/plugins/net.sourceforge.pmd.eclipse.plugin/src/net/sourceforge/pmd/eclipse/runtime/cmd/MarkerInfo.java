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
package net.sourceforge.pmd.eclipse.runtime.cmd;

/**
 * This class is intended to hold informations for future marker creation.
 * 
 * @author Philippe Herlin
 *
 */
public class MarkerInfo {
	
    private final String type;
    private String[] attributeNames;
    private Object[] attributeValues;
    
    public MarkerInfo(String theType) {
    	type = theType;
    }
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

}
