/*
 * Created on 18.08.2004
 */
package test.net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.test.FlowTest;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import junit.framework.TestCase;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class AcceptanceTest extends RuleTst {

    public void testAll() throws Throwable {
        runTestFromString(AcceptanceTestRule.TEST, new AcceptanceTestRule(), new Report());
    }

}
