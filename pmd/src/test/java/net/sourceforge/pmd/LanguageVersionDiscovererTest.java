package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;

import java.io.File;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;

import net.sourceforge.pmd.lang.LanguageVersionModule;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.jsp.JspLanguageModule;
import net.sourceforge.pmd.lang.plsql.PLSQLLanguageModule;
import org.junit.Test;

public class LanguageVersionDiscovererTest {

    /**
     * Test on JSP file.
     */
    @Test
    public void testJspFile() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        File jspFile = new File("/path/to/MyPage.jsp");
        LanguageVersionModule languageVersion = discoverer.getDefaultLanguageVersionForFile(jspFile);
        assertEquals("LanguageVersion must be JSP!", LanguageRegistry.getLanguage(JspLanguageModule.NAME).getDefaultVersion(), languageVersion);
    }

    /**
     * Test on Java file with default options.
     */
    @Test
    public void testJavaFileUsingDefaults() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        File javaFile = new File("/path/to/MyClass.java");

        LanguageVersionModule languageVersion = discoverer.getDefaultLanguageVersionForFile(javaFile);
        assertEquals("LanguageVersion must be Java 1.8 !", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.8"), languageVersion);
    }

    /**
     * Test on Java file with Java version set to 1.4.
     */
    @Test
    public void testJavaFileUsing14() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        discoverer.setDefaultLanguageVersion(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.4"));
        File javaFile = new File("/path/to/MyClass.java");

        LanguageVersionModule languageVersion = discoverer.getDefaultLanguageVersionForFile(javaFile);
        assertEquals("LanguageVersion must be Java 1.4!", LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.4"), languageVersion);
    }

    /**
     * Test on PLSQL file with default version
     */
    @Test
    public void testPlsql() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer();
        File plsqlFile = new File("/path/to/MY_PACKAGE.sql");

        LanguageVersionModule languageVersion = discoverer.getDefaultLanguageVersionForFile(plsqlFile);
        assertEquals("LanguageVersion must be PLSQL!", LanguageRegistry.getLanguage(PLSQLLanguageModule.NAME).getDefaultVersion(), languageVersion);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LanguageVersionDiscovererTest.class);
    }
}
