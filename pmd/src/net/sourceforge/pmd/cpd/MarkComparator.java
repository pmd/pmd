package net.sourceforge.pmd.cpd;

import java.util.Comparator;

public class MarkComparator implements Comparator {

    private final int comparisonUpdateInterval;
    private CPDListener l;
    private long comparisons;
    private int code;

    public MarkComparator(CPDListener l, int code) {
        this(l, code, 10000);
    }

    public MarkComparator(CPDListener l, int code, int comparisonUpdateInterval) {
        this.l = l;
        this.code = code;
        this.comparisonUpdateInterval = comparisonUpdateInterval;
    }

    public int compare(Object o1, Object o2) {
        comparisons++;
        if (comparisons % comparisonUpdateInterval == 0) {
            l.comparisonCountUpdate(comparisons);
        }

        Mark mark1 = (Mark)o1;
        Mark mark2 = (Mark)o2;
        for (int i = 1; i < code; i++) {
            int cmp = mark2.tokenAt(i).compareTo(mark1.tokenAt(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

}
