/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.test.AbstractLanguageVersionTest;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        return Arrays.asList(
            TestDescriptor.defaultVersionIs(KotlinLanguageModule.getInstance(), "1.8")
        );
    }
}
