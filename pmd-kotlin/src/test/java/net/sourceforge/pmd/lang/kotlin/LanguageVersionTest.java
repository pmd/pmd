/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import java.util.Collection;
import java.util.Collections;

import net.sourceforge.pmd.test.AbstractLanguageVersionTest;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        return Collections.singletonList(
                TestDescriptor.defaultVersionIs(KotlinLanguageModule.getInstance(), "1.8")
        );
    }
}
