package net.sourceforge.pmd.rx.facts;

public class RuleViolation {
    private String desc = null;
    private Object fact = null;

    public RuleViolation(String desc, Object fact) {
	this.desc = desc;
	this.fact = fact;
    }

    public String getDescription() {
	return desc;
    }

    public Object getFact() {
	return fact;
    }
}
