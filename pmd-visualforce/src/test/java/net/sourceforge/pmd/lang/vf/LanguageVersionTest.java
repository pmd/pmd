/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.AbstractLanguageVersionTest;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        return Arrays.asList(new TestDescriptor(VfLanguageModule.NAME, VfLanguageModule.ID,
                ApexLanguageModule.getInstance().getDefaultVersion().getVersion(),
                getLanguage(VfLanguageModule.NAME).getDefaultVersion()));
    }
}
