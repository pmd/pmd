package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.cpd.TokenEntry;

import oracle.ide.model.Node;


public class CPDViolationWrapper {
    private final transient String label;
    public transient Node file;
    public transient TokenEntry mark;

    public CPDViolationWrapper(final TokenEntry mark, final Node file, 
                               final String label) {
        this.label = label;
        this.mark = mark;
        this.file = file;
    }

    public String toString() {
        return label;
    }
}
