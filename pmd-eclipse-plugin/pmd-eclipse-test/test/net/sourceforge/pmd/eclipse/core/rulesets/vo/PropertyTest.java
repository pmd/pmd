/*
 * 
 * Created on 17 juin 2006
 * 
 * Copyright (c) 2006, PMD for Eclipse Development Team All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. * Redistributions
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. * The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgement: "This product includes software developed in part by
 * support from the Defense Advanced Research Project Agency (DARPA)" *
 * Neither the name of "PMD for Eclipse Development Team" nor the names of
 * its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.eclipse.core.rulesets.vo;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests of class Property
 * 
 * @author Herlin
 * 
 */

public class PropertyTest {

  /**
   * A new property objet has its name and value not null and assigned to an
   * empty string.
   * 
   */
  @Test
  public void testDefaults() {
    final Property p = new Property();
    Assert.assertNotNull("Name must not be null", p.getName());
    Assert.assertTrue("Name must be an empty string", p.getName().length() == 0);
    Assert.assertNotNull("Value must not be null", p.getValue());
    Assert.assertTrue("Value must be an empty string", p.getValue().length() == 0);
  }

  /**
   * 2 properties are equals if their names and value are equal
   * 
   */
  @Test
  public void testEquals1() {
    final Property p1 = new Property();
    p1.setName("p1");
    p1.setValue("value1");
    final Property p2 = new Property();
    p2.setName("p1");
    p2.setValue("value1");

    Assert.assertEquals("2 properties must be equals if their names and values are equals", p1, p2);
  }

  /**
   * 2 properties are diffrent if their names are different.
   * 
   */
  @Test
  public void testEquals2() {
    final Property p1 = new Property();
    p1.setName("p1");
    p1.setValue("value1");
    final Property p2 = new Property();
    p2.setName("p2");
    p2.setValue("value1");

    Assert.assertFalse("2 properties must be different if their names are different", p1.equals(p2));
  }

  /**
   * 2 properties are diffrent if their value are different.
   * 
   */
  @Test
  public void testEquals3() {
    final Property p1 = new Property();
    p1.setName("p1");
    p1.setValue("value1");
    final Property p2 = new Property();
    p2.setName("p1");
    p2.setValue("value2");

    Assert.assertFalse("2 properties must be different if their values are different", p1.equals(p2));
  }

  /**
   * A property is always different than null
   * 
   */
  @Test
  public void testEquals4() {
    final Property p1 = new Property();
    p1.setName("p1");
    p1.setValue("value1");
    Assert.assertNotNull("A property cannot be equals to null", p1);
  }

  /**
   * Any property objet is always different that any other objets
   * 
   */
  @Test
  public void testEquals5() {
    final Property p1 = new Property();
    p1.setName("p1");
    p1.setValue("value1");
    Assert.assertFalse("Property object must be different that any other object", p1.equals("p1"));
  }

  /**
   * Obviously a property object is equals to itself
   * 
   */
  @Test
  public void testEquals6() {
    final Property p1 = new Property();
    p1.setName("p1");
    p1.setValue("value1");
    Assert.assertEquals("A property object is equals to itself", p1, p1);
  }

  /**
   * 2 equal properties must have the same hashCode
   * 
   */
  @Test
  public void testHashCode1() {
    final Property p1 = new Property();
    p1.setName("p1");
    p1.setValue("value1");
    final Property p2 = new Property();
    p2.setName("p1");
    p2.setValue("value1");

    Assert.assertTrue("2 equal properties must have the same hashCode", p1.hashCode() == p2.hashCode());
  }

  /**
   * 2 different properties must have the different hashCodes.
   * 
   */
  @Test
  public void testHashCode2() {
    final Property p1 = new Property();
    p1.setName("p1");
    p1.setValue("value1");
    final Property p2 = new Property();
    p2.setName("p2");
    p2.setValue("value1");

    Assert.assertFalse("2 different properties should have different hashCodes", p1.hashCode() == p2.hashCode());
  }

  /**
   * 2 different properties must have the different hashCodes.
   * 
   */
  @Test
  public void testHashCode3() {
    final Property p1 = new Property();
    p1.setName("p1");
    p1.setValue("value1");
    final Property p2 = new Property();
    p2.setName("p1");
    p2.setValue("value2");

    Assert.assertFalse("2 different properties should have different hashCodes", p1.hashCode() == p2.hashCode());
  }

  /**
   * 2 different properties must have the different hashCodes.
   * 
   */
  @Test
  public void testHashCode4() {
    final Property p1 = new Property();
    p1.setName("p1");
    p1.setValue("value1");
    final Property p2 = new Property();
    p2.setName("p2");
    p2.setValue("value2");

    Assert.assertFalse("2 different properties should have different hashCodes", p1.hashCode() == p2.hashCode());
  }

  /**
   * Assigning any string to name is legal.
   * 
   */
  @Test
  public void testSetName1() {
    final Property p = new Property();
    p.setName("any string");
    Assert.assertEquals("Name can be assigned any string", "any string", p.getName());
  }

  /**
   * Assigning null to name is illegal.
   * 
   */
  @Test
  public void testSetName2() {
    try {
      final Property p = new Property();
      p.setName(null);
      Assert.fail("Assigning null to name is illegal");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

  /**
   * Assigning any string to value is legal.
   * 
   */
  @Test
  public void testSetValue1() {
    final Property p = new Property();
    p.setValue("any string");
    Assert.assertEquals("Value can be assigned any string", "any string", p.getValue());
  }

  /**
   * Assigning null to Value is illegal.
   * 
   */
  @Test
  public void testSetValue2() {
    try {
      final Property p = new Property();
      p.setValue(null);
      Assert.fail("Assigning null to value is illegal");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

}
