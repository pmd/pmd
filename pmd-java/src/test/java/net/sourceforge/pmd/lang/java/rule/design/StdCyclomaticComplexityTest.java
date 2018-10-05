/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.io.StringReader;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.testframework.PmdRuleTst;

public class StdCyclomaticComplexityTest extends PmdRuleTst {
    /**
     * Make sure the entry stack is empty, if show classes complexity is
     * disabled.
     *
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1501/">bug #1501</a>
     */
    @Test
    public void entryStackMustBeEmpty() {
        StdCyclomaticComplexityRule rule = new StdCyclomaticComplexityRule();
        rule.setProperty(StdCyclomaticComplexityRule.SHOW_CLASSES_COMPLEXITY_DESCRIPTOR, Boolean.FALSE);

        RuleContext ctx = new RuleContext();
        LanguageVersion javaLanguageVersion = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.8");
        ParserOptions parserOptions = javaLanguageVersion.getLanguageVersionHandler().getDefaultParserOptions();
        Parser parser = javaLanguageVersion.getLanguageVersionHandler().getParser(parserOptions);
        Node node = parser.parse("test", new StringReader("public class SampleClass {}"));

        rule.apply(Arrays.asList(node), ctx);

        Assert.assertTrue(rule.entryStack.isEmpty());
    }
}
