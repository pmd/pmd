/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.designer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Unit tests for {@link Designer}
 */
public class DesignerTest {

    /**
     * Unit test for https://sourceforge.net/p/pmd/bugs/1168/
     */
    @Test
    public void testCopyXmlToClipboard() {
        Node compilationUnit = Designer.getCompilationUnit(
                LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion().getLanguageVersionHandler(),
                "doesn't matter");
        String xml = Designer.getXmlTreeCode(compilationUnit);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<dummyNode BeginColumn=\"1\" BeginLine=\"1\" EndColumn=\"0\" EndLine=\"0\" FindBoundary=\"false\"\n"
                + "           Image=\"Foo\"\n" + "           SingleLine=\"false\"/>", xml);
    }
}
