/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class SwiftTokenizerTest extends CpdTextComparisonTest {

    SwiftTokenizerTest() {
        super(".swift");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/swift/cpd/testdata";
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new SwiftTokenizer();
    }


    @Test
    void testSwift42() {
        doTest("Swift4.2");
    }

    @Test
    void testSwift50() {
        doTest("Swift5.0");
    }

    @Test
    void testSwift51() {
        doTest("Swift5.1");
    }

    @Test
    void testSwift52() {
        doTest("Swift5.2");
    }

    @Test
    void testStackoverflowOnLongLiteral() {
        doTest("Issue628");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }
}
