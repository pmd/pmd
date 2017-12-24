/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.StringMultiProperty;

/**
 * Check that log.debug, log.trace, log.error, etc... statements are guarded by
 * some test expression on log.isDebugEnabled() or log.isTraceEnabled().
 * 
 * @author Romain Pelisse - &lt;belaran@gmail.com&gt;
 * @author Heiko Rupp - &lt;hwr@pilhuhn.de&gt;
 * @author Tammo van Lessen - provided original XPath expression
 * 
 */
public class GuardLogStatementRule extends AbstractJavaRule implements Rule {
    /*
     * guard methods and log levels:
     * 
     * log4j + apache commons logging (jakarta):
     * trace -> isTraceEnabled
     * debug -> isDebugEnabled
     * info  -> isInfoEnabled
     * warn  -> isWarnEnabled
     * error -> isErrorEnabled
     * 
     * 
     * java util:
     * log(Level.FINE) ->  isLoggable
     * finest ->  isLoggable
     * finer  ->  isLoggable
     * fine   ->  isLoggable
     * info   ->  isLoggable
     * warning -> isLoggable
     * severe  -> isLoggable
     */
    private static final StringMultiProperty LOG_LEVELS = new StringMultiProperty("logLevels", "LogLevels to guard",
            new String[] {"trace", "debug", "info", "warn", "error",
                "log", "finest", "finer", "fine", "info", "warning", "severe", }, 1.0f, ',');

    private static final StringMultiProperty GUARD_METHODS = new StringMultiProperty("guardsMethods",
            "method use to guard the log statement",
            new String[] {"isTraceEnabled", "isDebugEnabled", "isInfoEnabled", "isWarnEnabled", "isErrorEnabled",
                "isLoggable", }, 2.0f, ',');

    private Map<String, String> guardStmtByLogLevel = new HashMap<>(12);

    public GuardLogStatementRule() {
        definePropertyDescriptor(LOG_LEVELS);
        definePropertyDescriptor(GUARD_METHODS);
    }

    @Override
    public Object visit(ASTCompilationUnit unit, Object data) {
        extractProperties();
        return super.visit(unit, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data; // don't consider interfaces
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        if (node.jjtGetNumChildren() >= 2 && node.jjtGetChild(0) instanceof ASTPrimaryPrefix) {
            ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) node.jjtGetChild(0);
            String methodCall = getMethodCallName(prefix);
            String logLevel = getLogLevelName(node, methodCall);

            if (guardStmtByLogLevel.containsKey(methodCall)
                    && node.jjtGetChild(1) instanceof ASTPrimarySuffix
                    && node.jjtGetChild(1).hasDescendantOfType(ASTAdditiveExpression.class)) {

                if (!hasGuard(node, methodCall, logLevel)) {
                    super.addViolation(data, node);
                }
            }
        }
        return super.visit(node, data);
    }

    private boolean hasGuard(ASTPrimaryExpression node, String methodCall, String logLevel) {
        ASTIfStatement ifStatement = node.getFirstParentOfType(ASTIfStatement.class);
        if (ifStatement == null) {
            return false;
        }

        // an if statement always has an expression
        ASTExpression expr = ifStatement.getFirstChildOfType(ASTExpression.class);
        List<ASTPrimaryPrefix> guardCalls = expr.findDescendantsOfType(ASTPrimaryPrefix.class);
        if (guardCalls.isEmpty()) {
            return false;
        }

        boolean foundGuard = false;
        // check all conditions in the if expression
        for (ASTPrimaryPrefix guardCall : guardCalls) {
            if (guardCall.jjtGetNumChildren() < 1
                    || guardCall.jjtGetChild(0).getImage() == null) {
                continue;
            }

            String guardMethodCall = getLastPartOfName(guardCall.jjtGetChild(0).getImage());
            boolean guardMethodCallMatches = guardStmtByLogLevel.get(methodCall).contains(guardMethodCall);
            boolean hasArguments = guardCall.jjtGetParent().hasDescendantOfType(ASTArgumentList.class);

            if (guardMethodCallMatches && !hasArguments) {
                // simple case: guard method without arguments found
                foundGuard = true;
            } else if (guardMethodCallMatches && hasArguments) {
                // java.util.logging: guard method with argument. Verify the log level
                String guardArgLogLevel = getLogLevelName(guardCall.jjtGetParent(), guardMethodCall);
                foundGuard = logLevel.equals(guardArgLogLevel);
            }

            if (foundGuard) {
                break;
            }
        }

        return foundGuard;
    }

    /**
     * Extracts the method name of the method call.
     * @param prefix the method call
     * @return the name of the called method
     */
    private String getMethodCallName(ASTPrimaryPrefix prefix) {
        String result = "";
        if (prefix.jjtGetNumChildren() == 1 && prefix.jjtGetChild(0) instanceof ASTName) {
            result = getLastPartOfName(prefix.jjtGetChild(0).getImage());
        }
        return result;
    }

    private String getLastPartOfName(String name) {
        String result = "";
        if (name != null) {
            result = name;
        }
        int dotIndex = result.lastIndexOf('.');
        if (dotIndex > -1 && result.length() > dotIndex + 1) {
            result = result.substring(dotIndex + 1);
        }
        return result;
    }

    /**
     * Determines the log level, that is used. It is either the called method name
     * itself or - if the method has a first argument a primary prefix - the first argument.
     *
     * @param node the method call
     * @param methodCallName the called method name previously determined
     * @return the log level
     */
    private String getLogLevelName(Node node, String methodCallName) {
        String logLevel = methodCallName;

        ASTPrimarySuffix suffix = node.getFirstDescendantOfType(ASTPrimarySuffix.class);
        if (suffix != null) {
            ASTArgumentList argumentList = suffix.getFirstDescendantOfType(ASTArgumentList.class);
            if (argumentList != null && argumentList.hasDescendantOfType(ASTName.class)) {
                ASTName name = argumentList.getFirstDescendantOfType(ASTName.class);
                String lastPart = getLastPartOfName(name.getImage());
                lastPart = lastPart.toLowerCase(Locale.ROOT);
                if (!lastPart.isEmpty()) {
                    logLevel = lastPart;
                }
            }
        }

        return logLevel;
    }

    private void extractProperties() {
        if (guardStmtByLogLevel.isEmpty()) {

            List<String> logLevels = new ArrayList<>(super.getProperty(LOG_LEVELS));
            List<String> guardMethods = new ArrayList<>(super.getProperty(GUARD_METHODS));

            if (guardMethods.isEmpty() && !logLevels.isEmpty()) {
                throw new IllegalArgumentException("Can't specify logLevels without specifying guardMethods.");
            }
            if (logLevels.size() > guardMethods.size()) {
                // reuse the last guardMethod for the remaining log levels
                int needed = logLevels.size() - guardMethods.size();
                String lastGuard = guardMethods.get(guardMethods.size() - 1);
                for (int i = 0; i < needed; i++) {
                    guardMethods.add(lastGuard);
                }
            }
            if (logLevels.size() != guardMethods.size()) {
                throw new IllegalArgumentException("For each logLevel a guardMethod must be specified.");
            }

            buildGuardStatementMap(logLevels, guardMethods);
        }
    }

    private void buildGuardStatementMap(List<String> logLevels, List<String> guardMethods) {
        for (int i = 0; i < logLevels.size(); i++) {
            String logLevel = logLevels.get(i);
            if (guardStmtByLogLevel.containsKey(logLevel)) {
                String combinedGuard = guardStmtByLogLevel.get(logLevel);
                combinedGuard += "|" + guardMethods.get(i);
                guardStmtByLogLevel.put(logLevel, combinedGuard);
            } else {
                guardStmtByLogLevel.put(logLevel, guardMethods.get(i));
            }
        }
    }
}
