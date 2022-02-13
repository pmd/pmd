/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.ProviderNotFoundException;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.document.internal.LanguageDiscoverer;
import net.sourceforge.pmd.util.log.PmdLogger;
import net.sourceforge.pmd.util.log.SimplePmdLogger;

/**
 * Collects files to analyse before a PMD run. This API allows opening
 * zip files and makes sure they will be closed at the end of a run.
 *
 * @author Clément Fournier
 */
public final class FileCollector implements AutoCloseable {

    private static final Logger DEFAULT_LOG = Logger.getLogger(FileCollector.class.getName());
    private final List<FileWithLanguage> allFilesToProcess = new ArrayList<>();
    private final List<Closeable> resourcesToClose = new ArrayList<>();
    private final LanguageDiscoverer discoverer;
    private final PmdLogger log;

    private FileCollector(LanguageDiscoverer discoverer, PmdLogger logger) {
        this.discoverer = discoverer;
        this.log = logger;
    }

    public PmdLogger getLog() {
        return log;
    }

    public static FileCollector newCollector() {
        return newCollector(new LanguageDiscoverer(null), new SimplePmdLogger(DEFAULT_LOG));
    }

    public static FileCollector newCollector(LanguageDiscoverer discoverer, PmdLogger logger) {
        return new FileCollector(discoverer, logger);
    }

    List<FileWithLanguage> getAllFilesToProcess() {
        Collections.sort(allFilesToProcess);
        return Collections.unmodifiableList(allFilesToProcess);
    }

    /**
     * Add a file, language is determined automatically from
     * the extension/file patterns.
     *
     * @param file File to add
     */
    public boolean addFile(Path file) {
        if (!Files.isRegularFile(file)) {
            log.error("Not a regular file {}", file);
            return false;
        }
        Language language = discoverLanguage(file);
        if (language != null) {
            allFilesToProcess.add(new FileWithLanguage(file, language));
            return true;
        }
        return false;
    }

    /**
     * Add a file with the given language (which overrides the file patterns).
     */
    public boolean addFile(Path file, Language language) {
        AssertionUtil.requireParamNotNull("language", language);
        if (!Files.isRegularFile(file)) {
            log.error("Not a regular file {}", file);
            return false;
        }
        allFilesToProcess.add(new FileWithLanguage(file, language));
        return true;
    }


    /**
     * Add a directory recursively using {@link #addFile(Path)} on
     * all regular files.
     *
     * @param dir Directory path
     */
    public boolean addDirectory(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) {
            log.error("Not a directory {}", dir);
            return false;
        }
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile()) {
                    FileCollector.this.addFile(file);
                }
                return super.visitFile(file, attrs);
            }
        });
        return true;
    }


    // Add a file or directory recursively. Language is determined automatically
    // from the extension/file patterns.
    public boolean addFileOrDirectory(Path file) throws IOException {
        if (Files.isDirectory(file)) {
            return addDirectory(file);
        } else if (Files.isRegularFile(file)) {
            return addFile(file);
        } else {
            log.error("Not a file or directory {}", file);
            return false;
        }
    }

    /**
     * Opens a zip file and returns a FileSystem for its contents, so
     * it can be explored with the {@link Path} API. You can then call
     * {@link #addFile(Path)} and such. The zip file is registered as
     * a resource to close at the end of analysis.
     */
    public FileSystem addZipFile(Path zipFile) {
        if (!Files.isRegularFile(zipFile)) {
            throw new IllegalArgumentException("Not a regular file: " + zipFile);
        }
        URI zipUri = URI.create("zip:" + zipFile.toUri());
        try {
            FileSystem fs = FileSystems.getFileSystem(zipUri);
            resourcesToClose.add(fs);
            return fs;
        } catch (FileSystemNotFoundException | ProviderNotFoundException e) {
            log.error("Cannot open zip file " + zipFile, e);
            return null;
        }
    }

    /**
     * Close registered resources like zip files.
     */
    @Override
    public void close() throws IOException {
        IOException exception = IOUtil.closeAll(resourcesToClose);
        if (exception != null) {
            throw exception;
        }
    }

    private Language discoverLanguage(Path file) {
        List<Language> languages = discoverer.getLanguagesForFile(file);
        Language lang = languages.isEmpty() ? null : languages.get(0);

        if (languages.isEmpty()) {
            log.trace("File {0} matches no known language, ignoring", file);
        } else if (languages.size() > 1) {
            log.trace("File {0} matches multiple languages ({1}), selecting {2}", file, languages, lang);
        }
        return lang;
    }


    /**
     * Note: we store language and not language version so that every
     * file of the same language gets the same version language version.
     * The version is attributed later.
     */
    static final class FileWithLanguage implements Comparable<FileWithLanguage> {

        final Path path;
        final Language language;

        FileWithLanguage(Path path, Language language) {
            this.path = Objects.requireNonNull(path);
            this.language = Objects.requireNonNull(language);
        }

        public Path getPath() {
            return path;
        }

        public Language getLanguage() {
            return language;
        }

        @Override
        public int compareTo(FileWithLanguage o) {
            return this.path.compareTo(o.path);
        }
    }
}
