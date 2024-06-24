/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.reporting.Report;

/**
 * The result of a CPD analysis. This is rendered by a {@link CPDReportRenderer}.
 *
 * @since 6.48.0
 */
public class CPDReport {

    private final SourceManager sourceManager;
    private final List<Match> matches;
    private final Map<FileId, Integer> numberOfTokensPerFile;
    private final List<Report.ProcessingError> processingErrors;

    CPDReport(SourceManager sourceManager,
              List<Match> matches,
              Map<FileId, Integer> numberOfTokensPerFile,
              List<Report.ProcessingError> processingErrors) {
        this.sourceManager = sourceManager;
        this.matches = Collections.unmodifiableList(matches);
        this.numberOfTokensPerFile = Collections.unmodifiableMap(new TreeMap<>(numberOfTokensPerFile));
        this.processingErrors = Collections.unmodifiableList(processingErrors);
    }

    /** Return the list of duplication matches found by the CPD analysis. */
    public List<Match> getMatches() {
        return matches;
    }

    /** Return a map containing the number of tokens by processed file. */
    public Map<FileId, Integer> getNumberOfTokensPerFile() {
        return numberOfTokensPerFile;
    }

    /** Returns the list of occurred processing errors. */
    public List<Report.ProcessingError> getProcessingErrors() {
        return processingErrors;
    }

    /**
     * Return the slice of source code where the mark was found. This
     * returns the entire lines from the start to the end line of the
     * mark.
     */
    public Chars getSourceCodeSlice(Mark mark) {
        return sourceManager.getSlice(mark);
    }


    /**
     * Creates a new CPD report taking all the information from this report,
     * but filtering the matches. Note that the {@linkplain #getNumberOfTokensPerFile() token count map}
     * is not filtered.
     *
     * @param filter when true, the match will be kept.
     *
     * @return copy of this report
     */
    public CPDReport filterMatches(Predicate<Match> filter) {
        List<Match> filtered = this.matches.stream().filter(filter).collect(Collectors.toList());

        return new CPDReport(sourceManager, filtered, this.getNumberOfTokensPerFile(), this.getProcessingErrors());
    }

    /**
     * Return the display name of the given file.
     */
    public String getDisplayName(FileId fileId) {
        return sourceManager.getFileDisplayName(fileId);
    }
}
