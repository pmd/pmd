/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.AbstractLanguageVersionTest;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        return Arrays.asList(
                new TestDescriptor(ScalaLanguageModule.NAME, ScalaLanguageModule.TERSE_NAME, "2.13",
                    getLanguage(ScalaLanguageModule.NAME).getVersion("2.13")),
                new TestDescriptor(ScalaLanguageModule.NAME, ScalaLanguageModule.TERSE_NAME, "2.12",
                    getLanguage(ScalaLanguageModule.NAME).getVersion("2.12")),
                new TestDescriptor(ScalaLanguageModule.NAME, ScalaLanguageModule.TERSE_NAME, "2.11",
                    getLanguage(ScalaLanguageModule.NAME).getVersion("2.11")),
                new TestDescriptor(ScalaLanguageModule.NAME, ScalaLanguageModule.TERSE_NAME, "2.10",
                    getLanguage(ScalaLanguageModule.NAME).getVersion("2.10")));
    }
}
