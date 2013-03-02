package net.sourceforge.pmd.lang.java.rule.logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.rule.optimizations.AbstractOptimizationRule;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;

/**
 * Check that log.debug and log.trace statements are guarded by some
 * log.isDebugEnabled() or log.isTraceEnabled() checks.
 * 
 * @author Heiko hwr@pilhuhn.de
 * @author Romain Pelisse - <belaran@gmail.com>
 * 
 */
public class GuardLogStatementRule extends AbstractOptimizationRule implements Rule {

	public static final StringMultiProperty LOG_LEVELS = new StringMultiProperty(
			"logLevels", "LogLevels to guard", new String[] {}, 1.0f, ',');

	public static final StringMultiProperty GUARD_METHODS = new StringMultiProperty(
			"guardsMethods", "method use to guard the log statement",
			new String[] {}, 2.0f, ',');

	private final Map<String, String> guardStmtByLogLevel = new HashMap<String, String>(
			5);

	public GuardLogStatementRule() {
		definePropertyDescriptor(LOG_LEVELS);
		definePropertyDescriptor(GUARD_METHODS);
	}

	@Override
	public Object visit(ASTCompilationUnit unit, Object data) {
		if ( guardStmtByLogLevel.isEmpty() ) {
			List<String> logLevels = new ArrayList<String>(Arrays.asList(super
					.getProperty(LOG_LEVELS)));
			List<String> guardMethods = new ArrayList<String>(Arrays.asList(super
					.getProperty(GUARD_METHODS)));
			
			if (guardMethods.isEmpty() && ! logLevels.isEmpty() ) {
				throw new IllegalArgumentException(
						"Can't specify guardMethods without specifiying logLevels.");
			}
			
			if (logLevels.isEmpty()) 
				setPropertiesDefaultValues(logLevels, guardMethods);
	
			buildGuardStatementMap(logLevels, guardMethods);
		}
		return super.visit(unit,data);
	}

	@Override
	public Object visit(ASTName name, Object data) {
		Node node = name.jjtGetParent();
		if (node instanceof ASTPrimaryPrefix) {

		} else
			return super.visit(name, data);
		if (name != null) {
			String lastPrefix = lastPrefix(name.getImage());
			if (guardStmtByLogLevel.keySet().contains(lastPrefix)) {
				// TODO check for type
				Node parent1 = name.getNthParent(5);
				boolean guardFound = false;
				if (parent1 instanceof ASTIfStatement) {
					guardFound = checkForGuard((ASTIfStatement) parent1,
							lastPrefix);
				} else if (parent1 instanceof ASTBlockStatement) {
					Node parent2 = name.getNthParent(7);
					if (parent2 instanceof ASTIfStatement) {
						guardFound = checkForGuard((ASTIfStatement) parent2,
								lastPrefix);
					}
				}
				if (!guardFound)
					addViolation(data, name);
			}
		}
		return super.visit(name, data);
	}

	private String lastPrefix(String string) {
		if (string != null && ! "".equals(string) ) {
			if ( string.contains(".") )
				return string.substring(string.lastIndexOf('.'), string.length());
		} 
		return string;
	}

	private boolean checkForGuard(ASTIfStatement stm, String logLevel) {

		List<ASTName> names = stm.findDescendantsOfType(ASTName.class);
		if (names == null || names.isEmpty())
			return false;

		for (ASTName name : names) {
			if ( name.getImage().endsWith(guardStmtByLogLevel.get(logLevel)) )
				return true;
		}
		return false;
	}
	
	private void setPropertiesDefaultValues(List<String> logLevels, List<String> guardMethods) {
		logLevels.add("trace");
		logLevels.add("debug");
		logLevels.add("info");
		logLevels.add("warn");
		logLevels.add("error");

		guardMethods.clear();
		guardMethods.add("isTraceEnabled");
		guardMethods.add("isDebugEnabled");
		guardMethods.add("isInfoEnabled");
		guardMethods.add("isWarnEnabled");
		guardMethods.add("isErrorEnabled");
	}

	private void buildGuardStatementMap(List<String> logLevels, List<String> guardMethods) {
		for (String logLevel : logLevels) {
			boolean found = false;
			for (String guardMethod : guardMethods) {
				if (!found && guardMethod.toLowerCase().contains(logLevel.toLowerCase())) {
					found = true;
					guardStmtByLogLevel.put("." + logLevel, guardMethod);
				}
			}
			
			if (!found)
				throw new IllegalArgumentException(
						"No guard method associated to the logLevel:"
								+ logLevel + ". Should be something like 'is"
								+ logLevel + "Enabled'.");
		}
	}
}
