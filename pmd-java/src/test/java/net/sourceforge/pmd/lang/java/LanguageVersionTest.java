/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import static net.sourceforge.pmd.test.AbstractLanguageVersionTest.TestDescriptor.defaultVersionIs;
import static net.sourceforge.pmd.test.AbstractLanguageVersionTest.TestDescriptor.versionDoesNotExist;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.test.AbstractLanguageVersionTest;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        final Language java = JavaLanguageModule.getInstance();

        return Arrays.asList(
                new TestDescriptor(java, "1.3"),
                new TestDescriptor(java, "1.4"),
                new TestDescriptor(java, "1.5"),
                new TestDescriptor(java, "1.6"),
                new TestDescriptor(java, "1.7"),
                new TestDescriptor(java, "1.8"),
                new TestDescriptor(java, "9"),
                new TestDescriptor(java, "10"),
                new TestDescriptor(java, "11"),
                new TestDescriptor(java, "12"),
                new TestDescriptor(java, "13"),
                new TestDescriptor(java, "14"),
                new TestDescriptor(java, "15"),
                new TestDescriptor(java, "16"),
                new TestDescriptor(java, "17"),
                new TestDescriptor(java, "18"),
                new TestDescriptor(java, "19"),
                new TestDescriptor(java, "20"),
                new TestDescriptor(java, "21"),
                new TestDescriptor(java, "22"),
                new TestDescriptor(java, "22-preview"),
                new TestDescriptor(java, "23"),
                new TestDescriptor(java, "23-preview"),

                defaultVersionIs(java, "23"),

                // this one won't be found: case-sensitive!
                versionDoesNotExist("JAVA", "JAVA", "1.7"),
                // not supported anymore
                versionDoesNotExist(java, "20-preview")
        );
    }
}
