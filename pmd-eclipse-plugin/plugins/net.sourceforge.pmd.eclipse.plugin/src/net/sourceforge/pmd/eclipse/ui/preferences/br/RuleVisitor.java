package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.Rule;

/**
 * 
 * @author Brian Remedios
 */
public interface RuleVisitor {

	/**
	 * Process the rule provided and return whether to continue
	 * processing other rules.
	 * 
	 * @param rule
	 * @return boolean
	 */
	public boolean accept(Rule rule);
}
