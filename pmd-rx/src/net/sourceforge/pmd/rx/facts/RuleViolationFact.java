package net.sourceforge.pmd.rx.facts;

public class RuleViolationFact {
    private String desc = null;
    private Object fact = null;

    public RuleViolationFact(Object fact, String desc) {
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
