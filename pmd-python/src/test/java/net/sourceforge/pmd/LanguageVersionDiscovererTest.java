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
import net.sourceforge.pmd.lang.python.PythonLanguageModule;

public class LanguageVersionDiscovererTest {

    /**
     * Test on Python file with default version
     */
    @Test
    public void testPython() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        File pythonFile = new File("/path/to/MY_PACKAGE.py");

        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(pythonFile);
        assertEquals("LanguageVersion must be Python!",
                LanguageRegistry.getLanguage(PythonLanguageModule.NAME).getDefaultVersion(), languageVersion);
    }
}
