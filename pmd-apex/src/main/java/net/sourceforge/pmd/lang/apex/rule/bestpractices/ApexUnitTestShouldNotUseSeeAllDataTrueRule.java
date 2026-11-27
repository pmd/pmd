/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

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
    private static final String SET_TEST_PREFIX = "settest";

    private static final PropertyDescriptor<Boolean> REPORT_CONNECT_API_DESCRIPTOR = booleanProperty("reportConnectApi")
            .desc("Report seeAllData=true even where the annotated test class or test method "
                    + "calls a ConnectApi method that uses org data. "
                    + "Most ConnectApi methods require seeAllData=true in tests. Set this to false, "
                    + "and the rule does not report seeAllData=true on a test class or test method that calls one.")
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
     * Checks whether the annotated node calls a {@code ConnectApi} method that needs
     * {@code seeAllData=true}. For a test class, every method of the class is searched,
     * helper methods too. For a test method, only the body of that method is searched.
     *
     * <p>Two kinds of {@code ConnectApi} call use no org data, so this check ignores them,
     * and they do not exempt the test from the rule: a call to a {@code setTest...} method,
     * which registers test data, and a call to the regular method whose test data a
     * {@code setTest...} call in the same search registers, for example
     * {@code getFeedElementsFromFeed} after {@code setTestGetFeedElementsFromFeed}.
     */
    private boolean callsConnectApi(final ApexNode<?> node) {
        final List<String> connectApiCalls = node.descendants(ASTMethodCallExpression.class)
                .toStream()
                .map(call -> call.getFullMethodName().toLowerCase(Locale.ROOT))
                .filter(name -> name.startsWith(CONNECT_API_PREFIX))
                .collect(Collectors.toList());

        final Set<String> mockedMethods = connectApiCalls.stream()
                .filter(ApexUnitTestShouldNotUseSeeAllDataTrueRule::isSetTestMethod)
                .map(ApexUnitTestShouldNotUseSeeAllDataTrueRule::mockedMethod)
                .collect(Collectors.toSet());

        return connectApiCalls.stream()
                .anyMatch(name -> !isSetTestMethod(name) && !mockedMethods.contains(name));
    }

    private static boolean isSetTestMethod(final String fullMethodName) {
        return simpleName(fullMethodName).startsWith(SET_TEST_PREFIX);
    }

    /**
     * Returns the full name of the regular method that a {@code setTest...} method
     * registers test data for: {@code connectapi.chatterfeeds.settestgetfeedelementsfromfeed}
     * gives {@code connectapi.chatterfeeds.getfeedelementsfromfeed}.
     */
    private static String mockedMethod(final String fullMethodName) {
        final int dot = fullMethodName.lastIndexOf('.');
        return fullMethodName.substring(0, dot + 1)
                + fullMethodName.substring(dot + 1 + SET_TEST_PREFIX.length());
    }

    private static String simpleName(final String fullMethodName) {
        return fullMethodName.substring(fullMethodName.lastIndexOf('.') + 1);
    }

}
