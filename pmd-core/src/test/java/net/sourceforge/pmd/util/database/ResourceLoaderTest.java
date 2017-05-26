/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.database;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author sturton
 */
public class ResourceLoaderTest {

    /**
     * Test of getResourceStream method, of class ResourceLoader.
     */
    @Test
    public void testGetResourceStream() throws Exception {
        System.out.println("getResourceStream");
        String path = "";
        ResourceLoader instance = new ResourceLoader();
        InputStream expResult = null;
        InputStream result = instance.getResourceStream(path);
        Assert.assertNotNull(result);
        // assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }
}
