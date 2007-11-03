/*
 * Created on 18 juin 2006
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
import net.sourceforge.pmd.RuleSetNotFoundException;

/**
 * Unit tests for class Rule
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

public class RuleTest extends TestCase {

    /**
     * Asserts the defaults of a new rule object
     *
     */
    public void testDefaults() {
        Rule r = new Rule();
        assertNotNull("The ref attribute must not be null", r.getRef());
        assertEquals("The ref attribute should be empty", 0, r.getRef().trim().length());
        assertNull("The message attribute should be null", r.getMessage());
        assertNull("The priority attribute should be null", r.getPriority());
        assertNull("The properties attribute should be null", r.getProperties());
        assertNull("The pmd rule attribute should be null", r.getPmdRule());
    }
    
    /**
     * Setting a null ref attribute is illegal
     *
     */
    public void testSetRef1() {
        try {
            Rule r = new Rule();
            r.setRef(null);
            fail("Setting a null ref attribute is illegal");
        } catch (IllegalArgumentException e) {
            // success
        }
    }
    
    /**
     * Setting an empty ref attribute is illegal
     *
     */
    public void testSetRef2() {
        try {
            Rule r = new Rule();
            r.setRef("");
            fail("Setting an empty string for the ref attribute is illegal");
        } catch (IllegalArgumentException e) {
            // success
        }
    }
    
    /**
     * Setting an empty ref attribute is illegal.
     * Test with blank characters.
     *
     */
    public void testSetRef3() {
        try {
            Rule r = new Rule();
            r.setRef(" \r\n");
            fail("Setting an empty string for the ref attribute is illegal");
        } catch (IllegalArgumentException e) {
            // success
        }
    }
    
    /**
     * To be sure, a ref attribute should be assigned any string
     *
     */
    public void testSetRef4() {
        try {
            Rule r = new Rule();
            r.setRef("any string");
        } catch (IllegalArgumentException e) {
            fail("It should be legal to set any string to ref attribute");
        }
    }
    
    /**
     * Setting a null PMD Rule is illegal
     *
     */
    public void testSetPmdRule1() {
        try {
            Rule r = new Rule();
            r.setPmdRule(null);
            fail("Setting a null PMD Rule is illegal");
        } catch (IllegalArgumentException e) {
            // success
        }
    }
    
    /**
     * Setting a any PMD Rule is legal even if the reference is
     * not the same (no check for now).
     * 
     * @throws RuleSetNotFoundException 
     *
     */
    public void testSetPmdRule2() throws RuleSetNotFoundException {
        try {
            Rule r = new Rule();
            r.setPmdRule(TestManager.getRule(0));
        } catch (IllegalArgumentException e) {
            fail("Setting any PMD Rule is legal");
        }
    }
    
    /**
     * an instance of a Rule object is equal to itself
     */
    public void testEquals1() {
        Rule r = new Rule();
        r.setRef("A reference");
        assertEquals("A instance of Rule is equals to itself", r, r);
    }
    
    /**
     * a rule cannot be equals to null
     */
    public void testEquals2() {
        Rule r = new Rule();
        r.setRef("A reference");
        assertNotNull("A rule cannot be equal to null", r);
    }
    
    /**
     * To be equals, rules must share the same ref, priority and properties.
     *
     */
    public void testEquals3() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        
        Properties s1 = new Properties();
        s1.getProperties().add(p1);
        
        Property p2 = new Property();
        p2.setName("p1");
        p2.setValue("value1");
        
        Properties s2 = new Properties();
        s2.getProperties().add(p2);

        Rule r1 = new Rule();
        r1.setRef("ref to a rule");
        r1.setProperties(s1);
        r1.setPriority(Priority.LEVEL3);
        
        Rule r2 = new Rule();
        r2.setRef("ref to a rule");
        r2.setProperties(s2);
        r2.setPriority(Priority.LEVEL3);
        r2.setMessage("Message doesn't make the difference");

        assertEquals("These 2 rules must be equal", r1, r2);
        
    }
    
    /**
     * Rules with a different ref attribute are not equals
     *
     */
    public void testEquals4() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        
        Properties s1 = new Properties();
        s1.getProperties().add(p1);
        
        Property p2 = new Property();
        p2.setName("p1");
        p2.setValue("value1");
        
        Properties s2 = new Properties();
        s2.getProperties().add(p2);

        Rule r1 = new Rule();
        r1.setRef("ref to a rule");
        r1.setProperties(s1);
        r1.setPriority(Priority.LEVEL3);
        
        Rule r2 = new Rule();
        r2.setRef("ref to another rule");
        r2.setProperties(s2);
        r2.setPriority(Priority.LEVEL3);
        r2.setMessage("Message doesn't make the difference");

        assertFalse("Rules with different ref attribute are different", r1.equals(r2));
        
    }
    
    /**
     * Rules with a different priority are not equals
     *
     */
    public void testEquals5() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        
        Properties s1 = new Properties();
        s1.getProperties().add(p1);
        
        Property p2 = new Property();
        p2.setName("p1");
        p2.setValue("value1");
        
        Properties s2 = new Properties();
        s2.getProperties().add(p2);

        Rule r1 = new Rule();
        r1.setRef("ref to a rule");
        r1.setProperties(s1);
        r1.setPriority(Priority.LEVEL3);
        
        Rule r2 = new Rule();
        r2.setRef("ref to a rule");
        r2.setProperties(s2);
        r2.setPriority(Priority.LEVEL1);
        r2.setMessage("Message doesn't make the difference");

        assertFalse("Rules with different priority are different", r1.equals(r2));
        
    }
    
    /**
     * Rules with different properties are not equals
     *
     */
    public void testEquals6() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        
        Properties s1 = new Properties();
        s1.getProperties().add(p1);
        
        Property p2 = new Property();
        p2.setName("p1");
        p2.setValue("value2");
        
        Properties s2 = new Properties();
        s2.getProperties().add(p2);

        Rule r1 = new Rule();
        r1.setRef("ref to a rule");
        r1.setProperties(s1);
        r1.setPriority(Priority.LEVEL3);
        
        Rule r2 = new Rule();
        r2.setRef("ref to a rule");
        r2.setProperties(s2);
        r2.setPriority(Priority.LEVEL3);
        r2.setMessage("Message doesn't make the difference");

        assertFalse("Rules with different properties are different", r1.equals(r2));
        
    }
    
    /**
     * Equal rules have the same hash code
     *
     */
    public void testHashCode1() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        
        Properties s1 = new Properties();
        s1.getProperties().add(p1);
        
        Property p2 = new Property();
        p2.setName("p1");
        p2.setValue("value1");
        
        Properties s2 = new Properties();
        s2.getProperties().add(p2);

        Rule r1 = new Rule();
        r1.setRef("ref to a rule");
        r1.setProperties(s1);
        r1.setPriority(Priority.LEVEL3);
        
        Rule r2 = new Rule();
        r2.setRef("ref to a rule");
        r2.setProperties(s2);
        r2.setPriority(Priority.LEVEL3);
        r2.setMessage("Message doesn't make the difference");

        assertEquals("Equal rules have the same hashcode", r1.hashCode(), r2.hashCode());
        
    }
    
    /**
     * Different rules should have different hashCode
     *
     */
    public void testHashCode2() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        
        Properties s1 = new Properties();
        s1.getProperties().add(p1);
        
        Property p2 = new Property();
        p2.setName("p1");
        p2.setValue("value1");
        
        Properties s2 = new Properties();
        s2.getProperties().add(p2);

        Rule r1 = new Rule();
        r1.setRef("ref to a rule");
        r1.setProperties(s1);
        r1.setPriority(Priority.LEVEL3);
        
        Rule r2 = new Rule();
        r2.setRef("ref to another rule");
        r2.setProperties(s2);
        r2.setPriority(Priority.LEVEL3);
        r2.setMessage("Message doesn't make the difference");

        assertFalse("Different rules should have different hash code", r1.hashCode() == r2.hashCode());
        
    }
    
    /**
     * Different rules should have different hash code
     *
     */
    public void testHashCode3() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        
        Properties s1 = new Properties();
        s1.getProperties().add(p1);
        
        Property p2 = new Property();
        p2.setName("p1");
        p2.setValue("value1");
        
        Properties s2 = new Properties();
        s2.getProperties().add(p2);

        Rule r1 = new Rule();
        r1.setRef("ref to a rule");
        r1.setProperties(s1);
        r1.setPriority(Priority.LEVEL3);
        
        Rule r2 = new Rule();
        r2.setRef("ref to a rule");
        r2.setProperties(s2);
        r2.setPriority(Priority.LEVEL1);
        r2.setMessage("Message doesn't make the difference");

        assertFalse("Different rules should have different hash code", r1.hashCode() == r2.hashCode());
        
    }
    
    /**
     * Different rules should have different hash code
     *
     */
    public void testHashCode4() {
        Property p1 = new Property();
        p1.setName("p1");
        p1.setValue("value1");
        
        Properties s1 = new Properties();
        s1.getProperties().add(p1);
        
        Property p2 = new Property();
        p2.setName("p1");
        p2.setValue("value2");
        
        Properties s2 = new Properties();
        s2.getProperties().add(p2);

        Rule r1 = new Rule();
        r1.setRef("ref to a rule");
        r1.setProperties(s1);
        r1.setPriority(Priority.LEVEL3);
        
        Rule r2 = new Rule();
        r2.setRef("ref to a rule");
        r2.setProperties(s2);
        r2.setPriority(Priority.LEVEL3);
        r2.setMessage("Message doesn't make the difference");

        assertFalse("Different rules should have different hash code", r1.hashCode() == r2.hashCode());
        
    }
}
