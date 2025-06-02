/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * @author sturton
 */
class DBTypeTest {

    private File absoluteFile;

    private Properties testProperties;
    private Properties includeProperties;

    @TempDir
    private Path folder;

    @BeforeEach
    void setUp() throws Exception {
        testProperties = new Properties();
        testProperties.put("prop1", "value1");
        testProperties.put("prop2", "value2");
        testProperties.put("prop3", "value3");

        includeProperties = new Properties();
        includeProperties.putAll(testProperties);
        includeProperties.put("prop3", "include3");

        absoluteFile = folder.resolve("dbtypetest.properties").toFile();
        try (FileOutputStream fileOutputStream = new FileOutputStream(absoluteFile);
             PrintStream printStream = new PrintStream(fileOutputStream)) {
            for (Entry<?, ?> entry : testProperties.entrySet()) {
                printStream.printf("%s=%s\n", entry.getKey(), entry.getValue());
            }
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        testProperties = null;
    }

    /**
     * Test of getProperties method, of class DBType.
     */
    @Test
    void testGetPropertiesFromFile() throws Exception {
        System.out.println("getPropertiesFromFile");
        DBType instance = new DBType(absoluteFile.getAbsolutePath());
        Properties expResult = testProperties;
        Properties result = instance.getProperties();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getProperties method, of class DBType.
     */
    @Test
    void testGetProperties() throws Exception {
        System.out.println("testGetProperties");
        DBType instance = new DBType("test");
        Properties expResult = testProperties;
        System.out.println("testGetProperties: expected results " + testProperties);
        Properties result = instance.getProperties();
        System.out.println("testGetProperties: actual results " + result);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getProperties method, of class DBType.
     */
    @Test
    void testGetIncludeProperties() throws Exception {
        System.out.println("testGetIncludeProperties");
        DBType instance = new DBType("include");
        Properties expResult = includeProperties;
        System.out.println("testGetIncludeProperties: expected results " + includeProperties);
        Properties result = instance.getProperties();
        System.out.println("testGetIncludeProperties: actual results " + result);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getResourceBundleAsProperties method, of class DBType.
     */
    @Test
    void testAsProperties() {
        System.out.println("asProperties");
        ResourceBundle bundle = ResourceBundle.getBundle(DBType.class.getPackage().getName() + ".test");
        Properties expResult = testProperties;
        Properties result = DBType.getResourceBundleAsProperties(bundle);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }
}
