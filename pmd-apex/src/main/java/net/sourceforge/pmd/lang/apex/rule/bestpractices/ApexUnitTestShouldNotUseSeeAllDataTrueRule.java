/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.Locale;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotationParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * <p>
 * It's a very bad practice to use @isTest(seeAllData=true) in Apex unit tests,
 * because it opens up the existing database data for unexpected modification by
 * tests.
 * </p>
 *
 * @author a.subramanian
 */
public class ApexUnitTestShouldNotUseSeeAllDataTrueRule extends AbstractApexUnitTestRule {

    private static final String CONNECT_API_PREFIX = "connectapi.";

    private static final PropertyDescriptor<Boolean> REPORT_CONNECT_API_DESCRIPTOR = booleanProperty("reportConnectApi")
            .desc("Report seeAllData=true even in test classes that call ConnectApi methods. "
                    + "Most ConnectApi methods require seeAllData=true in tests, so set this to false "
                    + "to exempt test classes that call them.")
            .defaultValue(true)
            .build();

    public ApexUnitTestShouldNotUseSeeAllDataTrueRule() {
        definePropertyDescriptor(REPORT_CONNECT_API_DESCRIPTOR);
    }

    @Override
    public Object visit(final ASTUserClass node, final Object data) {
        if (!isTestMethodOrClass(node)) {
            return data;
        }

        checkForSeeAllData(node, data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (!isTestMethodOrClass(node)) {
            return data;
        }

        return checkForSeeAllData(node, data);
    }

    private Object checkForSeeAllData(final ApexNode<?> node, final Object data) {
        final ASTModifierNode modifierNode = node.firstChild(ASTModifierNode.class);

        if (modifierNode != null) {
            for (ASTAnnotationParameter parameter : modifierNode.descendants(ASTAnnotationParameter.class)) {
                if (parameter.hasName(ASTAnnotationParameter.SEE_ALL_DATA)
                        && (parameter.getBooleanValue() || "true".equalsIgnoreCase(parameter.getValue()))) {
                    if (!getProperty(REPORT_CONNECT_API_DESCRIPTOR) && callsConnectApi(node)) {
                        return data;
                    }
                    asCtx(data).addViolation(parameter);
                    return data;
                }
            }
        }

        return data;
    }

    /**
     * Checks whether the class that contains the given node (or the node itself,
     * if it is a class) calls any method of the {@code ConnectApi} namespace.
     */
    private boolean callsConnectApi(final ApexNode<?> node) {
        final ASTUserClass userClass = node instanceof ASTUserClass
                ? (ASTUserClass) node
                : node.ancestors(ASTUserClass.class).first();
        if (userClass == null) {
            return false;
        }
        return userClass.descendants(ASTMethodCallExpression.class)
                .any(call -> call.getFullMethodName().toLowerCase(Locale.ROOT).startsWith(CONNECT_API_PREFIX));
    }

}
