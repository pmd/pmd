/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.AbstractLanguageVersionTest;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        return Arrays.asList(
                new TestDescriptor(KotlinLanguageModule.NAME, KotlinLanguageModule.TERSE_NAME, "1.8",
                    getLanguage(KotlinLanguageModule.NAME).getDefaultVersion()));
    }
}
