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
        assertEquals(VfExpressionTypeVisitor.DEFAULT_APEX_DIRECTORIES,
                vfParserOptions.getProperty(VfExpressionTypeVisitor.APEX_DIRECTORIES_DESCRIPTOR));
        assertEquals(VfExpressionTypeVisitor.DEFAULT_OBJECT_DIRECTORIES,
                vfParserOptions.getProperty(VfExpressionTypeVisitor.OBJECTS_DIRECTORIES_DESCRIPTOR));
    }
}
