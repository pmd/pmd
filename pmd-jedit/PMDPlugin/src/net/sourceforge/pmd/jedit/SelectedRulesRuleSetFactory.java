package net.sourceforge.pmd.jedit;

import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;

/**
 * A ruleset factory that only supplies the rules it is told about.
 */
public class SelectedRulesRuleSetFactory extends RuleSetFactory {
    
    RuleSets selectedRules = null;
    
    /**
     * @param selectedRules The rule sets this factory knows about. It will only
     * provide these rules and will not create any other rules.
     */
    public SelectedRulesRuleSetFactory(RuleSets selectedRules) {
        super();
        this.selectedRules = selectedRules;
    }
    
    /**
     * @param referenceString Not used. This factory only provides the rules it was
     * given in the constructor.
     */
    @Override
    public RuleSets createRuleSets(String referenceString) {
        return selectedRules;
    }
}