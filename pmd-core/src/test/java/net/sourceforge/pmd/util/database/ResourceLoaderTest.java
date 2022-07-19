/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.database;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

/**
 *
 * @author sturton
 */
class ResourceLoaderTest {

    /**
     * Test of getResourceStream method, of class ResourceLoader.
     */
    @Test
    void testGetResourceStream() throws Exception {
        System.out.println("getResourceStream");
        String path = "";
        ResourceLoader instance = new ResourceLoader();
        InputStream expResult = null;
        InputStream result = instance.getResourceStream(path);
        assertNotNull(result);
        // assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }
}
