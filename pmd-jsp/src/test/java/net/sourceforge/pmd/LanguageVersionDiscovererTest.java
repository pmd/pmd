/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.jsp.ast.AbstractJspNodesTst;

class LanguageVersionDiscovererTest extends AbstractJspNodesTst {

    @Test
    void testParseJsp() {
        testLanguageIsJsp("sample.jsp");
        testLanguageIsJsp("sample.jspx");
    }

    @Test
    void testTag() {
        testLanguageIsJsp("sample.tag");
    }


    private void testLanguageIsJsp(String first) {
        assertEquals(jsp.getLanguage().getDefaultVersion(),
                                getLanguageVersion(Paths.get(first)));
    }

    @Test
    void testParseWrong() {
        assertNotEquals(jsp.getLanguage().getDefaultVersion(),
                                getLanguageVersion(Paths.get("sample.xxx")));
    }

    private LanguageVersion getLanguageVersion(Path jspFile) {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer(LanguageRegistry.PMD);
        return discoverer.getDefaultLanguageVersionForFile(jspFile.toFile());
    }
}
