/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.RuleViolation;

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

    public AnalysisResult(final File sourceFile) {
        this(computeFileChecksum(sourceFile), new ArrayList<RuleViolation>());
    }

    private static long computeFileChecksum(final File sourceFile) {
        try (
            CheckedInputStream stream = new CheckedInputStream(
                new BufferedInputStream(Files.newInputStream(sourceFile.toPath())), new Adler32());
        ) {
            // Just read it, the CheckedInputStream will update the checksum on it's own
            IOUtils.skipFully(stream, sourceFile.length());

            return stream.getChecksum().getValue();
        } catch (final IOException ignored) {
            // We don't really care, if it's unreadable
            // the analysis will fail and report the error on it's own since the checksum won't match
        }

        return 0;
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
