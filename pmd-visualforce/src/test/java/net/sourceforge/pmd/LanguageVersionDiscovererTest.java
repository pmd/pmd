/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.vf.ast.AbstractVfTest;

/**
 * @author sergey.gorbaty
 *
 */
class LanguageVersionDiscovererTest extends AbstractVfTest {

    /**
     * Test on VF file.
     */
    @Test
    void testVFFile() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer(LanguageRegistry.PMD);
        File vfFile = new File("/path/to/MyPage.page");
        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(vfFile);
        assertEquals(vf.getLanguage().getDefaultVersion(), languageVersion, "LanguageVersion must be VF!");
    }

    @Test
    void testComponentFile() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer(new LanguageRegistry(singleton(vf.getLanguage())));
        File vfFile = new File("/path/to/MyPage.component");
        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(vfFile);
        assertEquals(vf.getLanguage().getDefaultVersion(), languageVersion, "LanguageVersion must be VF!");
    }
}
