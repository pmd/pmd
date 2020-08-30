/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.Adler32;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.util.document.Chars;

/**
 * The result of a single file analysis.
 * Includes a checksum of the file and the complete list of violations detected.
 * @deprecated This is internal API, will be hidden with 7.0.0
 */
@Deprecated
@InternalApi
public class AnalysisResult {

    private final long fileChecksum;
    private final List<RuleViolation> violations;

    public AnalysisResult(final long fileChecksum, final List<RuleViolation> violations) {
        this.fileChecksum = fileChecksum;
        this.violations = violations;
    }

    static long computeFileChecksum(final Chars contents) {
        Adler32 checksum = new Adler32();
        checksum.update(contents.getBytes(StandardCharsets.UTF_16)); // don't use platform specific encoding
        return checksum.getValue();
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
