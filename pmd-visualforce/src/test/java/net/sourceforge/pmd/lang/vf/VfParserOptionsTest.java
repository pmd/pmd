/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VfParserOptionsTest {
    @Test
    public void testDefaultPropertyDescriptors() {
        VfParserOptions vfParserOptions = new VfParserOptions();
        assertEquals(VfParserOptions.DEFAULT_APEX_DIRECTORIES,
                vfParserOptions.getProperties().getProperty(VfParserOptions.APEX_DIRECTORIES_DESCRIPTOR));
        assertEquals(VfParserOptions.DEFAULT_OBJECT_DIRECTORIES,
                vfParserOptions.getProperties().getProperty(VfParserOptions.OBJECTS_DIRECTORIES_DESCRIPTOR));
    }
}
