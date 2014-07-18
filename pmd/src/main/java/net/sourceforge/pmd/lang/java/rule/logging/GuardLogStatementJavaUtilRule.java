/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.logging;

import java.util.logging.Level;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;

public class GuardLogStatementJavaUtilRule extends GuardLogStatementRule {

	private static final String GUARD_METHOD_NAME = "isLoggable";

	private static String extendedXPath = "//PrimaryPrefix[ends-with(Name/@Image, '.log')]\n" + 
	        "[following-sibling::PrimarySuffix\n" + 
	        "    [ends-with(.//PrimaryPrefix/Name/@Image, 'LOG_LEVEL_UPPERCASE')]\n" + 
	        "    [count(../descendant::AdditiveExpression) > 0]\n" + 
	        "]\n" + 
	        "[count(ancestor::IfStatement/Expression/descendant::PrimaryExpression\n" + 
	        "    [ends-with(descendant::PrimaryPrefix[1]/Name/@Image,'GUARD')]) = 0\n" + 
	        "or\n" + 
	        "count(ancestor::IfStatement/Expression/descendant::PrimaryExpression\n" + 
	        "    [ends-with(descendant::PrimaryPrefix[2]/Name/@Image,'LOG_LEVEL_UPPERCASE')]) = 0]";
	
	@Override
	public Object visit(ASTCompilationUnit unit, Object data) {
	    String[] logLevels = getProperty(LOG_LEVELS);
	    String[] guardMethods = getProperty(GUARD_METHODS);

        if (super.guardStmtByLogLevel.isEmpty() && logLevels.length > 0 && guardMethods.length > 0) {
            configureGuards(logLevels, guardMethods);
        } else if ( super.guardStmtByLogLevel.isEmpty() ) {
            configureDefaultGuards();
        }

        findViolationForEachLogStatement(unit, data, extendedXPath);
		return super.visit(unit,data);
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
