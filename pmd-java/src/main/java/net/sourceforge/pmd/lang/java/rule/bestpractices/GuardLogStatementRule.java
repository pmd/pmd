/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jaxen.JaxenException;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
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

    private static final String XPATH_EXPRESSION =
            // first part deals with log4j / apache commons logging
            "//StatementExpression/PrimaryExpression/PrimaryPrefix[ends-with(Name/@Image, 'LOG_LEVEL')]\n"
            + "[..//AdditiveExpression]\n"
            + "[not(ancestor::IfStatement) or\n"
            + " not(ancestor::IfStatement/Expression/PrimaryExpression/PrimaryPrefix/Name[contains('GUARD', substring-after(@Image, '.'))])]\n"
            + "|\n"
            // this part deals with java util
            + "//StatementExpression/PrimaryExpression/PrimaryPrefix[ends-with(Name/@Image, 'LOG_LEVEL_UPPERCASE')]\n"
            + "[../../..//AdditiveExpression]\n"
            + "[not(ancestor::IfStatement) or\n"
            + " not(ancestor::IfStatement/Expression/PrimaryExpression\n"
            + "    [contains('GUARD', substring-after(PrimaryPrefix/Name/@Image, '.'))]\n"
            + "    [ends-with(PrimarySuffix//Name/@Image, 'LOG_LEVEL_UPPERCASE')])\n"
            + "]";

    public GuardLogStatementRule() {
        definePropertyDescriptor(LOG_LEVELS);
        definePropertyDescriptor(GUARD_METHODS);
    }

    @Override
    public Object visit(ASTCompilationUnit unit, Object data) {
        extractProperties();
        findViolationForEachLogStatement(unit, data, XPATH_EXPRESSION);
        return super.visit(unit, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data; // don't consider interfaces
        }
        return super.visit(node, data);
    }


    private void findViolationForEachLogStatement(ASTCompilationUnit unit, Object data, String xpathExpression) {
        for (Entry<String, String> entry : guardStmtByLogLevel.entrySet()) {
            List<Node> nodes = findViolations(unit, entry.getKey(), entry.getValue(), xpathExpression);
            for (Node node : nodes) {
                super.addViolation(data, node);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Node> findViolations(ASTCompilationUnit unit, String logLevel, String guard,
            String xpathExpression) {
        try {
            String xpath = xpathExpression.replaceAll("LOG_LEVEL_UPPERCASE", logLevel.toUpperCase())
                    .replaceAll("LOG_LEVEL", logLevel).replaceAll("GUARD", guard);
            return unit.findChildNodesWithXPath(xpath);
        } catch (JaxenException e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
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
            String logLevel = "." + logLevels.get(i);
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
