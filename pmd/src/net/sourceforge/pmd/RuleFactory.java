/*
 * User: tom
 * Date: Jun 10, 2002
 * Time: 11:27:51 AM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.rules.*;

import java.util.*;

public class RuleFactory {

    public static final String ALL = "all";
    public static final String GENERAL = "general";
    public static final String COUGAAR = "cougaar";

    private static Set ruleSets = new HashSet();

    static {
        ruleSets.add(ALL);
        ruleSets.add(GENERAL);
        ruleSets.add(COUGAAR);
    }

    public static List createRules(String ruleSetType) {
        if (!ruleSets.contains(ruleSetType)) {
            throw new RuntimeException("Unknown rule set type " + ruleSetType);
        }

        if (ruleSetType.equals(ALL)) {
            return createAllRules();
        } else if (ruleSetType.equals(GENERAL)) {
            return createGeneralRules();
        }
        return createCougaarRules();
    }

    private static List createAllRules() {
        List list = new ArrayList();
        list.addAll(createCougaarRules());
        list.addAll(createGeneralRules());
        return list;
    }

    private static List createCougaarRules() {
        List list = new ArrayList();
        list.add(new DontCreateThreadsRule());
        list.add(new DontCreateTimersRule());
        list.add(new SystemOutRule());
        list.add(new SystemPropsRule());
        return list;
    }

    private static List createGeneralRules() {
        List list = new ArrayList();
        list.add(new EmptyCatchBlockRule());
        list.add(new EmptyIfStmtRule());
        list.add(new UnnecessaryConversionTemporaryRule());
        list.add(new UnusedLocalVariableRule());
        list.add(new UnusedPrivateInstanceVariableRule());
        list.add(new IfElseStmtsMustUseBracesRule());
        return list;
    }
}
