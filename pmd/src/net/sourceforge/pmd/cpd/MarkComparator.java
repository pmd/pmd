/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.Comparator;
import java.util.List;

public class MarkComparator implements Comparator {

    private final static int COMPARISON_UPDATE_INTERVAL = 10000;
    private CPDListener l;
    private long comparisons;
    private List tokens;
    private int min;

    public MarkComparator(CPDListener l, List tokens, int min) {
        this.l = l;
        this.tokens = tokens;
        this.min = min;
    }

    public int compare(Object o1, Object o2) {
        comparisons++;
        if (comparisons % COMPARISON_UPDATE_INTERVAL == 0) {
            l.comparisonCountUpdate(comparisons);
        }

        Mark mark1 = (Mark)o1;
        Mark mark2 = (Mark)o2;
        for (int i = 0; i < tokens.size() && i < min; i++) {
            TokenEntry t1 = tokenAt(i, mark1);
            TokenEntry t2 = tokenAt(i, mark2);
            int cmp = t1.compareTo(t2);
            if (cmp != 0) {
                return cmp;
            }
        }
        return mark1.getIndexIntoTokenArray() - mark2.getIndexIntoTokenArray();
    }

    public TokenEntry tokenAt(int index, Mark mark) {
        return (TokenEntry)tokens.get((index + mark.getIndexIntoTokenArray()) % tokens.size());
    }
}