/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.jsp.JspLanguageModule;

public class LanguageVersionDiscovererTest {

    /**
     * Test on JSP file.
     */
    @Test
    public void testJspFile() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        File jspFile = new File("/path/to/MyPage.jsp");
        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(jspFile);
        assertEquals("LanguageVersion must be JSP!",
                LanguageRegistry.getLanguage(JspLanguageModule.NAME).getDefaultVersion(), languageVersion);
    }
}
