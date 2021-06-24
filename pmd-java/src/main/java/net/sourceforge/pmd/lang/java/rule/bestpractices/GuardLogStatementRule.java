/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * Check that log.debug, log.trace, log.error, etc... statements are guarded by
 * some test expression on log.isDebugEnabled() or log.isTraceEnabled().
 *
 * @author Romain Pelisse - &lt;belaran@gmail.com&gt;
 * @author Heiko Rupp - &lt;hwr@pilhuhn.de&gt;
 * @author Tammo van Lessen - provided original XPath expression
 *
 */
public class GuardLogStatementRule extends AbstractJavaRulechainRule {
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
    private static final PropertyDescriptor<List<String>> LOG_LEVELS =
            stringListProperty("logLevels")
                    .desc("LogLevels to guard")
                    .defaultValues("trace", "debug", "info", "warn", "error",
                                   "log", "finest", "finer", "fine", "info", "warning", "severe")
                    .delim(',')
                    .build();

    private static final PropertyDescriptor<List<String>> GUARD_METHODS =
            stringListProperty("guardsMethods")
                    .desc("Method use to guard the log statement")
                    .defaultValues("isTraceEnabled", "isDebugEnabled", "isInfoEnabled", "isWarnEnabled", "isErrorEnabled", "isLoggable")
                    .delim(',').build();

    private final Map<String, String> guardStmtByLogLevel = new HashMap<>(12);

    /*
     * java util methods, that need special handling, e.g. they require an argument, which
     * determines the log level
     */
    private static final String JAVA_UTIL_LOG_METHOD = "log";
    private static final String JAVA_UTIL_LOG_GUARD_METHOD = "isLoggable";

    public GuardLogStatementRule() {
        super(ASTExpressionStatement.class);
        definePropertyDescriptor(LOG_LEVELS);
        definePropertyDescriptor(GUARD_METHODS);
    }

    @Override
    public void start(RuleContext ctx) {
        extractProperties();
    }

    @Override
    public Object visit(ASTExpressionStatement node, Object data) {
        ASTExpression expr = node.getExpr();
        if (!(expr instanceof ASTMethodCall)) {
            return null;
        }

        ASTMethodCall methodCall = (ASTMethodCall) expr;
        String logLevel = getLogLevelName(methodCall);
        if (logLevel != null && guardStmtByLogLevel.containsKey(logLevel)) {
            if (needsGuard(methodCall) && !hasGuard(methodCall, logLevel)) {
                addViolation(data, node);
            }
        }
        return null;
    }

    private boolean needsGuard(ASTMethodCall node) {
        if (node.getArguments().size() == 0) {
            return false;
        }

        ASTArgumentList argumentList = node.getArguments();
        for (ASTExpression child : argumentList) {
            if (child.descendantsOrSelf()
                    .filterIs(ASTInfixExpression.class)
                    .filter(n -> n.getOperator() == BinaryOp.ADD)
                    .nonEmpty()
                && TypeTestUtil.isA(String.class, child)) {
                // only consider the first String argument - which is the log message - and return here
                return !isConstantStringExpression(child);
            }
        }

        return true;
    }

    private boolean isConstantStringExpression(ASTExpression expr) {
        if (expr == null) {
            return false;
        }

        if (expr instanceof ASTStringLiteral) {
            return true;
        }

        if (expr instanceof ASTVariableAccess) {
            ASTVariableAccess var = (ASTVariableAccess) expr;
            if (var.isCompileTimeConstant()) {
                return true;
            }
            JVariableSymbol symbol = var.getReferencedSym();
            if (symbol == null) {
                return false;
            }
            if (!var.getReferencedSym().isFinal()) {
                return false;
            }
            @Nullable
            ASTVariableDeclaratorId declaratorId = symbol.tryGetNode();
            if (declaratorId != null) {
                return isConstantStringExpression(declaratorId.getInitializer());
            }
        }

        if (expr instanceof ASTInfixExpression) {
            ASTInfixExpression infix = (ASTInfixExpression) expr;
            if (isConstantStringExpression(infix.getLeftOperand())
                    && isConstantStringExpression(infix.getRightOperand())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasGuard(ASTMethodCall node, String logLevel) {
        ASTIfStatement ifStatement = node.ancestors(ASTIfStatement.class).first();
        if (ifStatement == null) {
            return false;
        }

        for (ASTMethodCall maybeAGuardCall : ifStatement.getCondition().descendantsOrSelf().filterIs(ASTMethodCall.class)) {
            String guardMethodName = maybeAGuardCall.getMethodName();
            // the guard is adapted to the actual log statement

            if (!guardStmtByLogLevel.get(logLevel).contains(guardMethodName)) {
                continue;
            }

            if (JAVA_UTIL_LOG_GUARD_METHOD.equals(guardMethodName)) {
                // java.util.logging: guard method with argument. Verify the log level
                if (logLevel.equals(getJutilLogLevelInFirstArg(maybeAGuardCall))) {
                    return true;
                }
            } else {
                return true;
            }

        }
        return false;
    }

    /**
     * Determines the log level, that is used. It is either the called method name
     * itself or - in case java util logging is used, then it is the first argument of
     * the method call (if it exists).
     *
     * @param methodCall the method call
     *
     * @return the log level or <code>null</code> if it could not be determined
     */
    private @Nullable String getLogLevelName(ASTMethodCall methodCall) {
        String methodName = methodCall.getMethodName();
        if (!JAVA_UTIL_LOG_METHOD.equals(methodName)) {
            if (isUnguardedAccessOk(methodCall, 0)) {
                return null;
            }
            return methodName; // probably logger.warn(...)
        }

        // else it's java.util.logging, eg
        // LOGGER.log(Level.FINE, "m")
        if (isUnguardedAccessOk(methodCall, 1)) {
            return null;
        }
        return getJutilLogLevelInFirstArg(methodCall);
    }

    private @Nullable String getJutilLogLevelInFirstArg(ASTMethodCall methodCall) {
        ASTExpression firstArg = methodCall.getArguments().toStream().get(0);
        if (TypeTestUtil.isA(Level.class, firstArg) && firstArg instanceof ASTNamedReferenceExpr) {
            return ((ASTNamedReferenceExpr) firstArg).getName().toLowerCase(Locale.ROOT);
        }
        return null;
    }

    private boolean isUnguardedAccessOk(ASTMethodCall call, int messageArgIndex) {
        // return true if the statement has limited overhead even if unguarded,
        // so that we can ignore it
        return call.getArguments().toStream()
                   .drop(messageArgIndex) // remove the level argument if needed
                   .all(it -> it instanceof ASTStringLiteral || it instanceof ASTLambdaExpression || it instanceof ASTVariableAccess);
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
