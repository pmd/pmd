package net.sourceforge.pmd.jedit;

import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;

public class SelectedRulesRuleSetFactory extends RuleSetFactory {
    
    RuleSets selectedRules = null;
    
    public SelectedRulesRuleSetFactory(RuleSets selectedRules) {
        super();
        this.selectedRules = selectedRules;
    }
    
    @Override
    public RuleSets createRuleSets(String referenceString) {
        return selectedRules;
    }
}