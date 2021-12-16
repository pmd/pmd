/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.internal.util.PredicateUtil;
import net.sourceforge.pmd.internal.util.ShortFilenameUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.database.DBMSMetadata;
import net.sourceforge.pmd.util.database.DBURI;
import net.sourceforge.pmd.util.database.SourceObject;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.document.ReferenceCountedCloseable;
import net.sourceforge.pmd.util.document.TextFile;
import net.sourceforge.pmd.util.document.TextFileBuilder;

/**
 * This is a utility class for working with Files.
 *
 * @deprecated Is internal API
 */
@Deprecated
@InternalApi
public final class FileUtil {

    private static final Logger LOG = Logger.getLogger(PMD.class.getName());

    private FileUtil() {
    }

    /**
     * Helper method to get a filename without its extension
     *
     * @param fileName
     *            String
     * @return String
     */
    public static String getFileNameWithoutExtension(String fileName) {
        String name = fileName;

        int index = fileName.lastIndexOf('.');
        if (index != -1) {
            name = fileName.substring(0, index);
        }

        return name;
    }

    /**
     * Normalizes the filename by taking the casing into account, e.g. on
     * Windows, the filename is changed to lowercase only.
     *
     * @param fileName
     *            the file name
     * @return the normalized file name
     */
    public static String normalizeFilename(String fileName) {
        if (fileName != null && File.separatorChar == '\\') {
            // windows
            return fileName.toLowerCase(Locale.ROOT);
        }
        return fileName;
    }

    @SuppressWarnings("PMD.CloseResource")
    // the zip file can't be closed here, it's closed with the FileSystemCloseable during analysis
    private static void collect(List<TextFile> result,
                                String root,
                                PMDConfiguration configuration,
                                Predicate<? super Path> filter) throws IOException {
        Path rootPath = toExistingPath(root);

        Stream<Path> subfiles;
        @Nullable ReferenceCountedCloseable fsCloseable;
        if (Files.isDirectory(rootPath)) {
            fsCloseable = null;
            subfiles = Files.walk(rootPath);
        } else if (root.endsWith(".zip") || root.endsWith(".jar")) {
            URI uri = URI.create(root);
            FileSystem zipfs = FileSystems.newFileSystem(uri, Collections.emptyMap());
            fsCloseable = new ReferenceCountedCloseable(zipfs);
            subfiles = Files.walk(zipfs.getPath("/"));
        } else {
            if (filter.test(rootPath)) {
                result.add(createNioTextFile(configuration, rootPath, null));
            }
            return;
        }

        try (Stream<Path> walk = subfiles) {
            walk.filter(filter)
                .map(path -> createNioTextFile(configuration, path, fsCloseable))
                .forEach(result::add);
        }

    }

    public static @NonNull Path toExistingPath(String root) throws FileNotFoundException {
        Path file = Paths.get(root);
        if (!Files.exists(file)) {
            throw new FileNotFoundException(root);
        }
        return file;
    }

    /**
     * Handy method to find a certain pattern into a file. While this method
     * lives in the FileUtils, it was designed with with unit test in mind (to
     * check result redirected into a file)
     *
     * @param file
     * @param pattern
     * @return
     */
    public static boolean findPatternInFile(final File file, final String pattern) {

        Pattern regexp = Pattern.compile(pattern);
        Matcher matcher = regexp.matcher("");

        try {
            for (String line : Files.readAllLines(file.toPath(), StandardCharsets.UTF_8)) {
                matcher.reset(line); // reset the input
                if (matcher.find()) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    /**
     * Reads the file, which contains the filelist. This is used for the
     * command line arguments --filelist/-filelist for both PMD and CPD.
     * The separator in the filelist is a comma and/or newlines.
     *
     * @param filelist the file which contains the list of path names
     *
     * @return a list of file paths
     *
     * @throws IOException if the file couldn't be read
     */
    public static List<String> readFilelistEntries(Path filelist) throws IOException {
        return Files.readAllLines(filelist).stream()
                    .flatMap(it -> Arrays.stream(it.split(",")))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
    }

    /**
     * Determines all the files, that should be analyzed by PMD.
     *
     * @param configuration contains either the file path or the DB URI, from where to
     *                      load the files
     * @param languages     used to filter by file extension
     *
     * @return List of {@link DataSource} of files, not sorted
     *
     * @throws IOException If an IOException occurs
     */
    public static List<TextFile> getApplicableFiles(PMDConfiguration configuration,
                                                    Set<Language> languages) throws IOException {
        List<TextFile> result = new ArrayList<>();
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.COLLECT_FILES)) {

            internalGetApplicableFiles(result, configuration, languages);

        } catch (IOException ioe) {
            // then, close everything that's done for now, and rethrow
            Exception exception = IOUtil.closeAll(result);
            if (exception != null) {
                ioe.addSuppressed(exception);
            }
            throw ioe;
        }

        return result;
    }


    private static void internalGetApplicableFiles(List<TextFile> files, PMDConfiguration configuration, Set<Language> languages) throws IOException {
        List<String> ignoredFiles = getIgnoredFiles(configuration);
        LanguageVersion forcedVersion = configuration.getForceLanguageVersion();
        Predicate<Path> fileFilter =
            forcedVersion != null ? Files::isRegularFile // accept everything except dirs
                                  : PredicateUtil.toFileFilter(new LanguageFilenameFilter(languages));
        fileFilter = fileFilter.and(path -> !ignoredFiles.contains(path.toString()));

        for (String root : configuration.getAllInputPaths()) {
            collect(files, root, configuration, fileFilter);
        }

        if (null != configuration.getInputUri()) {
            getURIDataSources(files, configuration.getInputUri(), configuration);
        }

        if (null != configuration.getInputFilePath()) {
            @NonNull Path fileList = toExistingPath(configuration.getInputFilePath());

            try {
                for (String root : readFilelistEntries(fileList)) {
                    collect(files, root, configuration, fileFilter);
                }
            } catch (IOException ex) {
                throw new IOException("Problem with filelist: " + configuration.getInputFilePath(), ex);
            }
        }
    }

    private static List<String> getIgnoredFiles(PMDConfiguration configuration) throws IOException {
        if (null != configuration.getIgnoreFilePath()) {
            Path ignoreFile = toExistingPath(configuration.getIgnoreFilePath());
            try {
                // todo, if the file list contains relative paths, they
                //  should be taken relative to the filelist location,
                //  not the working directory, right?
                return readFilelistEntries(ignoreFile);
            } catch (IOException ex) {
                throw new IOException("Problem with exclusion filelist: " + ignoreFile, ex);
            }
        } else {
            return Collections.emptyList();
        }
    }


    private static void getURIDataSources(List<TextFile> collector, String uriString, PMDConfiguration config) throws IOException {

        try {
            DBURI dbUri = new DBURI(uriString);
            DBMSMetadata dbmsMetadata = new DBMSMetadata(dbUri);
            LOG.log(Level.FINE, "DBMSMetadata retrieved");
            List<SourceObject> sourceObjectList = dbmsMetadata.getSourceObjectList();
            LOG.log(Level.FINE, "Located {0} database source objects", sourceObjectList.size());
            for (SourceObject sourceObject : sourceObjectList) {
                String falseFilePath = sourceObject.getPseudoFileName();
                LOG.log(Level.FINEST, "Adding database source object {0}", falseFilePath);

                try {
                    LanguageVersion lv = config.getLanguageVersionOfFile(falseFilePath);
                    collector.add(TextFile.forReader(dbmsMetadata.getSourceCode(sourceObject), falseFilePath, lv).build());
                } catch (SQLException ex) {
                    if (LOG.isLoggable(Level.WARNING)) {
                        LOG.log(Level.WARNING, "Cannot get SourceCode for " + falseFilePath + "  - skipping ...", ex);
                    }
                }
            }
        } catch (URISyntaxException e) {
            throw new IOException("Cannot get DataSources from DBURI - \"" + uriString + "\"", e);
        } catch (SQLException e) {
            throw new IOException(
                "Cannot get DataSources from DBURI, couldn't access the database - \"" + uriString + "\"", e);
        } catch (ClassNotFoundException e) {
            throw new IOException(
                "Cannot get DataSources from DBURI, probably missing database jdbc driver - \"" + uriString + "\"", e);
        } catch (Exception e) {
            throw new IOException("Encountered unexpected problem with URI \"" + uriString + "\"", e);
        }
    }

    private static @Nullable String displayName(PMDConfiguration config, Path file) {
        if (config.isReportShortNames()) {
            return ShortFilenameUtil.determineFileName(config.getAllInputPaths(), file.toString());
        }
        return null;
    }

    public static TextFile createNioTextFile(PMDConfiguration config, Path file, @Nullable ReferenceCountedCloseable fsCloseable) {
        return buildNioTextFile(config, file).belongingTo(fsCloseable).build();
    }

    /**
     * Returns a builder that uses the configuration's encoding, and pre-fills the display name
     * using the input paths ({@link PMDConfiguration#getAllInputPaths()}) if {@link PMDConfiguration#isReportShortNames()}).
     */
    public static TextFileBuilder buildNioTextFile(PMDConfiguration config, Path file) {
        LanguageVersion langVersion = config.getLanguageVersionOfFile(file.toString());

        return TextFile.builderForPath(file, config.getSourceEncoding(), langVersion)
                       .withDisplayName(displayName(config, file));
    }

}
