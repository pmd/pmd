/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.test.SimpleAggregatorTst;

/**
 * Executes the metrics testing rules.
 *
 * @author Clément Fournier
 */
class AllMetricsTest extends SimpleAggregatorTst {


    private static final String RULESET = "rulesets/java/metrics_test.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "CognitiveComplexityTest");
        addRule(RULESET, "CycloTest");
        addRule(RULESET, "NcssTest");
        addRule(RULESET, "WmcTest");
        addRule(RULESET, "LocTest");
        addRule(RULESET, "NPathTest");
        addRule(RULESET, "NopaTest");
        addRule(RULESET, "NoamTest");
        addRule(RULESET, "WocTest");
        addRule(RULESET, "TccTest");
        addRule(RULESET, "AtfdTest");
        addRule(RULESET, "CfoTest");
    }


    static String formatJavaMessage(Node node, Object result, String defaultMessage) {
        String qname = null;
        if (node instanceof ASTTypeDeclaration) {
            qname = ((ASTTypeDeclaration) node).getBinaryName();
        } else if (node instanceof ASTExecutableDeclaration) {
            String enclosing = ((ASTExecutableDeclaration) node).getEnclosingType().getBinaryName();
            qname = enclosing + "#" + PrettyPrintingUtil.displaySignature((ASTExecutableDeclaration) node);
        }

        if (qname != null) {
            return "''" + qname + "'' has value " + result + ".";
        }
        return defaultMessage;
    }

}
