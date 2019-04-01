/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.errorprone;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.testframework.PmdRuleTst;

public class ToDateWithoutDateFormatTest extends PmdRuleTst {
    // No additional unit tests

    @Override
    protected List<Rule> getRules() {
        Rule rule = findRule("category/plsql/errorprone.xml", "TO_DATEWithoutDateFormat");
        return Collections.singletonList(rule);
    }
}
