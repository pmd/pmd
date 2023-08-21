/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.AbstractLanguageVersionTest;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        return Arrays.asList(new TestDescriptor(SwiftLanguageModule.NAME, SwiftLanguageModule.TERSE_NAME, "5.7",
                getLanguage(SwiftLanguageModule.NAME).getDefaultVersion()));
    }
}
