/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses(names = {
    "net.sourceforge.pmd.lang.java.rule.codestyle.LocalVariableCouldBeFinalTest",
    "net.sourceforge.pmd.lang.java.rule.design.ImmutableFieldTest",
    "net.sourceforge.pmd.lang.java.rule.bestpractices.UnusedAssignmentTest",
    "net.sourceforge.pmd.lang.java.rule.design.LawOfDemeterTest",
    "net.sourceforge.pmd.lang.java.rule.design.SingularFieldTest",
    "net.sourceforge.pmd.lang.java.rule.errorprone.ImplicitSwitchFallThroughTest",
    "net.sourceforge.pmd.lang.java.rule.errorprone.InvalidLogMessageFormatTest",
    "net.sourceforge.pmd.lang.java.rule.design.AvoidThrowingNullPointerExceptionTest"
})
public class AllDataflowRuleTests {
}
