package net.sourceforge.pmd.eclipse.ui.preferences.br;

import net.sourceforge.pmd.Rule;


/**
 * 
 * @author Brian Remedios
 */
public interface RuleCollection {

	boolean isEmpty();
	
	/**
	 * Return the only rule in the receiver or null if empty 
	 * or more than one is found.
	 * 
	 * @return
	 */
	Rule soleRule();
	
	/**
	 * Iterate through all the rules while the visitor returns
	 * true. Returns the result of the last rule visited.
	 * 
	 * @param visitor
	 * @return
	 */
	boolean rulesDo(RuleVisitor visitor);
}
