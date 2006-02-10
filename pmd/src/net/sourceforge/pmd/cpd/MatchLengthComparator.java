package net.sourceforge.pmd.cpd;

import java.util.Comparator;

public class MatchLengthComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        Match m1 = (Match) o1;
        Match m2 = (Match) o2;
        return m2.getLineCount() - m1.getLineCount();
    }
}
