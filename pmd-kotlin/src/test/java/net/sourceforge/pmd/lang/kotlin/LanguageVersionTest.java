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
                new TestDescriptor(KotlinLanguageModule.NAME, KotlinLanguageModule.TERSE_NAME, "1.6-rfc+0.1",
                    getLanguage(KotlinLanguageModule.NAME).getDefaultVersion()),
                new TestDescriptor(KotlinLanguageModule.NAME, KotlinLanguageModule.TERSE_NAME, "1.6",
                    getLanguage(KotlinLanguageModule.NAME).getDefaultVersion()));
    }
}
