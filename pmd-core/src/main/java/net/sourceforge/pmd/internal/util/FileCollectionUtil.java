/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
import net.sourceforge.pmd.util.log.MessageReporter.Level;
import net.sourceforge.pmd.util.log.internal.MessageReporterScope;

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

    @Deprecated
    public static FileCollector collectFiles(PMDConfiguration configuration, Set<Language> languages, MessageReporter reporter) {
        FileCollector collector = collectFiles(configuration, reporter);
        collector.filterLanguages(languages);
        return collector;
    }

    @Deprecated
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

        // This is to be removed when --short-names is removed.
        // If the new --relativize-paths-with option is specified (!= null), it takes precedence.
        boolean legacyShortNamesBehavior =
            configuration.getRelativizeRoots().isEmpty() && configuration.isReportShortNames();

        if (configuration.getInputPaths() != null) {
            for (Path path : configuration.getInputPathList()) {
                try {
                    if (legacyShortNamesBehavior) {
                        collector.relativizeWith(path.toString());
                    }
                    addRoot(collector, path);
                } catch (IOException e) {
                    collector.getReporter().errorEx("Error collecting " + path, e);
                }
            }
        }

        if (configuration.getInputUri() != null) {
            collectDB(collector, configuration.getInputUri());
        }

        if (configuration.getInputFilePath() != null) {
            collectFileList(collector, configuration.getInputFilePath());
        }

        if (configuration.getIgnoreFilePath() != null) {
            // disable trace logs for this secondary collector (would report 'adding xxx')
            MessageReporterScope mutedLog = new MessageReporterScope("exclude list", collector.getReporter());
            mutedLog.setLevel(Level.ERROR);
            try (FileCollector excludeCollector = FileCollector.newCollector(configuration.getLanguageVersionDiscoverer(), mutedLog)) {
                collectFileList(excludeCollector, configuration.getIgnoreFilePath());
                collector.exclude(excludeCollector);
            }
        }
    }


    public static void collectFiles(FileCollector collector, List<String> fileLocations) {
        for (String rootLocation : fileLocations) {
            try {
                addRoot(collector, Paths.get(rootLocation));
            } catch (IOException e) {
                collector.getReporter().errorEx("Error collecting " + rootLocation, e);
            }
        }
    }

    public static void collectFileList(FileCollector collector, String fileListLocation) {
        Path path = Paths.get(fileListLocation);
        if (!Files.exists(path)) {
            collector.getReporter().error("No such file {0}", fileListLocation);
            return;
        }

        String filePaths;
        try {
            filePaths = FileUtil.readFilelist(path.toFile());
        } catch (IOException e) {
            collector.getReporter().errorEx("Error reading {0}", new Object[] { fileListLocation }, e);
            return;
        }
        collectFiles(collector, Arrays.asList(filePaths.split(",")));
    }

    private static void addRoot(FileCollector collector, Path path) throws IOException {
        if (!Files.exists(path)) {
            collector.getReporter().error("No such file {0}", path);
            return;
        }

        if (Files.isDirectory(path)) {
            collector.addDirectory(path);
        } else if (path.toString().endsWith(".zip") || path.toString().endsWith(".jar")) {
            collector.addZipFileWithContent(path);
        } else if (Files.isRegularFile(path)) {
            collector.addFile(path);
        } else {
            collector.getReporter().trace("Ignoring {0}: not a regular file or directory", path);
        }
    }

    public static void collectDB(FileCollector collector, String uriString) {
        try {
            collector.getReporter().trace("Connecting to {0}", uriString);
            DBURI dbUri = new DBURI(uriString);
            DBMSMetadata dbmsMetadata = new DBMSMetadata(dbUri);
            collector.getReporter().trace("DBMSMetadata retrieved");
            List<SourceObject> sourceObjectList = dbmsMetadata.getSourceObjectList();
            collector.getReporter().trace("Located {0} database source objects", sourceObjectList.size());
            for (SourceObject sourceObject : sourceObjectList) {
                String falseFilePath = sourceObject.getPseudoFileName();
                collector.getReporter().trace("Adding database source object {0}", falseFilePath);

                try (Reader sourceCode = dbmsMetadata.getSourceCode(sourceObject)) {
                    String source = IOUtil.readToString(sourceCode);
                    collector.addSourceFile(source, falseFilePath);
                } catch (SQLException ex) {
                    collector.getReporter().warnEx("Cannot get SourceCode for {0}  - skipping ...",
                                                   new Object[] { falseFilePath},
                                                   ex);
                }
            }
        } catch (ClassNotFoundException e) {
            collector.getReporter().errorEx("Cannot get files from DB - probably missing database JDBC driver", e);
        } catch (Exception e) {
            collector.getReporter().errorEx("Cannot get files from DB - ''{0}''", new Object[] { uriString }, e);
        }
    }
}
