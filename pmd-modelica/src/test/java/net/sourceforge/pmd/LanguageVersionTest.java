/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.modelica.ModelicaLanguageModule;

public class LanguageVersionTest extends AbstractLanguageVersionTest {
    public LanguageVersionTest(String name, String terseName, String version, LanguageVersion expected) {
        super(name, terseName, version, expected);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { ModelicaLanguageModule.NAME, ModelicaLanguageModule.TERSE_NAME, "",
                LanguageRegistry.getLanguage(ModelicaLanguageModule.NAME).getDefaultVersion(),
            },
        });
    }
}
