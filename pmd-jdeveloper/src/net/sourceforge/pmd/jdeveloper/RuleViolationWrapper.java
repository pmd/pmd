package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.RuleViolation;

public class RuleViolationWrapper {
    private RuleViolation rv;

    public RuleViolationWrapper(RuleViolation rv) {
        this.rv = rv;
    }

    public RuleViolation getRV() {
        return this.rv;
    }

    public String toString() {
        return rv.getFilename() + ":" + rv.getLine() +":"+ rv.getDescription();
    }
}
