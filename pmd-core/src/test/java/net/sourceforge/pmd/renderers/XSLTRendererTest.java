/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

public class XSLTRendererTest {

    @Test
    public void testDefaultStylesheet() throws Exception {
        XSLTRenderer renderer = new XSLTRenderer();
        FileLocation loc = FileLocation.range("file", TextRange2d.range2d(1, 1, 1, 2));
        RuleViolation rv = new ParametricRuleViolation(new FooRule(), loc, "violation message");
        String result = ReportTest.render(renderer, it -> it.onRuleViolation(rv));
        Assert.assertTrue(result.contains("violation message"));
    }
}
