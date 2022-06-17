/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LanguageRegistryTest {

    @Test
    public void getDefaultLanguageTest() {
        Language defaultLanguage = LanguageRegistry.getDefaultLanguage();
        Assertions.assertNotNull(defaultLanguage);
        // as we don't have java language in this test, we get the first
        // available language now -> DummyLanguage
        Assertions.assertSame(DummyLanguageModule.class, defaultLanguage.getClass());
    }

    @Test
    public void getDefaultVersionLanguageTest() {
        Language dummy = LanguageRegistry.findLanguageByTerseName("dummy");
        LanguageVersion dummy12 = dummy.getVersion("1.2");
        Assertions.assertNotNull(dummy12);

        LanguageVersion dummyDefault = dummy.getDefaultVersion();
        Assertions.assertNotNull(dummyDefault);

        Assertions.assertNotSame(dummy12, dummyDefault);
    }

    @Test
    public void getLanguageVersionByAliasTest() {
        Language dummy = LanguageRegistry.findLanguageByTerseName("dummy");

        LanguageVersion dummy17 = dummy.getVersion("1.7");
        Assertions.assertNotNull(dummy17);
        Assertions.assertEquals("1.7", dummy17.getVersion());

        LanguageVersion dummy7 = dummy.getVersion("7");
        Assertions.assertNotNull(dummy7);
        Assertions.assertEquals("1.7", dummy17.getVersion());
        Assertions.assertSame(dummy17, dummy7);
    }
}
