package net.sourceforge.pmd.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import net.sourceforge.pmd.PMD;

/**
 * An analysis cache backed by a regular file.
 */
public class FileAnalysisCache extends AbstractAnalysisCache {

    private final File cacheFile;
    
    /**
     * Creates a new empty cache
     * @param cache The file on which to store analysis cache
     */
    private FileAnalysisCache(final File cache) {
        super();
        this.cacheFile = cache;
    }

    /**
     * Loads / creates a cache backed by the given file.
     * @param cacheFile The file which backs the file analysis cache.
     * @return The new / loaded cache.
     */
    public static FileAnalysisCache fromFile(final File cacheFile) {
        final FileAnalysisCache cache = new FileAnalysisCache(cacheFile);
        
        if (cacheFile.exists()) {
            try (
                final DataInputStream inputStream = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(cacheFile)));
            ) {
                final String cacheVersion = inputStream.readUTF();
                
                if (PMD.VERSION.equals(cacheVersion)) {
                    // Cache seems valid, load the rest
                    
                    // Get checksums
                    cache.rulesetChecksum = inputStream.readLong();
                    cache.classpathChecksum = inputStream.readLong();
                    
                    // Cached results
                    while (inputStream.available() > 0) {
                        final String fileName = inputStream.readUTF();
                        final long checksum = inputStream.readLong();
                        
                        cache.fileResultsCache.put(fileName, new AnalysisResult(checksum));
                    }
                } else {
                    LOG.info("Analysis cache invalidated, PMD version changed.");
                }
            } catch (final EOFException e) {
                LOG.warning("Cache file " + cacheFile.getPath() + " is malformed, will not be used for current analysis");
            } catch (final IOException e) {
                LOG.severe("Could not load analysis cache to file. " + e.getMessage());
            }
        }
        
        return cache;
    }

    @Override
    public void persist() {
        try (
            final DataOutputStream outputStream = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(cacheFile)));
        ) {
            outputStream.writeUTF(pmdVersion);
            
            outputStream.writeLong(rulesetChecksum);
            outputStream.writeLong(classpathChecksum);
            
            for (final Map.Entry<String, AnalysisResult> resultEntry : updatedResultsCache.entrySet()) {
                // TODO : In the future, we want to persist all violations, for now, just store files with NO violations
                if (resultEntry.getValue().getViolations().isEmpty()) {
                    outputStream.writeUTF(resultEntry.getKey());
                    outputStream.writeLong(resultEntry.getValue().getFileChecksum());
                }
            }
        } catch (final IOException e) {
            LOG.severe("Could not persist analysis cache to file. " + e.getMessage());
        }
    }
}
