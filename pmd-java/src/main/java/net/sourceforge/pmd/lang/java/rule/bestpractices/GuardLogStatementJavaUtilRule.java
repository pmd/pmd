/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.List;
import java.util.logging.Level;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;

public class GuardLogStatementJavaUtilRule extends GuardLogStatementRule {

    private static final String GUARD_METHOD_NAME = "isLoggable";

    private static String extendedXPath = "//PrimaryPrefix[ends-with(Name/@Image, '.log')]\n"
            + "[following-sibling::PrimarySuffix\n"
            + "    [ends-with(.//PrimaryPrefix/Name/@Image, 'LOG_LEVEL_UPPERCASE')]\n"
            + "    [count(../descendant::AdditiveExpression) > 0]\n" + "]\n"
            + "[count(ancestor::IfStatement/Expression/descendant::PrimaryExpression\n"
            + "    [ends-with(descendant::PrimaryPrefix[1]/Name/@Image,'GUARD')]) = 0\n" + "or\n"
            + "count(ancestor::IfStatement/Expression/descendant::PrimaryExpression\n"
            + "    [ends-with(descendant::PrimaryPrefix[2]/Name/@Image,'LOG_LEVEL_UPPERCASE')]) = 0]";

    @Override
    public Object visit(ASTCompilationUnit unit, Object data) {
        if (isSlf4jOrLog4jImported(unit)) {
            return data;
        }

        String[] logLevels = getProperty(LOG_LEVELS).toArray(new String[0]); // TODO:cf convert to list
        String[] guardMethods = getProperty(GUARD_METHODS).toArray(new String[0]);

        if (super.guardStmtByLogLevel.isEmpty() && logLevels.length > 0 && guardMethods.length > 0) {
            configureGuards(logLevels, guardMethods);
        } else if (super.guardStmtByLogLevel.isEmpty()) {
            configureDefaultGuards();
        }

        findViolationForEachLogStatement(unit, data, extendedXPath);
        return super.visit(unit, data);
    }

    private boolean isSlf4jOrLog4jImported(ASTCompilationUnit unit) {
        List<ASTImportDeclaration> imports = unit.findChildrenOfType(ASTImportDeclaration.class);
        for (ASTImportDeclaration i : imports) {
            if (i.getImportedName().startsWith("org.slf4j") || i.getImportedName().startsWith("org.apache.log4j")) {
                return true;
            }
        }
        return false;
    }

    private void configureGuards(String[] logLevels, String[] guardMethods) {
        String[] methods = guardMethods;
        if (methods.length != logLevels.length) {
            String firstMethodName = guardMethods[0];
            methods = new String[logLevels.length];
            for (int i = 0; i < logLevels.length; i++) {
                methods[i] = firstMethodName;
            }
        }
        for (int i = 0; i < logLevels.length; i++) {
            super.guardStmtByLogLevel.put("." + logLevels[i], methods[i]);
        }
    }

    private void configureDefaultGuards() {
        super.guardStmtByLogLevel.put(formatLogLevelString(Level.FINEST), GUARD_METHOD_NAME);
        super.guardStmtByLogLevel.put(formatLogLevelString(Level.FINER), GUARD_METHOD_NAME);
        super.guardStmtByLogLevel.put(formatLogLevelString(Level.FINE), GUARD_METHOD_NAME);
        super.guardStmtByLogLevel.put(formatLogLevelString(Level.INFO), GUARD_METHOD_NAME);
        super.guardStmtByLogLevel.put(formatLogLevelString(Level.WARNING), GUARD_METHOD_NAME);
        super.guardStmtByLogLevel.put(formatLogLevelString(Level.SEVERE), GUARD_METHOD_NAME);
    }

    private String formatLogLevelString(Level logLevel) {
        return "." + logLevel.toString().toLowerCase();
    }
}
