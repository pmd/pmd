package net.sourceforge.pmd.cpd;

public class Match {

    private Locator start;
    private Locator end;
    private int tokenCount;

    public Match(int tokenCount, Locator start, Locator end) {
        this.start = start;
        this.end = end;
        this.tokenCount = tokenCount;
    }

    public int getTokenCount() {
        return this.tokenCount;
    }

    public Locator getStart() {
        return this.start;
    }

    public Locator getEnd() {
        return this.end;
    }
}
