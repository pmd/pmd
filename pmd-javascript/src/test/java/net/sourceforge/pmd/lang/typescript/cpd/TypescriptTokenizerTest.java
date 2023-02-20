/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.typescript.cpd;

import java.util.Properties;

import org.junit.Test;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

public class TypescriptTokenizerTest extends CpdTextComparisonTest {

    public TypescriptTokenizerTest() {
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
    public void greeterTest() {
        doTest("greeter");
    }

    @Test
    public void apiSampleWatchTest() {
        doTest("APISample_Watch");
    }
}
