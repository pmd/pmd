package net.sourceforge.pmd.cpd;

import java.util.List;

public class Mark {

    private Locator locator;
    private int offset;
    private List code;

    public Mark(List code, Locator locator) {
        this.code = code;
        this.offset = code.size();
        this.locator = locator;
    }

    public Locator getLocator() {
        return this.locator;
    }

    public int getOffset() {
        return offset;
    }

    public TokenEntry tokenAt(int i) {
        if (i < 0) {
            return (TokenEntry)code.get((code.size() + i + offset) % code.size());
        } else {
            return (TokenEntry)code.get((i + offset) % code.size());
        }
    }
}
