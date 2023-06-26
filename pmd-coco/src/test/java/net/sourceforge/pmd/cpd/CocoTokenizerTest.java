/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.coco.cpd.CocoTokenizer;

class CocoTokenizerTest extends CpdTextComparisonTest {
    CocoTokenizerTest() {
        super(".coco");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/coco/cpd/testdata";
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        CocoTokenizer tok = new CocoTokenizer();
        return tok;
    }

    @Test
    void testAnnotatedSource() {
        doTest("simple_machine");
    }

    @Test
    void testDocstring() {
        doTest("enum");
    }
}
