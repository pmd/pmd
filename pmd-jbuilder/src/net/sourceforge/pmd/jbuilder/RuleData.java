package net.sourceforge.pmd.jbuilder;

public class RuleData {
    protected String ruleName;
    protected boolean selected;
    protected RuleSetProperty rsp;

    public RuleData(String ruleName) {
        this.ruleName = ruleName;
        selected = true;
    }

    public RuleData(String ruleName, RuleSetProperty rsp) {
        this(ruleName);
        this.rsp = rsp;
        this.selected = rsp.isRuleSelected(ruleName);
    }

    public String getName() {
        return ruleName;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void invertSelected() {
        this.selected = !this.selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public String toString() {
        return this.ruleName;
    }
}