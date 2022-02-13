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

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.database.DBMSMetadata;
import net.sourceforge.pmd.util.database.DBURI;
import net.sourceforge.pmd.util.database.SourceObject;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.document.FileCollector;
import net.sourceforge.pmd.util.document.TextFile;
import net.sourceforge.pmd.util.log.PmdLogger;

/**
 * @author Clément Fournier
 */
public final class FileCollectionUtil {

    private FileCollectionUtil() {

    }

    public static List<DataSource> collectorToDataSource(FileCollector collector) {
        List<DataSource> result = new ArrayList<>();
        for (TextFile file : collector.getCollectedFiles()) {
            result.add(file.toDataSourceCompat());
        }
        return result;
    }

    public static FileCollector collectFiles(PMDConfiguration configuration, Set<Language> languages, PmdLogger logger) {
        FileCollector collector = collectFiles(configuration, logger);
        collector.filterLanguages(languages);
        return collector;
    }

    private static FileCollector collectFiles(PMDConfiguration configuration, PmdLogger logger) {
        FileCollector collector = FileCollector.newCollector(
            configuration.getLanguageVersionDiscoverer(),
            logger
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
            // todo disable trace logs for this secondary collector
            try (FileCollector excludeCollector = FileCollector.newCollector(configuration.getLanguageVersionDiscoverer(), collector.getLog())) {
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
                collector.getLog().errorEx("Error collecting " + rootLocation, e);
            }
        }
    }

    public static void collectFileList(FileCollector collector, String fileListLocation) {
        Path path = Paths.get(fileListLocation);
        if (!Files.exists(path)) {
            collector.getLog().error("No such file {0}", fileListLocation);
            return;
        }

        String filePaths;
        try {
            filePaths = FileUtil.readFilelist(path.toFile());
        } catch (IOException e) {
            collector.getLog().errorEx("Error reading {0}", new Object[] { fileListLocation }, e);
            return;
        }
        collectFiles(collector, filePaths);
    }

    private static void addRoot(FileCollector collector, String rootLocation) throws IOException {
        Path path = Paths.get(rootLocation);
        if (!Files.exists(path)) {
            collector.getLog().error("No such file {0}", path);
            return;
        }

        if (Files.isDirectory(path)) {
            collector.addDirectory(path);
        } else if (rootLocation.endsWith(".zip") || rootLocation.endsWith(".jar")) {
            @SuppressWarnings("PMD.CloseResource")
            FileSystem fs = collector.addZipFile(path);
            if (fs == null) {
                return;
            }
            for (Path zipRoot : fs.getRootDirectories()) {
                collector.addFileOrDirectory(zipRoot);
            }
        } else if (Files.isRegularFile(path)) {
            collector.addFile(path);
        } else {
            collector.getLog().trace("Ignoring {0}: not a regular file or directory", path);
        }
    }

    public static void collectDB(FileCollector collector, String uriString) {
        try {
            collector.getLog().trace("Connecting to {0}", uriString);
            DBURI dbUri = new DBURI(uriString);
            DBMSMetadata dbmsMetadata = new DBMSMetadata(dbUri);
            collector.getLog().trace("DBMSMetadata retrieved");
            List<SourceObject> sourceObjectList = dbmsMetadata.getSourceObjectList();
            collector.getLog().trace("Located {0} database source objects", sourceObjectList.size());
            for (SourceObject sourceObject : sourceObjectList) {
                String falseFilePath = sourceObject.getPseudoFileName();
                collector.getLog().trace("Adding database source object {0}", falseFilePath);

                try (Reader sourceCode = dbmsMetadata.getSourceCode(sourceObject)) {
                    String source = IOUtils.toString(sourceCode);
                    collector.addSourceFile(source, falseFilePath);
                } catch (SQLException ex) {
                    collector.getLog().warningEx("Cannot get SourceCode for {0}  - skipping ...",
                                                 new Object[] { falseFilePath},
                                                 ex);
                }
            }
        } catch (ClassNotFoundException e) {
            collector.getLog().errorEx("Cannot get files from DB - probably missing database JDBC driver", e);
        } catch (Exception e) {
            collector.getLog().errorEx("Cannot get files from DB - ''{0}''", new Object[] { uriString }, e);
        }
    }
}
