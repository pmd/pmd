package net.sourceforge.pmd.util.database;

import javax.xml.transform.Source;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author sturton
 */
public class ResourceResolverTest extends TestCase {
  
  public ResourceResolverTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(ResourceResolverTest.class);
    return suite;
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }
  
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test of resolve method, of class ResourceResolver.
   */
  public void testResolve() throws Exception {
    System.out.println("resolve");
    String href = "";
    String base = "";
    ResourceResolver instance = new ResourceResolver();
    Source expResult = null;
    Source result = instance.resolve(href, base);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    //fail("The test case is a prototype.");
  }
}
