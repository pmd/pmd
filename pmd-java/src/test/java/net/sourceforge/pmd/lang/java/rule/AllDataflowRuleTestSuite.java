/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;


import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("net.sourceforge.pmd.lang.java.rule")
@IncludeTags(AllDataflowRuleTestSuite.TAG)
public class AllDataflowRuleTestSuite {
    public static final String TAG = "dataflow";

    private AllDataflowRuleTestSuite() {}
}
