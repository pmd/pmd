/*
 * Created on 17 juin 2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.core.rulesets.vo;

import junit.framework.TestCase;

/**
 * Unit tests of class Property
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/06/21 23:06:52  phherlin
 * Move the new rule sets management to the core plugin instead of the runtime.
 * Continue the development.
 *
 * Revision 1.1  2006/06/18 22:29:50  phherlin
 * Begin refactoring the unit tests for the plugin
 *
 * 
 */

public class PropertyTest extends TestCase {

    /**
     * A new property objet has its name and value not null and assigned to an
     * empty string.
     * 
     */
    public void testDefaults() {
        Property p = new Property();
        assertNotNull("Name must not be null", p.getName());
        assertTrue("Name must be an empty string", p.getName().length() == 0);
        assertNotNull("Value must not be null", p.getValue());
        assertTrue("Value must be an empty string", p.getValue().length() == 0);
    }

    /**
     * Assigning any string to name is legal.
     * 
     */
    public void testSetName1() {
        Property p = new Property();
        p.setName("any string");
        assertEquals("Name can be assigned any string", "any string", p.getName());
    }

    /**
     * Assigning null to name is illegal.
     * 
     */
    public void testSetName2() {
        try {
            Property p = new Property();
            p.setName(null);
            fail("Assigning null to name is illegal");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    /**
     * Assigning any string to value is legal.
     * 
     */
    public void testSetValue1() {
        Property p = new Property();
        p.setValue("any string");
        assertEquals("Value can be assigned any string", "any string", p.getValue());
    }

    /**
     * Assigning null to Value is illegal.
     * 
     */
    public void testSetValue2() {
        try {
            Property p = new Property();
            p.setValue(null);
            fail("Assigning null to value is illegal");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    /**
     * 2 properties are equals if their names and value are equal
     * 
     */
    public void testEquals1() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        Property p2 = new Property();
        p2.setName("p1");
        p2.setValue("value1");
        
        assertEquals("2 properties must be equals if their names and values are equals", p1, p2);
    }

    /**
     * 2 properties are diffrent if their names are different.
     * 
     */
    public void testEquals2() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        Property p2 = new Property();
        p2.setName("p2");
        p2.setValue("value1");
        
        assertFalse("2 properties must be different if their names are different", p1.equals(p2));
    }

    /**
     * 2 properties are diffrent if their value are different.
     * 
     */
    public void testEquals3() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        Property p2 = new Property();
        p2.setName("p1");
        p2.setValue("value2");
        
        assertFalse("2 properties must be different if their values are different", p1.equals(p2));
    }
    
    /**
     * A property is always different than null
     *
     */
    public void testEquals4() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        assertFalse("A property cannot be equals to null", p1.equals(null));
    }
    
    /**
     * Any property objet is always different that any other objets
     *
     */
    public void testEquals5() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        assertFalse("Property object must be different that any other object", p1.equals("p1"));
    }
    
    /**
     * Obviously a property object is equals to itself
     *
     */
    public void testEquals6() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        assertEquals("A property object is equals to itself", p1, p1);
    }
    
    /**
     * 2 equal properties must have the same hashCode
     *
     */
    public void testHashCode1() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        Property p2 = new Property();
        p2.setName("p1");
        p2.setValue("value1");
        
        assertTrue("2 equal properties must have the same hashCode", p1.hashCode() == p2.hashCode());
    }

    /**
     * 2 different properties must have the different hashCodes.
     *
     */
    public void testHashCode2() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        Property p2 = new Property();
        p2.setName("p2");
        p2.setValue("value1");
        
        assertFalse("2 different properties should have different hashCodes", p1.hashCode() == p2.hashCode());
    }

    /**
     * 2 different properties must have the different hashCodes.
     *
     */
    public void testHashCode3() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        Property p2 = new Property();
        p2.setName("p1");
        p2.setValue("value2");
        
        assertFalse("2 different properties should have different hashCodes", p1.hashCode() == p2.hashCode());
    }

    /**
     * 2 different properties must have the different hashCodes.
     *
     */
    public void testHashCode4() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        Property p2 = new Property();
        p2.setName("p2");
        p2.setValue("value2");
        
        assertFalse("2 different properties should have different hashCodes", p1.hashCode() == p2.hashCode());
    }

}
