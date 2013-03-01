package net.sourceforge.pmd.lang.java.rule.logging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.rule.optimizations.AbstractOptimizationRule;


/**
 * Check that log.debug and log.trace statements are guarded by some
 * log.isDebugEnabled() or log.isTraceEnabled() checks.
 * 
 * @author Heiko hwr@pilhuhn.de
 * @author Romain Pelisse - <belaran@gmail.com>
 *
 */
public class GuardLogStatementRule extends AbstractOptimizationRule implements Rule {

	private static final Map<String,String> guardStmtByLogLevel = new HashMap<String,String>(5);
	
	public GuardLogStatementRule() {
		guardStmtByLogLevel.put(".trace","isTraceEnabled");
		guardStmtByLogLevel.put(".debug","isDebugEnabled");
		guardStmtByLogLevel.put(".warn", "isWarnEnabled");
		guardStmtByLogLevel.put(".error", "isErrorEnabled");
		guardStmtByLogLevel.put(".info","isInfoEnabled");
	}
	
	private String lastPrefix(String string) {
		return (string != null && ! "".equals(string)) ? string.substring(string.lastIndexOf('.'), string.length()) : string;
	}
	
	public Object visit(ASTName name, Object data) {
			Node node = name.jjtGetParent();
			if ( node instanceof ASTPrimaryPrefix ) {
				
			} else
				return super.visit(name,data);
			if (name != null) {
				String lastPrefix = lastPrefix(name.getImage());
				if ( guardStmtByLogLevel.keySet().contains(lastPrefix)) {
					// TODO check for type 
					Node parent1 = name.getNthParent(5);
					boolean guardFound = false;
					if (parent1 instanceof ASTIfStatement) {
						guardFound = checkForGuard((ASTIfStatement) parent1, lastPrefix);
					}
					else if (parent1 instanceof ASTBlockStatement) {
						Node parent2 = name.getNthParent(7);
						if (parent2 instanceof ASTIfStatement) {
							guardFound = checkForGuard((ASTIfStatement) parent2, lastPrefix);						
						}
					}
					if (!guardFound)
						addViolation(data, name);
				}
			} 
		return super.visit(name, data);		
	}
			
	/**
	 * We stand on an if() check if it contains log.isDebugEnabled()
	 * @param stm
	 * @param isTrace true if log.trace() is used
	 * @return true if guard was found
	 */
	
	private boolean checkForGuard(ASTIfStatement stm, String logLevel) {

		List<ASTName> names = stm.findDescendantsOfType(ASTName.class);
		if (names == null || names.isEmpty() )
			return false;

		for ( ASTName name : names) {
			String image = name.getImage();
			if ( guardStmtByLogLevel.get(logLevel).equals(name.getImage()) );
				return true;
		}
		return false;
	}
}
