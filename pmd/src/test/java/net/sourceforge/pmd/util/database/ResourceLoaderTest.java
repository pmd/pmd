package net.sourceforge.pmd.util.database;

import java.io.InputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author sturton
 */
public class ResourceLoaderTest extends TestCase {
  
  public ResourceLoaderTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(ResourceLoaderTest.class);
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
   * Test of getResourceStream method, of class ResourceLoader.
   */
  public void testGetResourceStream() throws Exception {
    System.out.println("getResourceStream");
    String path = "";
    ResourceLoader instance = new ResourceLoader();
    InputStream expResult = null;
    InputStream result = instance.getResourceStream(path);
    assertNotNull(result);
    //assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    //fail("The test case is a prototype.");
  }
}
