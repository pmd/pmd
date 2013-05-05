/*
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
 * Unit tests of Priority object.
 * 
 * @author Herlin
 * 
 */

public class PriorityTest {

  /**
   * A default constructed priority is set to level 3
   * 
   */
  @Test
  public void testDefaultPriority() {
    Assert.assertEquals("Default priority is not set to 3", Priority.LEVEL3, new Priority());
  }

  /**
   * 2 different instances assigned different levels are not equals
   * 
   */
  @Test
  public void testEquals1() {
    final Priority p = new Priority();
    p.setPriorityValue(1);
    Assert.assertFalse("2 priorities with different levels (1:2) are not equals", Priority.LEVEL2.equals(p));
    Assert.assertFalse("2 priorities with different levels (1:3) are not equals", Priority.LEVEL3.equals(p));
    Assert.assertFalse("2 priorities with different levels (1:4) are not equals", Priority.LEVEL4.equals(p));
    Assert.assertFalse("2 priorities with different levels (1:5) are not equals", Priority.LEVEL5.equals(p));
  }

  /**
   * Any priority object is always not equal to null
   * 
   */
  @Test
  public void testEquals2() {
    Assert.assertNotNull("Priority object (1) is not equal to null", Priority.LEVEL1);
    Assert.assertNotNull("Priority object (2) is not equal to null", Priority.LEVEL2);
    Assert.assertNotNull("Priority object (3) is not equal to null", Priority.LEVEL3);
    Assert.assertNotNull("Priority object (4) is not equal to null", Priority.LEVEL4);
    Assert.assertNotNull("Priority object (5) is not equal to null", Priority.LEVEL5);
  }

  /**
   * Any priority objet is always different that any other objets
   * 
   */
  @Test
  public void testEquals3() {
    Assert.assertFalse("Priority object must be different that any other object", Priority.LEVEL1.equals(new Integer(1)));
  }

  @Test
  public void testEquals4() {
    final Priority p1 = new Priority();
    Assert.assertEquals("A priority objetc must be equals to itself", p1, p1);
  }

  /**
   * 2 equals objets have the same hashcode
   * 
   */
  @Test
  public void testHashCode1() {
    final Priority p = new Priority();
    Assert.assertTrue("2 equal priority objects must have the same hashcode value", Priority.LEVEL3.hashCode() == p.hashCode());
  }

  /**
   * 2 different priority objects must have different hashcode values
   * 
   */
  @Test
  public void testHashCode2() {
    Assert.assertFalse("2 different priority objects must have different hashcode values",
        Priority.LEVEL1.hashCode() == Priority.LEVEL2.hashCode());
  }

  /**
   * It is legal to construct a priority object and to assign a value. This
   * test implicitluy test the basic equality also. All in one test case!
   */
  @Test
  public void testSetPriority() {
    final Priority p = new Priority();
    p.setPriorityValue(Priority.LEVEL1_LITTERAL);
    Assert.assertEquals("Constructing a priority level 1 has failed!", Priority.LEVEL1, p);

    p.setPriorityValue(Priority.LEVEL2_LITTERAL);
    Assert.assertEquals("Constructing a priority level 2 has failed!", Priority.LEVEL2, p);

    p.setPriorityValue(Priority.LEVEL3_LITTERAL);
    Assert.assertEquals("Constructing a priority level 3 has failed!", Priority.LEVEL3, p);

    p.setPriorityValue(Priority.LEVEL4_LITTERAL);
    Assert.assertEquals("Constructing a priority level 4 has failed!", Priority.LEVEL4, p);

    p.setPriorityValue(Priority.LEVEL5_LITTERAL);
    Assert.assertEquals("Constructing a priority level 5 has failed!", Priority.LEVEL5, p);

  }

  /**
   * Test an illegal value that is 0
   * 
   */
  @Test
  public void testSetPriorityIllegal1() {
    try {
      final Priority p = new Priority();
      p.setPriorityValue(0);
      Assert.fail("Setting a priority level to 0 should raise an IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
      // this is correct
    }
  }

  /**
   * Test an illegal value that is negative
   * 
   */
  @Test
  public void testSetPriorityIllegal2() {
    try {
      final Priority p = new Priority();
      p.setPriorityValue(-15);
      Assert.fail("Setting a priority level to a negative number should raise an IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
      // this is correct
    }
  }

  /**
   * Test an illegal value that is too high
   * 
   */
  @Test
  public void testSetPriorityIllegal3() {
    try {
      final Priority p = new Priority();
      p.setPriorityValue(6);
      Assert.fail("Setting a priority level to a high value should raise an IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
      // this is correct
    }
  }

}
