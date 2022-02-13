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
import java.util.List;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.database.DBMSMetadata;
import net.sourceforge.pmd.util.database.DBURI;
import net.sourceforge.pmd.util.database.SourceObject;
import net.sourceforge.pmd.util.document.FileCollector;

/**
 * @author Clément Fournier
 */
public class FileCollectionUtil {

    public static void collectFiles(FileCollector collector, String fileLocations) {
        for (String rootLocation : fileLocations.split(",")) {
            try {
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
