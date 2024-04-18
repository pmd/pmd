/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.AbstractConfiguration;
import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.InternalApiBridge;
import net.sourceforge.pmd.util.database.DBMSMetadata;
import net.sourceforge.pmd.util.database.DBURI;
import net.sourceforge.pmd.util.database.SourceObject;
import net.sourceforge.pmd.util.log.PmdReporter;
import net.sourceforge.pmd.util.log.internal.ErrorsAsWarningsReporter;

/**
 * @author Clément Fournier
 */
public final class FileCollectionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(FileCollectionUtil.class);

    private FileCollectionUtil() {

    }

    public static void collectFiles(CPDConfiguration cpdConfiguration, FileCollector collector) {
        if (cpdConfiguration.isSkipDuplicates()) {
            final Set<String> alreadyAddedFileNamesWithSize = new HashSet<>();
            collector.setFileFilter(fileId -> {
                Path path = Paths.get(fileId.getAbsolutePath());
                if (!Files.isRegularFile(path)) {
                    // file is not a simple file, maybe inside a ZIP archive
                    // don't filter these out
                    LOG.debug("Path {} is not a regular file, skipping in fileFilter", path);
                    return true;
                }

                try {
                    String signature = path.getFileName() + "_" + Files.size(path);
                    if (!alreadyAddedFileNamesWithSize.add(signature)) {
                        LOG.info("Skipping {} since it appears to be a duplicate file and --skip-duplicate-files is set",
                                path);
                        return false;
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                return true;
            });
        }

        collectFiles((AbstractConfiguration) cpdConfiguration, collector);
    }

    public static void collectFiles(AbstractConfiguration configuration, FileCollector collector) {
        if (configuration.getSourceEncoding() != null) {
            collector.setCharset(configuration.getSourceEncoding());
            collector.setRecursive(configuration.collectFilesRecursively());
        }


        collectFiles(collector, configuration.getInputPathList());

        if (configuration.getUri() != null) {
            collectDB(collector, configuration.getUri());
        }


        if (configuration.getInputFile() != null) {
            collectFileList(collector, configuration.getInputFile());
        }

        if (configuration.getIgnoreFile() != null || !configuration.getExcludes().isEmpty()) {
            // This is to be able to interpret the log (will report 'adding' xxx)
            LOG.debug("Now collecting files to exclude.");
            // errors like "excluded file does not exist" are reported as warnings.
            PmdReporter mutedLog = new ErrorsAsWarningsReporter(collector.getReporter());
            try (FileCollector excludeCollector = InternalApiBridge.newCollector(collector, mutedLog)) {

                if (configuration.getIgnoreFile() != null) {
                    // todo better reporting of *where* exactly the path is
                    collectFileList(excludeCollector, configuration.getIgnoreFile());
                }
                collectFiles(excludeCollector, configuration.getExcludes());
                collector.exclude(excludeCollector);
            }
        }
    }


    public static void collectFiles(FileCollector collector, List<Path> filePaths) {
        for (Path rootLocation : filePaths) {
            try {
                addRoot(collector, rootLocation);
            } catch (IOException e) {
                collector.getReporter().errorEx("Error collecting {0}", new Object[]{ rootLocation }, e);
            }
        }
    }

    public static void collectFileList(FileCollector collector, Path fileList) {
        LOG.debug("Reading file list {}.", fileList);
        if (!Files.exists(fileList)) {
            collector.getReporter().error("No such file {0}", fileList);
            return;
        }

        List<Path> filePaths;
        try {
            filePaths = FileUtil.readFilelistEntries(fileList);
        } catch (IOException e) {
            collector.getReporter().errorEx("Error reading {0}", new Object[] { fileList }, e);
            return;
        }
        collectFiles(collector, filePaths);
    }

    private static void addRoot(FileCollector collector, Path path) throws IOException {
        String pathStr = path.toString();
        if (!Files.exists(path)) {
            collector.getReporter().error("No such file {0}", path);
            return;
        }

        if (Files.isDirectory(path)) {
            LOG.debug("Adding directory {}.", path);
            collector.addDirectory(path);
        } else if (pathStr.endsWith(".zip") || pathStr.endsWith(".jar")) {
            collector.addZipFileWithContent(path);
        } else if (Files.isRegularFile(path)) {
            LOG.debug("Adding regular file {}.", path);
            collector.addFile(path);
        } else {
            LOG.debug("Ignoring {}: not a regular file or directory", path);
        }
    }

    public static void collectDB(FileCollector collector, URI uri) {
        try {
            LOG.debug("Connecting to {}", uri);
            DBURI dbUri = new DBURI(uri);
            DBMSMetadata dbmsMetadata = new DBMSMetadata(dbUri);
            LOG.trace("DBMSMetadata retrieved");
            List<SourceObject> sourceObjectList = dbmsMetadata.getSourceObjectList();
            LOG.trace("Located {} database source objects", sourceObjectList.size());
            for (SourceObject sourceObject : sourceObjectList) {
                String falseFilePath = sourceObject.getPseudoFileName();
                LOG.trace("Adding database source object {}", falseFilePath);

                try (Reader sourceCode = dbmsMetadata.getSourceCode(sourceObject)) {
                    String source = IOUtil.readToString(sourceCode);
                    collector.addSourceFile(FileId.fromPathLikeString(falseFilePath), source);
                } catch (SQLException ex) {
                    collector.getReporter().warnEx("Cannot get SourceCode for {0}  - skipping ...",
                                                   new Object[] { falseFilePath },
                                                   ex);
                }
            }
        } catch (ClassNotFoundException e) {
            collector.getReporter().errorEx("Cannot get files from DB - probably missing database JDBC driver", e);
        } catch (Exception e) {
            collector.getReporter().errorEx("Cannot get files from DB - ''{0}''", new Object[] { uri }, e);
        }
    }
}
