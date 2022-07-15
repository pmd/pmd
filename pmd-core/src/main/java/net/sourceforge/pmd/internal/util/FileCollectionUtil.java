/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.database.DBMSMetadata;
import net.sourceforge.pmd.util.database.DBURI;
import net.sourceforge.pmd.util.database.SourceObject;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.log.MessageReporter;
import net.sourceforge.pmd.util.log.internal.ErrorsAsWarningsReporter;

/**
 * @author Clément Fournier
 */
public final class FileCollectionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(FileCollectionUtil.class);

    private FileCollectionUtil() {

    }

    public static List<DataSource> collectorToDataSource(FileCollector collector) {
        List<DataSource> result = new ArrayList<>();
        for (TextFile file : collector.getCollectedFiles()) {
            result.add(file.toDataSourceCompat());
        }
        return result;
    }

    public static FileCollector collectFiles(PMDConfiguration configuration, Set<Language> languages, MessageReporter reporter) {
        FileCollector collector = collectFiles(configuration, reporter);
        collector.filterLanguages(languages);
        return collector;
    }

    private static FileCollector collectFiles(PMDConfiguration configuration, MessageReporter reporter) {
        FileCollector collector = FileCollector.newCollector(
            configuration.getLanguageVersionDiscoverer(),
            reporter
        );
        collectFiles(configuration, collector);
        return collector;
    }

    public static void collectFiles(PMDConfiguration configuration, FileCollector collector) {
        if (configuration.getSourceEncoding() != null) {
            collector.setCharset(configuration.getSourceEncoding());
        }

        if (configuration.getInputPaths() != null) {
            collectFiles(collector, configuration.getInputPaths());
        }

        if (configuration.getInputUri() != null) {
            collectDB(collector, configuration.getInputUri());
        }

        if (configuration.getInputFilePath() != null) {
            collectFileList(collector, configuration.getInputFilePath());
        }

        if (configuration.getIgnoreFilePath() != null) {
            // This is to be able to interpret the log (will report 'adding' xxx)
            LOG.debug("Now collecting files to exclude.");
            // errors like "excluded file does not exist" are reported as warnings.
            // todo better reporting of *where* exactly the path is
            MessageReporter mutedLog = new ErrorsAsWarningsReporter(collector.getReporter());
            try (FileCollector excludeCollector = collector.newCollector(mutedLog)) {
                collectFileList(excludeCollector, configuration.getIgnoreFilePath());
                collector.exclude(excludeCollector);
            }
        }
    }


    public static void collectFiles(FileCollector collector, String fileLocations) {
        for (String rootLocation : fileLocations.split(",")) {
            try {
                collector.relativizeWith(rootLocation);
                addRoot(collector, rootLocation);
            } catch (IOException e) {
                collector.getReporter().errorEx("Error collecting " + rootLocation, e);
            }
        }
    }

    public static void collectFileList(FileCollector collector, String fileListLocation) {
        LOG.debug("Reading file list {}.", fileListLocation);
        Path path = Paths.get(fileListLocation);
        if (!Files.exists(path)) {
            collector.getReporter().error("No such file {}", fileListLocation);
            return;
        }

        String filePaths;
        try {
            filePaths = FileUtil.readFilelist(path.toFile());
        } catch (IOException e) {
            collector.getReporter().errorEx("Error reading {}", new Object[] { fileListLocation }, e);
            return;
        }
        collectFiles(collector, filePaths);
    }

    private static void addRoot(FileCollector collector, String rootLocation) throws IOException {
        Path path = Paths.get(rootLocation);
        if (!Files.exists(path)) {
            collector.getReporter().error("No such file {0}", path);
            return;
        }

        if (Files.isDirectory(path)) {
            LOG.debug("Adding directory {}.", path);
            collector.addDirectory(path);
        } else if (rootLocation.endsWith(".zip") || rootLocation.endsWith(".jar")) {
            LOG.debug("Adding zip file {}.", path);
            @SuppressWarnings("PMD.CloseResource")
            FileSystem fs = collector.addZipFile(path);
            if (fs == null) {
                return;
            }
            for (Path zipRoot : fs.getRootDirectories()) {
                collector.addFileOrDirectory(zipRoot);
            }
        } else if (Files.isRegularFile(path)) {
            LOG.debug("Adding regular file {}.", path);
            collector.addFile(path);
        } else {
            LOG.debug("Ignoring {}: not a regular file or directory", path);
        }
    }

    public static void collectDB(FileCollector collector, String uriString) {
        try {
            LOG.debug("Connecting to {}", uriString);
            DBURI dbUri = new DBURI(uriString);
            DBMSMetadata dbmsMetadata = new DBMSMetadata(dbUri);
            LOG.trace("DBMSMetadata retrieved");
            List<SourceObject> sourceObjectList = dbmsMetadata.getSourceObjectList();
            LOG.trace("Located {} database source objects", sourceObjectList.size());
            for (SourceObject sourceObject : sourceObjectList) {
                String falseFilePath = sourceObject.getPseudoFileName();
                LOG.trace("Adding database source object {}", falseFilePath);

                try (Reader sourceCode = dbmsMetadata.getSourceCode(sourceObject)) {
                    String source = IOUtil.readToString(sourceCode);
                    collector.addSourceFile(source, falseFilePath);
                } catch (SQLException ex) {
                    collector.getReporter().warnEx("Cannot get SourceCode for {}  - skipping ...",
                                                   new Object[] { falseFilePath },
                                                   ex);
                }
            }
        } catch (ClassNotFoundException e) {
            collector.getReporter().errorEx("Cannot get files from DB - probably missing database JDBC driver", e);
        } catch (Exception e) {
            collector.getReporter().errorEx("Cannot get files from DB - ''{}''", new Object[] { uriString }, e);
        }
    }
}
