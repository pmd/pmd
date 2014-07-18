/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.rule.optimizations.AbstractOptimizationRule;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;

import org.jaxen.JaxenException;

/**
 * Check that log.debug, log.trace, log.error, etc... statements are guarded by
 * some test expression on log.isDebugEnabled() or log.isTraceEnabled().
 * 
 * @author Romain Pelisse - <belaran@gmail.com>
 * @author Heiko Rupp - <hwr@pilhuhn.de>
 * @author Tammo van Lessen - provided original XPath expression
 * 
 */
public class GuardLogStatementRule extends AbstractOptimizationRule implements
		Rule {

	public static final StringMultiProperty LOG_LEVELS = new StringMultiProperty(
			"logLevels", "LogLevels to guard", new String[] {}, 1.0f, ',');

	public static final StringMultiProperty GUARD_METHODS = new StringMultiProperty(
			"guardsMethods", "method use to guard the log statement",
			new String[] {}, 2.0f, ',');

	protected Map<String, String> guardStmtByLogLevel = new HashMap<String, String>(
			5);

    private static final String xpathExpression = "//PrimaryPrefix[ends-with(Name/@Image, 'LOG_LEVEL')]"
            + "[count(../descendant::AdditiveExpression) > 0]"
            + "[count(ancestor::IfStatement/Expression/descendant::PrimaryExpression["
                + "ends-with(descendant::PrimaryPrefix/Name/@Image,'GUARD')]) = 0]";

	public GuardLogStatementRule() {
		definePropertyDescriptor(LOG_LEVELS);
		definePropertyDescriptor(GUARD_METHODS);
	}

	@Override
	public Object visit(ASTCompilationUnit unit, Object data) {
		extractProperties();
		findViolationForEachLogStatement(unit, data, xpathExpression);
		return super.visit(unit, data);
	}

	protected void findViolationForEachLogStatement(ASTCompilationUnit unit, Object data, String xpathExpression) {
		for (Entry<String, String> entry : guardStmtByLogLevel.entrySet()) {
			List<Node> nodes = findViolations(unit, entry.getKey(),
					entry.getValue(), xpathExpression);
			for (Node node : nodes) {
				super.addViolation(data, node);
			}
		}		
	}
	
	@SuppressWarnings("unchecked")
	private List<Node> findViolations(ASTCompilationUnit unit, String logLevel,
			String guard, String xpathExpression) {
		try {
			return unit.findChildNodesWithXPath(xpathExpression
			        .replaceAll("LOG_LEVEL_UPPERCASE", logLevel.toUpperCase())
			        .replaceAll("LOG_LEVEL", logLevel)
			        .replaceAll("GUARD", guard));
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		return Collections.EMPTY_LIST;
	}

	private void setPropertiesDefaultValues(List<String> logLevels,
			List<String> guardMethods) {
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

	protected void extractProperties() {
		if (guardStmtByLogLevel.isEmpty()) {

			List<String> logLevels = new ArrayList<String>(Arrays.asList(super
					.getProperty(LOG_LEVELS)));
			List<String> guardMethods = new ArrayList<String>(
					Arrays.asList(super.getProperty(GUARD_METHODS)));

			if (guardMethods.isEmpty() && !logLevels.isEmpty()) {
				throw new IllegalArgumentException(
						"Can't specify guardMethods without specifiying logLevels.");
			}

			if (logLevels.isEmpty())
				setPropertiesDefaultValues(logLevels, guardMethods);

			buildGuardStatementMap(logLevels, guardMethods);
		}
	}

	protected void buildGuardStatementMap(List<String> logLevels,
			List<String> guardMethods) {
		for (String logLevel : logLevels) {
			boolean found = false;
			for (String guardMethod : guardMethods) {
				if (!found
						&& guardMethod.toLowerCase().contains(
								logLevel.toLowerCase())) {
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
