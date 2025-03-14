/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.test.AbstractLanguageVersionTest;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        return Arrays.asList(new TestDescriptor(VfLanguageModule.NAME, VfLanguageModule.ID,
                ApexLanguageModule.getInstance().getDefaultVersion().getVersion(),
                getLanguage(VfLanguageModule.NAME).getDefaultVersion()));
    }
}
