/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.util.Predicate;

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

    /**
     * Creates a new CPD report taking all the information from this report,
     * but filtering the matches.
     *
     * @param filter when true, the match will be kept.
     * @return copy of this report
     */
    @Experimental
    public CPDReport filterMatches(Predicate<Match> filter) {
        List<Match> filtered = new ArrayList<>();
        for (Match match : this.getMatches()) {
            if (filter.test(match)) {
                filtered.add(match);
            }
        }

        return new CPDReport(filtered, this.getNumberOfTokensPerFile());
    }
}
