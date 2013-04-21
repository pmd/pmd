/*
 * Created on 18 juin 2006
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

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for Properties object
 * 
 * @author Herlin
 * 
 */

public class PropertiesTest {

  /**
   * A new properties object must have its set of properties not null and
   * empty
   * 
   */
  @Test
  public void testDefaults() {
    final Properties p = new Properties();
    Assert.assertNotNull("Properties set cannot be null", p.getProperties());
    Assert.assertEquals("Properties set should be empty", 0, p.getProperties().size());
  }

  /**
   * Obviously an instance is equals to itself
   * 
   */
  @Test
  public void testEquals1() {
    final Properties p = new Properties();
    Assert.assertEquals("An instance should be equals to itself", p, p);
  }

  /**
   * 2 different properties instances are equals if their set are the same
   */
  @Test
  public void testEquals2() {
    final Properties p1 = new Properties();
    final Set set = new HashSet();
    set.add("foo");
    p1.setProperties(set);
    final Properties p2 = new Properties();
    p2.setProperties(set);

    Assert.assertEquals("2 different properties instances are equals if their set are the same", p1, p2);
  }

  /**
   * 2 different properties instances are equals if their set are the same.
   * same as previous one but with a legal properties set.
   */
  @Test
  public void testEquals3() {
    final Property prop = new Property();
    prop.setName("p1");
    prop.setValue("value1");
    final Set set = new HashSet();
    set.add(prop);

    final Properties p1 = new Properties();
    p1.setProperties(set);
    final Properties p2 = new Properties();
    p2.setProperties(set);

    Assert.assertEquals("2 different properties instances are equals if their set are the same", p1, p2);
  }

  /**
   * 2 different properties instances are equals if their set are the same.
   * same as previous one but with 2 differents sets
   */
  @Test
  public void testEquals4() {
    final Property prop = new Property();
    prop.setName("p1");
    prop.setValue("value1");

    final Set set1 = new HashSet();
    set1.add(prop);

    final Set set2 = new HashSet();
    set2.add(prop);

    final Properties p1 = new Properties();
    p1.setProperties(set1);
    final Properties p2 = new Properties();
    p2.setProperties(set2);

    Assert.assertEquals("2 different properties instances are equals if their set are the same", p1, p2);
  }

  /**
   * 2 different properties instances are equals if their set are the same.
   * same as previous one but with 2 differents instances of property
   * objets.
   */
  @Test
  public void testEquals5() {
    final Property prop1 = new Property();
    prop1.setName("p1");
    prop1.setValue("value1");

    final Property prop2 = new Property();
    prop2.setName("p1");
    prop2.setValue("value1");

    final Set set1 = new HashSet();
    set1.add(prop1);

    final Set set2 = new HashSet();
    set2.add(prop2);

    final Properties p1 = new Properties();
    p1.setProperties(set1);
    final Properties p2 = new Properties();
    p2.setProperties(set2);

    Assert.assertEquals("2 different properties instances are equals if their set are the same", p1, p2);
  }

  /**
   * 2 different properties instances are different if their set are the
   * different
   */
  @Test
  public void testEquals6() {
    final Property prop1 = new Property();
    prop1.setName("p1");
    prop1.setValue("value1");

    final Property prop2 = new Property();
    prop2.setName("p2");
    prop2.setValue("value2");

    final Set set1 = new HashSet();
    set1.add(prop1);

    final Set set2 = new HashSet();
    set1.add(prop2);

    final Properties p1 = new Properties();
    p1.setProperties(set1);
    final Properties p2 = new Properties();
    p2.setProperties(set2);

    Assert.assertFalse("2 different properties instances are different if their set are the different", p1.equals(p2));
  }

  /**
   * A properties Object cannot be equals to null
   * 
   */
  @Test
  public void testEquals7() {
    final Property prop1 = new Property();
    prop1.setName("p1");
    prop1.setValue("value1");

    final Set set1 = new HashSet();
    set1.add(prop1);

    final Properties p1 = new Properties();
    p1.setProperties(set1);

    Assert.assertNotNull("A properties object cannot be equals to null", p1);
  }

  /**
   * A properties Object cannot be equals to any other objects
   * 
   */
  @Test
  public void testEquals8() {
    final Property prop1 = new Property();
    prop1.setName("p1");
    prop1.setValue("value1");

    final Set set1 = new HashSet();
    set1.add(prop1);

    final Properties p1 = new Properties();
    p1.setProperties(set1);

    Assert.assertFalse("A properties Object cannot be equals to any other objects", p1.equals(prop1));
    Assert.assertFalse("A properties Object cannot be equals to any other objects", p1.equals(set1));
  }

  /**
   * 2 equal properties must have the same hashCode
   * 
   */
  @Test
  public void testHashCode1() {
    final Property prop1 = new Property();
    prop1.setName("p1");
    prop1.setValue("value1");

    final Property prop2 = new Property();
    prop2.setName("p1");
    prop2.setValue("value1");

    final Set set1 = new HashSet();
    set1.add(prop1);

    final Set set2 = new HashSet();
    set2.add(prop2);

    final Properties p1 = new Properties();
    p1.setProperties(set1);
    final Properties p2 = new Properties();
    p2.setProperties(set2);

    Assert.assertEquals("2 equal properties must have the same hashCode", p1.hashCode(), p2.hashCode());
  }

  /**
   * 2 different properties must have the different hashCode
   * 
   */
  @Test
  public void testHashCode2() {
    final Property prop1 = new Property();
    prop1.setName("p1");
    prop1.setValue("value1");

    final Property prop2 = new Property();
    prop2.setName("p2");
    prop2.setValue("value2");

    final Set set1 = new HashSet();
    set1.add(prop1);

    final Set set2 = new HashSet();
    set2.add(prop2);

    final Properties p1 = new Properties();
    p1.setProperties(set1);
    final Properties p2 = new Properties();
    p2.setProperties(set2);

    Assert.assertFalse("2 different properties must have the different hashCode", p1.hashCode() == p2.hashCode());
  }

  /**
   * Setting any set as properties is allowed for now !
   * 
   */
  @Test
  public void testSetProperties1() {
    try {
      final Properties p = new Properties();
      final Set set = new HashSet();
      set.add("foo");
      p.setProperties(set);
    }
    catch (final IllegalArgumentException e) {
      Assert.fail("Setting any set as properties is allowed for now!");
    }
  }

  /**
   * Setting a null set as properties is illegal
   * 
   */
  @Test
  public void testSetProperties2() {
    try {
      final Properties p = new Properties();
      p.setProperties(null);
      Assert.fail("Setting a null set as properties is illegal");
    }
    catch (final IllegalArgumentException e) {
      // success!
    }
  }
}
