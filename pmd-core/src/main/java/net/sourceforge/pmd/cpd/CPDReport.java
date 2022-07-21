/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @since 6.48.0
 */
public class CPDReport {
    private final List<Match> matches;
    private final Map<String, Integer> numberOfTokensPerFile;

    CPDReport(final List<Match> matches, final Map<String, Integer> numberOfTokensPerFile) {
        this.matches = Collections.unmodifiableList(matches);
        this.numberOfTokensPerFile = Collections.unmodifiableMap(new TreeMap<>(numberOfTokensPerFile));
    }

    public List<Match> getMatches() {
        return matches;
    }

    public Map<String, Integer> getNumberOfTokensPerFile() {
        return numberOfTokensPerFile;
    }
}
