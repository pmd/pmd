package net.sourceforge.pmd.cpd;

import java.util.Comparator;
import java.util.List;

public class MarkComparator implements Comparator {

    private static final int COMPARISON_UPDATE_INTERVAL = 100000;
    private CPDListener l;
    private long comparisons;
    private List code;

    public MarkComparator(CPDListener l, List code) {
        this.l = l;
        this.code = code;
    }

    public void reset() {
        comparisons = 0;
    }

    public int compare(Object o1, Object o2) {
        comparisons++;
        if (comparisons % COMPARISON_UPDATE_INTERVAL == 0) {
            l.comparisonCountUpdate(comparisons);
        }

        Mark mark1 = (Mark)o1;
        Mark mark2 = (Mark)o2;
        if (mark1.getOffset() == mark2.getOffset()) {
            return 0;
        }
        // don't compare leading 'mark' token
        for (int i = 1; i < code.size(); i++) {
            int cmp = mark2.tokenAt(i).compareTo(mark1.tokenAt(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }
}
