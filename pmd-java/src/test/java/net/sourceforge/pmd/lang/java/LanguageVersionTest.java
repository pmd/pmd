/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.AbstractLanguageVersionTest;
import net.sourceforge.pmd.lang.Language;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        final String name = JavaLanguageModule.NAME;
        final String terseName = JavaLanguageModule.TERSE_NAME;
        final Language java = getLanguage(name);

        return Arrays.asList(
                new TestDescriptor(name, terseName, "1.3", java.getVersion("1.3")),
                new TestDescriptor(name, terseName, "1.4", java.getVersion("1.4")),
                new TestDescriptor(name, terseName, "1.5", java.getVersion("1.5")),
                new TestDescriptor(name, terseName, "1.6", java.getVersion("1.6")),
                new TestDescriptor(name, terseName, "1.7", java.getVersion("1.7")),
                new TestDescriptor(name, terseName, "1.8", java.getVersion("1.8")),
                new TestDescriptor(name, terseName, "9", java.getVersion("9")),
                new TestDescriptor(name, terseName, "10", java.getVersion("10")),
                new TestDescriptor(name, terseName, "11", java.getVersion("11")),
                new TestDescriptor(name, terseName, "12", java.getVersion("12")),
                new TestDescriptor(name, terseName, "13", java.getVersion("13")),
                new TestDescriptor(name, terseName, "14", java.getVersion("14")),
                new TestDescriptor(name, terseName, "15", java.getVersion("15")),
                new TestDescriptor(name, terseName, "16", java.getVersion("16")),
                new TestDescriptor(name, terseName, "16-preview", java.getVersion("16-preview")),
                new TestDescriptor(name, terseName, "17", java.getVersion("17")),
                new TestDescriptor(name, terseName, "17-preview", java.getVersion("17-preview")),

                // this one won't be found: case sensitive!
                new TestDescriptor("JAVA", "JAVA", "1.7", null));
    }
}
