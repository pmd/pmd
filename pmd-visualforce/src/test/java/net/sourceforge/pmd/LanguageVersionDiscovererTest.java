/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.vf.ast.AbstractVfNodesTest;

/**
 * @author sergey.gorbaty
 *
 */
public class LanguageVersionDiscovererTest extends AbstractVfNodesTest {

    /**
     * Test on VF file.
     */
    @Test
    public void testVFFile() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        File vfFile = new File("/path/to/MyPage.page");
        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(vfFile);
        assertEquals("LanguageVersion must be VF!",
                vf.getLanguage().getDefaultVersion(), languageVersion);
    }

    @Test
    public void testComponentFile() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        File vfFile = new File("/path/to/MyPage.component");
        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(vfFile);
        assertEquals("LanguageVersion must be VF!",
                vf.getLanguage().getDefaultVersion(), languageVersion);
    }
}
