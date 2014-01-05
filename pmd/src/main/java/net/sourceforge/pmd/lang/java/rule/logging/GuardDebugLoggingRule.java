/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.logging;

import java.util.HashMap;

public class GuardDebugLoggingRule extends GuardLogStatementRule {

	public GuardDebugLoggingRule() {
		super.guardStmtByLogLevel = new HashMap<String, String>(1);
		super.guardStmtByLogLevel.put(".debug","isDebugEnabled");
	}
	
	@Override
	protected void extractProperties() {
		// This rule is not configurable
	}

}
