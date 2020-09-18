/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import org.junit.Ignore;

import net.sourceforge.pmd.testframework.PmdRuleTst;

@Ignore("Ignored until modifiers (@Private, etc) are made accessible "
            + "to XPath rules without deprecation warning")
public class UncommentedEmptyConstructorTest extends PmdRuleTst {
    // no additional unit tests
}
