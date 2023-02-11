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
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.util.Predicate;

/**
 * @since 6.48.0
 */
public class CPDReport {

    private final SourceManager sourceManager;
    private final List<Match> matches;
    private final Map<String, Integer> numberOfTokensPerFile;

    CPDReport(SourceManager sourceManager,
              List<Match> matches,
              Map<String, Integer> numberOfTokensPerFile) {
        this.sourceManager = sourceManager;
        this.matches = Collections.unmodifiableList(matches);
        this.numberOfTokensPerFile = Collections.unmodifiableMap(new TreeMap<>(numberOfTokensPerFile));
    }

    public List<Match> getMatches() {
        return matches;
    }

    public Map<String, Integer> getNumberOfTokensPerFile() {
        return numberOfTokensPerFile;
    }

    public Chars getSourceCodeSlice(Match match) {
        return sourceManager.getSlice(match.getFirstMark());
    }

    /**
     * Creates a new CPD report taking all the information from this report,
     * but filtering the matches.
     *
     * @param filter when true, the match will be kept.
     *
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

        return new CPDReport(sourceManager, filtered, this.getNumberOfTokensPerFile());
    }
}
