/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.typescript.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class TypescriptTokenizerTest extends CpdTextComparisonTest {

    TypescriptTokenizerTest() {
        super(".ts");
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new TypescriptTokenizer();
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
