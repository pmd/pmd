package net.sourceforge.pmd.util.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author sturton
 */
public class DBTypeTest extends TestCase {
  
  private static String TEST_FILE_NAME ="/tmp/test.properties";

  private File absoluteFile = new File(TEST_FILE_NAME);

  private Properties testProperties;
  private Properties includeProperties;

  public DBTypeTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(DBTypeTest.class);
    return suite;
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    testProperties = new Properties();
    testProperties.put("prop1", "value1");
    testProperties.put("prop2", "value2");
    testProperties.put("prop3", "value3");

    includeProperties = new Properties();
    includeProperties.putAll(testProperties);
    includeProperties.put("prop3", "include3");

    FileOutputStream fileOutputStream = new FileOutputStream(absoluteFile);
    PrintStream printStream = new PrintStream(fileOutputStream);

    for (Entry entry : testProperties.entrySet() )
    {
      printStream.printf("%s=%s\n", entry.getKey(), entry.getValue());
    }

  }
  
  @Override
  protected void tearDown() throws Exception {
    testProperties = null;
    super.tearDown();
  }

  /**
   * Test of getProperties method, of class DBType.
   */
  public void testGetPropertiesFromFile() throws Exception {
    System.out.println("getPropertiesFromFile");
    DBType instance = new DBType("/tmp/test.properties");
    Properties expResult = testProperties;
    Properties result = instance.getProperties();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    //fail("The test case is a prototype.");
  }

  /**
   * Test of getProperties method, of class DBType.
   */
  public void testGetProperties() throws Exception {
    System.out.println("testGetProperties");
    DBType instance = new DBType("test");
    Properties expResult = testProperties;
    System.out.println("testGetProperties: expected results "+ testProperties);
    Properties result = instance.getProperties();
    System.out.println("testGetProperties: actual results "+ result);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    //fail("The test case is a prototype.");
  }

  /**
   * Test of getProperties method, of class DBType.
   */
  public void testGetIncludeProperties() throws Exception {
    System.out.println("testGetIncludeProperties");
    DBType instance = new DBType("include");
    Properties expResult = includeProperties;
    System.out.println("testGetIncludeProperties: expected results "+ includeProperties);
    Properties result = instance.getProperties();
    System.out.println("testGetIncludeProperties: actual results "+ result);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    //fail("The test case is a prototype.");
  }

  /**
   * Test of getResourceBundleAsProperties method, of class DBType.
   */
  public void testAsProperties() {
    System.out.println("asProperties");
    ResourceBundle bundle = ResourceBundle.getBundle(DBType.class.getCanonicalName()+".test");
    Properties expResult = testProperties;
    Properties result = DBType.getResourceBundleAsProperties(bundle);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    //fail("The test case is a prototype.");
  }
}
