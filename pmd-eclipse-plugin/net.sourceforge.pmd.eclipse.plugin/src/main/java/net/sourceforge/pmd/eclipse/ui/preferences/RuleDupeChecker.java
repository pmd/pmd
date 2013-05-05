package net.sourceforge.pmd.eclipse.ui.preferences;

import net.sourceforge.pmd.Rule;

public interface RuleDupeChecker {

	boolean isDuplicate(Rule otherRule);
}
