package net.sourceforge.pmd.cpd;

public class Match {

    private Mark start;
    private Mark end;
    private int tokenCount;

    public Match(int tokenCount, Mark start, Mark end) {
        this.start = start;
        this.end = end;
        this.tokenCount = tokenCount;
    }

    public int getTokenCount() {
        return this.tokenCount;
    }

    public Mark getStart() {
        return this.start;
    }

    public Mark getEnd() {
        return this.end;
    }
}
