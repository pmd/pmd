/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Iterator;
import java.util.Map;

public class CPDReport {
    private final Iterator<Match> matches;
    private final Map<String, Integer> numberOfTokensPerFile;

    public CPDReport(final Iterator<Match> matches, final Map<String, Integer> numberOfTokensPerFile) {
        this.matches = matches;
        this.numberOfTokensPerFile = numberOfTokensPerFile;
    }

    public Iterator<Match> getMatches() {
        return matches;
    }

    public Map<String, Integer> getNumberOfTokensPerFile() {
        return numberOfTokensPerFile;
    }
}
