package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Match implements Comparable {

    private int tokenCount;
    private List marks = new ArrayList();

    public Match(int tokenCount, Mark first, Mark second) {
        marks.add(first);
        marks.add(second);
        this.tokenCount = tokenCount;
    }

    public int getTokenCount() {
        return this.tokenCount;
    }

    public Iterator iterator() {
        return marks.iterator();
    }

    public int compareTo(Object o) {
        Match other = (Match)o;
        return other.getTokenCount() - this.getTokenCount();
    }
}
