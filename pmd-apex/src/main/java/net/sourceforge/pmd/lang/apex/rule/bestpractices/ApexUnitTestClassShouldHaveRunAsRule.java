/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.Locale;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTRunAsBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;

/**
 * Apex unit tests should have System.runAs methods in them
 *
 * @author t.prouvot
 */
public class ApexUnitTestClassShouldHaveRunAsRule extends AbstractApexUnitTestRule {

    /**
     * Connect in Apex does not support {@code System.runAs}, so a test method
     * that calls a {@code ConnectApi} method cannot use it.
     *
     * @see <a href="https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/connectAPI_TestingApex.htm">Testing ConnectApi Code</a>
     */
    private static final String CONNECT_API_PREFIX = "connectapi.";

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (!isTestMethodOrClass(node)) {
            return data;
        }

        return checkForRunAsStatements(node, data);
    }

    private Object checkForRunAsStatements(ApexNode<?> node, Object data) {
        if (node.descendants(ASTRunAsBlockStatement.class).isEmpty() && !callsConnectApi(node)) {
            asCtx(data).addViolation(node);
        }
        return data;
    }

    private boolean callsConnectApi(ApexNode<?> node) {
        return node.descendants(ASTMethodCallExpression.class)
                .any(call -> call.getFullMethodName().toLowerCase(Locale.ROOT).startsWith(CONNECT_API_PREFIX));
    }
}
