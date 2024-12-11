/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.lang.plsql.PLSQLLanguageModule;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.test.cpd.LanguagePropertyConfig;

class PLSQLCpdLexerTest extends CpdTextComparisonTest {

    PLSQLCpdLexerTest() {
        super(PLSQLLanguageModule.getInstance(), ".sql");
    }

    @Test
    void testSimple() {
        doTest("sample-plsql");
    }

    @Test
    void testSpecialComments() {
        doTest("specialComments");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }

    @Test
    void testIdentifiers() {
        doTest("identifiers");
    }

    @Test
    void anonymizeLiterals() {
        doTest("sample-plsql", "_ignore-literals", ignoreLiterals());
    }

    LanguagePropertyConfig ignoreLiterals() {
        return props -> {
            props.setProperty(CpdLanguageProperties.CPD_ANONYMIZE_LITERALS, true);
        };
    }
}
