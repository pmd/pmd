/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.test.AbstractLanguageVersionTest;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        return Arrays.asList(
                new TestDescriptor(ScalaLanguageModule.getInstance(), "2.13"),
                new TestDescriptor(ScalaLanguageModule.getInstance(), "2.12"),
                new TestDescriptor(ScalaLanguageModule.getInstance(), "2.11"),
                new TestDescriptor(ScalaLanguageModule.getInstance(), "2.10")
        );
    }
}
