/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.rule.errorprone;

import java.util.Collections;
import java.util.List;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.testframework.PmdRuleTst;

public class ForceCastTest extends PmdRuleTst {
    // no additional unit tests

    @Override
    protected List<Rule> getRules() {
        final Rule rule = findRule("category/swift/errorprone.xml", "ForceCast");
        return Collections.singletonList(rule);
    }
}
