package net.sourceforge.pmd.cpd;

import java.util.List;

// I can't remember if this is right, but my intent here is that
    // all marks share their reference to the 'code' list.
    public class Mark implements Comparable {
        private Locator locator;
        private int offset;
        private List code;
        private CPDListener listener;
        private static int comparisons;

        public Mark(List code, int offset, Locator locator, CPDListener listener) {
            this.code = code;
            this.offset = offset;
            this.locator = locator;
            this.listener = listener;
        }

        public Locator getLocator() {
            return this.locator;
        }

        public MyToken tokenAt(int i) {
            if (i < 0) {
                return (MyToken)code.get((code.size() + i + offset) % code.size());
            } else {
                return (MyToken)code.get((i + offset) % code.size());
            }
        }

        // implements the 'cyclic' comparison from the perl
        public int compareTo(Object o) {
            Mark.comparisons++;
            if (Mark.comparisons % 100000 == 0) {
                listener.comparisonCountUpdate(Mark.comparisons);
            }
            Mark mark = (Mark)o;
            if (mark.offset == this.offset) { return 0; }
            // don't compare leading 'mark' token
            for (int i = 1; i < code.size(); i++) {
                int cmp = this.tokenAt(i).compareTo(mark.tokenAt(i));
                if (cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        }
    }
