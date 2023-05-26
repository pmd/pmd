/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.typescript.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class TypeScriptTokenizerTest extends CpdTextComparisonTest {

    TypeScriptTokenizerTest() {
        super(".ts");
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new TypeScriptTokenizer();
    }

    @Override
    protected String getResourcePrefix() {
        return "../cpd/testdata";
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
