package net.sourceforge.pmd.cpd;

public class Match implements Comparable {

    private Mark firstOccurrence;
    private Mark secondOccurrence;
    private int tokenCount;

    public Match(int tokenCount, Mark firstOccurrence, Mark secondOccurrence) {
        this.firstOccurrence = firstOccurrence;
        this.secondOccurrence = secondOccurrence;
        this.tokenCount = tokenCount;
    }

    public int getTokenCount() {
        return this.tokenCount;
    }

    public Mark getFirstOccurrence() {
        return this.firstOccurrence;
    }

    public Mark getSecondOccurrence() {
        return this.secondOccurrence;
    }

    public int compareTo(Object o) {
        Match other = (Match)o;
        return other.getTokenCount() - this.getTokenCount();
    }
}
