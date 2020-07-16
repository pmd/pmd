/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

public class RubyTokenizerTest extends CpdTextComparisonTest {

    public RubyTokenizerTest() {
        super(".rb");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/ruby/cpd/testdata";
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new RubyTokenizer();
    }


    @Test
    public void testSimple() {
        doTest("server");
    }
}
