package net.sourceforge.pmd;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.vm.VmLanguageModule;

import org.junit.runners.Parameterized.Parameters;

public class LanguageVersionTest extends AbstractLanguageVersionTest {

    public LanguageVersionTest(String name, String terseName, String version, LanguageVersion expected) {
        super(name, terseName, version, expected);
    }

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] {
                { VmLanguageModule.NAME, VmLanguageModule.TERSE_NAME, "", LanguageRegistry.getLanguage(VmLanguageModule.NAME).getDefaultVersion() }
            });
    }
}
