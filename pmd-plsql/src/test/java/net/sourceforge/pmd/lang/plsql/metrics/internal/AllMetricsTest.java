/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.metrics.internal;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ExecutableCode;
import net.sourceforge.pmd.lang.plsql.ast.OracleObject;
import net.sourceforge.pmd.test.SimpleAggregatorTst;

class AllMetricsTest extends SimpleAggregatorTst {
    private static final String RULESET = "rulesets/plsql/metrics_test.xml";

    @Override
    protected void setUp() {
        addRule(RULESET, "NcssTest");
    }

    static String formatPlsqlMessage(Node node, Integer result, String defaultMessage) {
        String name = "(" + node.getXPathNodeName() + ")";
        if (node instanceof ExecutableCode) {
            name = name + " " + ((ExecutableCode) node).getMethodName();
        } else if (node instanceof OracleObject) {
            name = name + " " + ((OracleObject) node).getObjectName();
        }
        return "''" + name + "'' has value " + result + ".";
    }
}
