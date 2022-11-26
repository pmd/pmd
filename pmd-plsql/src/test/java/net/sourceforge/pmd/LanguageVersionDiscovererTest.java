/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class LanguageVersionDiscovererTest extends AbstractPLSQLParserTst {

    /**
     * Test on PLSQL file with default version
     */
    @Test
    void testPlsql() {
        LanguageVersionDiscoverer discoverer = new LanguageVersionDiscoverer(LanguageRegistry.PMD);
        File plsqlFile = new File("/path/to/MY_PACKAGE.sql");

        LanguageVersion languageVersion = discoverer.getDefaultLanguageVersionForFile(plsqlFile);
        assertEquals(plsql.getLanguage().getDefaultVersion(), languageVersion,
                "LanguageVersion must be PLSQL!");
    }
}
