/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.annotation.InternalApi;

/**
 * An analysis cache backed by a regular file.
 *
 * @deprecated This is internal API, will be hidden with 7.0.0
 */
@Deprecated
@InternalApi
public class FileAnalysisCache extends AbstractAnalysisCache {

    private final File cacheFile;

    /**
     * Creates a new cache backed by the given file, and attempts to load pre-existing data from it.
     * @param cache The file on which to store analysis cache
     */
    public FileAnalysisCache(final File cache) {
        super();
        this.cacheFile = cache;

        loadFromFile(cache);
    }

    /**
     * Loads cache data from the given file.
     * @param cacheFile The file which backs the file analysis cache.
     */
    private void loadFromFile(final File cacheFile) {
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
                        final String fileName = inputStream.readUTF();
                        final long checksum = inputStream.readLong();

                        final int countViolations = inputStream.readInt();
                        final List<RuleViolation> violations = new ArrayList<>(countViolations);
                        for (int i = 0; i < countViolations; i++) {
                            violations.add(CachedRuleViolation.loadFromStream(inputStream, fileName, ruleMapper));
                        }

                        fileResultsCache.put(fileName, new AnalysisResult(checksum, violations));
                    }

                    LOG.info("Analysis cache loaded");
                } else {
                    LOG.info("Analysis cache invalidated, PMD version changed.");
                }
            } catch (final EOFException e) {
                LOG.warning("Cache file " + cacheFile.getPath() + " is malformed, will not be used for current analysis");
            } catch (final IOException e) {
                LOG.severe("Could not load analysis cache from file. " + e.getMessage());
            }
        } else if (cacheFile.isDirectory()) {
            LOG.severe("The configured cache location must be the path to a file, but is a directory.");
        }
    }

    @Override
    public void persist() {

        if (cacheFile.isDirectory()) {
            LOG.severe("Cannot persist the cache, the given path points to a directory.");
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

            for (final Map.Entry<String, AnalysisResult> resultEntry : updatedResultsCache.entrySet()) {
                final List<RuleViolation> violations = resultEntry.getValue().getViolations();

                outputStream.writeUTF(resultEntry.getKey()); // the full filename
                outputStream.writeLong(resultEntry.getValue().getFileChecksum());

                outputStream.writeInt(violations.size());
                for (final RuleViolation rv : violations) {
                    CachedRuleViolation.storeToStream(outputStream, rv);
                }
            }
            if (cacheFileShouldBeCreated) {
                LOG.info("Analysis cache created");
            } else {
                LOG.info("Analysis cache updated");
            }
        } catch (final IOException e) {
            LOG.severe("Could not persist analysis cache to file. " + e.getMessage());
        }
    }


    @Override
    protected boolean cacheExists() {
        return cacheFile.exists() && cacheFile.isFile() && cacheFile.length() > 0;
    }
}
