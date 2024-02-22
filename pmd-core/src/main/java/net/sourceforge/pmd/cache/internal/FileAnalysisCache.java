/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.internal.RuleSets;
import net.sourceforge.pmd.reporting.RuleViolation;

/**
 * An analysis cache backed by a regular file.
 */
public class FileAnalysisCache extends AbstractAnalysisCache {

    private final File cacheFile;

    /**
     * Creates a new cache backed by the given file.
     * @param cache The file on which to store analysis cache
     */
    public FileAnalysisCache(final File cache) {
        super();
        this.cacheFile = cache;
    }

    @Override
    public void checkValidity(RuleSets ruleSets, ClassLoader auxclassPathClassLoader, Collection<? extends TextFile> files) {
        // load cached data before checking for validity
        loadFromFile(cacheFile, files);
        super.checkValidity(ruleSets, auxclassPathClassLoader, files);
    }

    /**
     * Loads cache data from the given file.
     *
     * @param cacheFile The file which backs the file analysis cache.
     */
    private void loadFromFile(final File cacheFile, Collection<? extends TextFile> files) {
        Map<String, FileId> idMap =
            files.stream().map(TextFile::getFileId)
                 .collect(Collectors.toMap(FileId::getUriString, id -> id));

        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.ANALYSIS_CACHE, "load")) {
            if (cacheExists()) {
                try (
                    DataInputStream inputStream = new DataInputStream(
                        new BufferedInputStream(Files.newInputStream(cacheFile.toPath())));
                ) {
                    final String cacheVersion = inputStream.readUTF();

                    if (PMDVersion.VERSION.equals(cacheVersion)) {
                        // Cache seems valid, load the rest

                        // Get checksums
                        rulesetChecksum = inputStream.readLong();
                        auxClassPathChecksum = inputStream.readLong();
                        executionClassPathChecksum = inputStream.readLong();

                        // Cached results
                        while (inputStream.available() > 0) {
                            final String filePathId = inputStream.readUTF();
                            FileId fileId = idMap.get(filePathId);
                            if (fileId == null) {
                                LOG.debug("File {} is in the cache but is not part of the analysis",
                                          filePathId);
                                fileId = FileId.fromURI(filePathId);
                            }
                            final long checksum = inputStream.readLong();

                            final int countViolations = inputStream.readInt();
                            final List<RuleViolation> violations = new ArrayList<>(countViolations);
                            for (int i = 0; i < countViolations; i++) {
                                violations.add(CachedRuleViolation.loadFromStream(inputStream, fileId, ruleMapper));
                            }

                            fileResultsCache.put(fileId, new AnalysisResult(checksum, violations));
                        }

                        LOG.debug("Analysis cache loaded from {}", cacheFile);
                    } else {
                        LOG.debug("Analysis cache invalidated, PMD version changed.");
                    }
                } catch (final EOFException e) {
                    LOG.warn("Cache file {} is malformed, will not be used for current analysis", cacheFile.getPath());
                } catch (final IOException e) {
                    LOG.error("Could not load analysis cache from file: {}", e.getMessage());
                }
            } else if (cacheFile.isDirectory()) {
                LOG.error("The configured cache location must be the path to a file, but is a directory.");
            }
        }
    }

    @Override
    public void persist() {
        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.ANALYSIS_CACHE, "persist")) {
            if (cacheFile.isDirectory()) {
                LOG.error("Cannot persist the cache, the given path points to a directory.");
                return;
            }

            boolean cacheFileShouldBeCreated = !cacheFile.exists();

            // Create directories missing along the way
            if (cacheFileShouldBeCreated) {
                final File parentFile = cacheFile.getAbsoluteFile().getParentFile();
                if (parentFile != null && !parentFile.exists()) {
                    parentFile.mkdirs();
                }
            }

            try (
                DataOutputStream outputStream = new DataOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(cacheFile.toPath())))
            ) {
                outputStream.writeUTF(pmdVersion);

                outputStream.writeLong(rulesetChecksum);
                outputStream.writeLong(auxClassPathChecksum);
                outputStream.writeLong(executionClassPathChecksum);

                for (final Map.Entry<FileId, AnalysisResult> resultEntry : updatedResultsCache.entrySet()) {
                    final List<RuleViolation> violations = resultEntry.getValue().getViolations();

                    outputStream.writeUTF(resultEntry.getKey().getUriString()); // the path id
                    outputStream.writeLong(resultEntry.getValue().getFileChecksum());

                    outputStream.writeInt(violations.size());
                    for (final RuleViolation rv : violations) {
                        CachedRuleViolation.storeToStream(outputStream, rv);
                    }
                }
                if (cacheFileShouldBeCreated) {
                    LOG.debug("Analysis cache created");
                } else {
                    LOG.debug("Analysis cache updated");
                }
            } catch (final IOException e) {
                LOG.error("Could not persist analysis cache to file: {}", e.getMessage());
            }
        }
    }

    @Override
    protected boolean cacheExists() {
        return cacheFile.exists() && cacheFile.isFile() && cacheFile.length() > 0;
    }
}
