/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.util.IOUtil;

/**
 * @author Clément Fournier
 */
public final class FileCollector implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(FileCollector.class.getName());
    private final List<FileWithLanguage> allFilesToProcess = new ArrayList<>();
    private final List<Closeable> resourcesToClose = new ArrayList<>();
    private final LanguageVersionDiscoverer discoverer;

    public FileCollector(LanguageVersionDiscoverer discoverer) {
        this.discoverer = discoverer;
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
            throw new IllegalArgumentException("Not a regular file: " + file);
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
            throw new IllegalArgumentException("Not a regular file: " + file);
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
    public void addDirectory(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Not a directory: " + dir);
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
    }


    // Add a file or directory recursively. Language is determined automatically
    // from the extension/file patterns.
    public void addFileOrDirectory(Path file) throws IOException {
        if (Files.isDirectory(file)) {
            addDirectory(file);
        } else if (Files.isRegularFile(file)) {
            addFile(file);
        } else {
            throw new IllegalArgumentException("Not a file or directory " + file);
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
        FileSystem fs = FileSystems.getFileSystem(zipUri);
        resourcesToClose.add(fs);
        return fs;
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
        List<Language> languages = discoverer.getLanguagesForFile(file.toFile());
        Language lang = languages.isEmpty() ? null : languages.get(0);
        if (LOG.isLoggable(Level.FINE)) {
            if (languages.isEmpty()) {
                LOG.fine(MessageFormat.format("File {0} matches no known language, ignoring", file));
            } else if (languages.size() > 1) {
                LOG.fine(MessageFormat.format(
                    "File {0} matches multiple languages ({1}), selecting {2}",
                    file,
                    languages,
                    lang
                ));
            }
        }
        return lang;
    }


    // todo(textdocuments): replace with TextFile
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
