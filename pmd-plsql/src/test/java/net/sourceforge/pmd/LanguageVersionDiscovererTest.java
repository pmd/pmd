package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;

import java.io.File;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.plsql.PLSQLLanguageModule;

import org.junit.Test;

public class LanguageVersionDiscovererTest {

    /**
     * Test on PLSQL file with default version
     */
    @Test
    public void testPlsql() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        File plsqlFile = new File("/path/to/MY_PACKAGE.sql");

        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(plsqlFile);
        assertEquals("LanguageVersion must be PLSQL!", LanguageRegistry.getLanguage(PLSQLLanguageModule.NAME).getDefaultVersion(), languageVersion);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LanguageVersionDiscovererTest.class);
    }
}
