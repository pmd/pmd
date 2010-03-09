package net.sourceforge.pmd.jedit;

import javax.swing.JCheckBox;

import org.gjt.sp.jedit.jEdit;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;

@Deprecated
public class RuleCheckBox extends JCheckBox {

    private static final long serialVersionUID = 4779236734852745643L;

    private final Rule rule;

    private final RuleSet ruleset;

    public RuleCheckBox(Rule rule, RuleSet rs) {
        super(rule.getName());
        this.rule = rule;
        this.ruleset = rs;

        setSelected(jEdit.getBooleanProperty(PMDJEditPlugin.OPTION_RULES_PREFIX + rule.getName(), true));
    }

    public String toString() {
        return rule.getName();
    }

    /**
     * @return the rule
     */
    public Rule getRule() {
        return rule;
    }

    /**
     * @return the ruleset
     */
    public RuleSet getRuleset() {
        return ruleset;
    }

}
