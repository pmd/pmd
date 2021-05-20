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

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
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

    private Map<String, String> guardStmtByLogLevel = new HashMap<>(12);

    /*
     * java util methods, that need special handling, e.g. they require an argument, which
     * determines the log level
     */
    private static final String JAVA_UTIL_LOG_METHOD = "log";
    private static final String JAVA_UTIL_LOG_GUARD_METHOD = "isLoggable";

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
    public Object visit(ASTStatementExpression node, Object data) {
        if (node.getNumChildren() < 1 || !(node.getChild(0) instanceof ASTPrimaryExpression)) {
            // only consider primary expressions
            return node;
        }

        ASTPrimaryExpression primary = (ASTPrimaryExpression) node.getChild(0);
        if (primary.getNumChildren() >= 2 && primary.getChild(0) instanceof ASTPrimaryPrefix) {
            ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) primary.getChild(0);
            String methodCall = getMethodCallName(prefix);
            String logLevel = getLogLevelName(primary, methodCall);

            if (guardStmtByLogLevel.containsKey(methodCall) && logLevel != null
                    && primary.getChild(1) instanceof ASTPrimarySuffix
                    && !hasGuard(primary, methodCall, logLevel)) {

                ASTPrimarySuffix primarySuffix = (ASTPrimarySuffix) primary.getChild(1);
                if (hasStringConcatenationWithVars(primarySuffix)
                        || hasArgumentWithMethodCall(primarySuffix)) {
                    addViolation(data, node);
                }
            }
        }
        return super.visit(node, data);
    }

    private boolean hasStringConcatenationWithVars(ASTPrimarySuffix node) {
        if (!node.isArguments() || node.getArgumentCount() == 0) {
            return false;
        }
        ASTArgumentList argumentList = node.getFirstChildOfType(ASTArguments.class).getFirstChildOfType(ASTArgumentList.class);
        for (JavaNode child : argumentList.children()) {
            if (child instanceof TypeNode && TypeTestUtil.isA(String.class, (TypeNode) child)) {
                // only consider the first String argument - which is the log message - and return here
                return !isConstantStringExpression(child);
            }
        }
        return false;
    }

    private boolean isConstantStringExpression(JavaNode expr) {
        if (expr instanceof ASTPrimaryExpression && expr.getChild(0) instanceof ASTPrimaryPrefix
                && expr.getChild(0).getNumChildren() == 1
                && expr.getChild(0).getChild(0) instanceof ASTLiteral) {
            return ((ASTLiteral) expr.getChild(0).getChild(0)).isStringLiteral();
        }
        if (expr instanceof ASTPrimaryExpression && expr.getChild(0) instanceof ASTPrimaryPrefix
                && expr.getChild(0).getNumChildren() == 1
                && expr.getChild(0).getChild(0) instanceof ASTName) {
            ASTName name = (ASTName) expr.getChild(0).getChild(0);
            if (!TypeTestUtil.isA(String.class, name)) {
                return false;
            }
            NameDeclaration nameDeclaration = name.getNameDeclaration();
            if (nameDeclaration instanceof VariableNameDeclaration) {
                ASTVariableDeclaratorId varId = ((VariableNameDeclaration) nameDeclaration).getDeclaratorId();
                if (!varId.isFinal()) {
                    return false;
                }
                if (varId.getParent() instanceof ASTVariableDeclarator) {
                    ASTVariableInitializer initializer = ((ASTVariableDeclarator) varId.getParent()).getInitializer();
                    if (initializer == null) {
                        return false;
                    }
                    return isConstantStringExpression(initializer.getFirstChildOfType(ASTExpression.class));
                }
            }
        }
        if (expr instanceof ASTExpression || expr instanceof ASTAdditiveExpression) {
            for (JavaNode child : expr.children()) {
                if (!isConstantStringExpression(child)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean hasArgumentWithMethodCall(ASTPrimarySuffix node) {
        if (!node.isArguments()) {
            return false;
        }

        ASTArgumentList arguments = node.getFirstDescendantOfType(ASTArgumentList.class);
        for (int i = 0; i < arguments.getNumChildren(); i++) {
            JavaNode expression = arguments.getChild(i);
            if (expression.getNumChildren() > 0) {
                JavaNode primaryExpr = expression.getChild(0);
                if (primaryExpr instanceof ASTPrimaryExpression && primaryExpr.getNumChildren() > 1) {
                    JavaNode lastChild = primaryExpr.getChild(primaryExpr.getNumChildren() - 1);
                    if (lastChild instanceof ASTPrimarySuffix) {
                        return ((ASTPrimarySuffix) lastChild).isArguments();
                    }
                }
            }
        }

        return false;
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
            if (guardCall.getNumChildren() < 1
                    || guardCall.getChild(0).getImage() == null) {
                continue;
            }

            String guardMethodCall = getLastPartOfName(guardCall.getChild(0));
            boolean guardMethodCallMatches = guardStmtByLogLevel.get(methodCall).contains(guardMethodCall);
            boolean hasArguments = guardCall.getParent().hasDescendantOfType(ASTArgumentList.class);

            if (guardMethodCallMatches && !JAVA_UTIL_LOG_GUARD_METHOD.equals(guardMethodCall)) {
                // simple case: guard method without the need to check arguments found
                foundGuard = true;
            } else if (guardMethodCallMatches && hasArguments) {
                // java.util.logging: guard method with argument. Verify the log level
                String guardArgLogLevel = getLogLevelName(guardCall.getParent(), guardMethodCall);
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
        if (prefix.getNumChildren() == 1 && prefix.getChild(0) instanceof ASTName) {
            result = getLastPartOfName(prefix.getChild(0));
        }
        return result;
    }

    private String getLastPartOfName(Node name) {
        String result = "";
        if (name != null) {
            result = name.getImage();
        }
        int dotIndex = result.lastIndexOf('.');
        if (dotIndex > -1 && result.length() > dotIndex + 1) {
            result = result.substring(dotIndex + 1);
        }
        return result;
    }

    /**
     * Gets the first child, first grand child, ... of the given types.
     * The children must follow the given order of types
     *
     * @param root the node from where to start the search
     * @param childrenTypes the list of types
     * @param <N> should match the last type of childrenType, otherwise you'll get a ClassCastException
     * @return the found child node or <code>null</code>
     */
    @SafeVarargs
    private static <N extends Node> N getFirstChild(Node root, Class<? extends Node> ... childrenTypes) {
        Node current = root;
        for (Class<? extends Node> clazz : childrenTypes) {
            Node child = current.getFirstChildOfType(clazz);
            if (child != null) {
                current = child;
            } else {
                return null;
            }
        }
        @SuppressWarnings("unchecked")
        N result = (N) current;
        return result;
    }

    /**
     * Determines the log level, that is used. It is either the called method name
     * itself or - in case java util logging is used, then it is the first argument of
     * the method call (if it exists).
     *
     * @param node the method call
     * @param methodCallName the called method name previously determined
     * @return the log level or <code>null</code> if it could not be determined
     */
    private String getLogLevelName(Node node, String methodCallName) {
        if (!JAVA_UTIL_LOG_METHOD.equals(methodCallName) && !JAVA_UTIL_LOG_GUARD_METHOD.equals(methodCallName)) {
            return methodCallName;
        }

        String logLevel = null;
        ASTPrimarySuffix suffix = node.getFirstDescendantOfType(ASTPrimarySuffix.class);
        if (suffix != null) {
            ASTArgumentList argumentList = suffix.getFirstDescendantOfType(ASTArgumentList.class);
            if (argumentList != null && argumentList.getNumChildren() > 0) {
                // at least one argument - the log level. If the method call is "log", then a message might follow
                ASTName name = GuardLogStatementRule.<ASTName>getFirstChild(argumentList.getChild(0),
                        ASTPrimaryExpression.class, ASTPrimaryPrefix.class, ASTName.class);
                String lastPart = getLastPartOfName(name);
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
