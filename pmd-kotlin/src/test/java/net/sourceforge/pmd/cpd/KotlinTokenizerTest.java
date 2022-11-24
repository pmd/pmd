/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class KotlinTokenizerTest extends CpdTextComparisonTest {

    KotlinTokenizerTest() {
        super(".kt");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/kotlin/cpd/testdata";
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new KotlinTokenizer();
    }

    @Test
    void testComments() {
        doTest("comment");
    }

    @Test
    void testIncrement() {
        doTest("increment");
    }

    @Test
    void testImportsIgnored() {
        doTest("imports");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }
}
