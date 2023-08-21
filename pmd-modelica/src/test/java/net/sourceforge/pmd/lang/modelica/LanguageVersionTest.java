/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.AbstractLanguageVersionTest;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        return Arrays.asList(new TestDescriptor(ModelicaLanguageModule.NAME, ModelicaLanguageModule.TERSE_NAME, "3.5",
                getLanguage(ModelicaLanguageModule.NAME).getDefaultVersion()));
    }
}
