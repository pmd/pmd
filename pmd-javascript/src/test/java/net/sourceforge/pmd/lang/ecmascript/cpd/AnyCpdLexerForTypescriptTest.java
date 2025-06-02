/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ecmascript.EcmascriptLanguageModule;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

/**
 *
 */
class AnyCpdLexerForTypescriptTest extends CpdTextComparisonTest {

    AnyCpdLexerForTypescriptTest() {
        super(EcmascriptLanguageModule.getInstance(), ".ts");
    }

    @Override
    protected String getResourcePrefix() {
        return "testdata/ts";
    }

    @Test
    void testFile1() {
        doTest("SampleTypeScript");
    }

}
