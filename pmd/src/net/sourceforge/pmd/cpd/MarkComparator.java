/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package net.sourceforge.pmd.cpd;

import java.util.Comparator;
import java.util.List;

public class MarkComparator implements Comparator {

    private final int comparisonUpdateInterval;
    private CPDListener l;
    private long comparisons;
    private List tokens;

    public MarkComparator(CPDListener l, List tokens) {
        this(l, tokens, 10000);
    }

    public MarkComparator(CPDListener l, List tokens, int comparisonUpdateInterval) {
        this.l = l;
        this.tokens = tokens;
        this.comparisonUpdateInterval = comparisonUpdateInterval;
    }

    public int compare(Object o1, Object o2) {
        comparisons++;
        if (comparisons % comparisonUpdateInterval == 0) {
            l.comparisonCountUpdate(comparisons);
        }

        Mark mark1 = (Mark)o1;
        Mark mark2 = (Mark)o2;
        for (int i = 1; i < tokens.size(); i++) {
            int cmp = tokenAt(i, mark1).compareTo(tokenAt(i, mark2));
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    public TokenEntry tokenAt(int index, Mark mark) {
        return (TokenEntry)tokens.get((index + mark.getIndexIntoTokenArray()) % tokens.size());
    }
}
