/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.test.AbstractLanguageVersionTest;

class LanguageVersionTest extends AbstractLanguageVersionTest {

    static Collection<TestDescriptor> data() {
        return Arrays.asList(new TestDescriptor(PLSQLLanguageModule.NAME, PLSQLLanguageModule.ID, "21c",
            getLanguage(PLSQLLanguageModule.NAME).getDefaultVersion()));
    }
}
