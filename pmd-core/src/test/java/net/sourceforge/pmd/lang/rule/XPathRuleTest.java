/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import org.junit.Assert;
import org.junit.Test;

public class XPathRuleTest {

    /**
     * It's easy to forget the attribute "typeResolution=true" when
     * defining XPath rules in xml. Therefore we by default enable
     * typeresolution. For Java rules, type resolution was enabled by
     * default long time ago.
     *
     * @see <a href="https://github.com/pmd/pmd/issues/2048">#2048 [core] Enable type resolution by default for XPath rules</a>
     */
    @Test
    public void typeResolutionShouldBeEnabledByDefault() {
        XPathRule rule = new XPathRule();
        Assert.assertTrue(rule.isTypeResolution());

        XPathRule rule2 = new XPathRule(".");
        Assert.assertTrue(rule2.isTypeResolution());
    }

}
