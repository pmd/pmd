/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.reporting.RuleViolation;

/**
 * The result of a single file analysis.
 * Includes a checksum of the file and the complete list of violations detected.
 */
public class AnalysisResult {

    private final long fileChecksum;
    private final List<RuleViolation> violations;

    public AnalysisResult(final long fileChecksum, final List<RuleViolation> violations) {
        this.fileChecksum = fileChecksum;
        this.violations = violations;
    }

    public AnalysisResult(final long fileChecksum) {
        this(fileChecksum, new ArrayList<>());
    }

    public long getFileChecksum() {
        return fileChecksum;
    }

    public List<RuleViolation> getViolations() {
        return violations;
    }

    public void addViolations(final List<RuleViolation> violations) {
        this.violations.addAll(violations);
    }

    public void addViolation(final RuleViolation ruleViolation) {
        this.violations.add(ruleViolation);
    }
}
