/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.typescript.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.typescript.TsLanguageModule;

class TypeScriptCpdLexerTest extends CpdTextComparisonTest {

    TypeScriptCpdLexerTest() {
        super(TsLanguageModule.getInstance(), ".ts");
    }

    @Test
    void greeterTest() {
        doTest("greeter");
    }

    @Test
    void apiSampleWatchTest() {
        doTest("APISample_Watch");
    }
}
