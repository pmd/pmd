/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import net.sourceforge.pmd.lang.java.rule.bestpractices.UnusedAssignmentTest;
import net.sourceforge.pmd.lang.java.rule.codestyle.LocalVariableCouldBeFinalTest;
import net.sourceforge.pmd.lang.java.rule.design.AvoidThrowingNullPointerExceptionTest;
import net.sourceforge.pmd.lang.java.rule.design.ImmutableFieldTest;
import net.sourceforge.pmd.lang.java.rule.design.LawOfDemeterTest;
import net.sourceforge.pmd.lang.java.rule.design.SingularFieldTest;
import net.sourceforge.pmd.lang.java.rule.errorprone.ImplicitSwitchFallThroughTest;
import net.sourceforge.pmd.lang.java.rule.errorprone.InvalidLogMessageFormatTest;

@Suite
@SelectClasses({
    LocalVariableCouldBeFinalTest.class,
    ImmutableFieldTest.class,
    UnusedAssignmentTest.class,
    LawOfDemeterTest.class,
    SingularFieldTest.class,
    ImplicitSwitchFallThroughTest.class,
    InvalidLogMessageFormatTest.class,
    AvoidThrowingNullPointerExceptionTest.class
})
public class AllDataflowRuleTests {
}
