/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.logging;

import java.util.logging.Level;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;

public class GuardLogStatementJavaUtilRule extends GuardLogStatementRule {

	private static final String GUARD_METHOD_NAME = "isLoggable";
	
	// Override default constructor - this rule can't be configured
	public GuardLogStatementJavaUtilRule() {};

	@Override
	public Object visit(ASTCompilationUnit unit, Object data) {
		if ( super.guardStmtByLogLevel.isEmpty() ) {
			super.guardStmtByLogLevel.put(formatLogLevelString(Level.FINEST), GUARD_METHOD_NAME);
			super.guardStmtByLogLevel.put(formatLogLevelString(Level.FINER), GUARD_METHOD_NAME);
			super.guardStmtByLogLevel.put(formatLogLevelString(Level.FINE), GUARD_METHOD_NAME);
			super.guardStmtByLogLevel.put(formatLogLevelString(Level.INFO), GUARD_METHOD_NAME);
			super.guardStmtByLogLevel.put(formatLogLevelString(Level.WARNING), GUARD_METHOD_NAME);
			super.guardStmtByLogLevel.put(formatLogLevelString(Level.SEVERE), GUARD_METHOD_NAME);
		}		
		return super.visit(unit,data);
	}
	
	private String formatLogLevelString(Level logLevel) {
		return "." + logLevel.toString().toLowerCase();
	}
}
