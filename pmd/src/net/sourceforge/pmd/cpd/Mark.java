package net.sourceforge.pmd.cpd;

import java.util.List;

public class Mark {

    private Locator locator;
    private int offset;
    private List code;

    public Mark(List code, int offset, Locator locator) {
        this.code = code;
        this.offset = offset;
        this.locator = locator;
    }

    public Locator getLocator() {
        return this.locator;
    }

    public int getOffset() {
        return offset;
    }

    public TokenEntry tokenAt(int index) {
        if (index < 0) {
            return (TokenEntry)code.get((code.size() + index + offset) % code.size());
        } else {
            return (TokenEntry)code.get((index + offset) % code.size());
        }
    }

}
