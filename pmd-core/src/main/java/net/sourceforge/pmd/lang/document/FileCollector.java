/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.ProviderNotFoundException;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.log.PmdReporter;

/**
 * Collects files to analyse before a PMD run. This API allows opening
 * zip files and makes sure they will be closed at the end of a run.
 *
 * @author Clément Fournier
 */
@SuppressWarnings("PMD.CloseResource")
public final class FileCollector implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(FileCollector.class);

    private final Set<TextFile> allFilesToProcess = new LinkedHashSet<>();
    private final List<Closeable> resourcesToClose = new ArrayList<>();
    private Charset charset = StandardCharsets.UTF_8;
    private final LanguageVersionDiscoverer discoverer;
    private final PmdReporter reporter;
    private final FileId outerFsPath;
    private boolean closed;
    private boolean recursive = true;
    private Predicate<FileId> fileFilter = file -> true;

    // construction

    private FileCollector(LanguageVersionDiscoverer discoverer, PmdReporter reporter, FileId outerFsPath) {
        this.discoverer = discoverer;
        this.reporter = reporter;
        this.outerFsPath = outerFsPath;
        LOG.debug("Created new FileCollector with {}", discoverer);
    }

    /**
     * @apiNote Internal API - please use {@link PmdAnalysis#files()} instead of
     * creating a collector yourself.
     */
    static FileCollector newCollector(LanguageVersionDiscoverer discoverer, PmdReporter reporter) {
        return new FileCollector(discoverer, reporter, null);
    }

    /**
     * Returns a new collector using the same configuration except for the logger.
     *
     * @apiNote Internal API - please use {@link PmdAnalysis#files()} instead of
     * creating a collector yourself.
     */
    FileCollector newCollector(PmdReporter logger) {
        FileCollector fileCollector = new FileCollector(discoverer, logger, null);
        fileCollector.charset = this.charset;
        return fileCollector;
    }

    // public behaviour

    /**
     * Returns an unmodifiable list of all files that have been collected.
     *
     * @throws IllegalStateException if the collector was already closed
     */
    public List<TextFile> getCollectedFiles() {
        if (closed) {
            throw new IllegalStateException("Collector was closed!");
        }
        List<TextFile> allFilesToProcess = new ArrayList<>(this.allFilesToProcess);
        allFilesToProcess.sort(Comparator.comparing(TextFile::getFileId));
        return Collections.unmodifiableList(allFilesToProcess);
    }


    /**
     * Returns the reporter for the file collection phase.
     */
    public PmdReporter getReporter() {
        return reporter;
    }

    /**
     * Close registered resources like zip files.
     */
    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        Exception exception = IOUtil.closeAll(resourcesToClose);
        if (exception != null) {
            reporter.errorEx("Error while closing resources", exception);
        }
    }

    // collection

    /**
     * Add a file, language is determined automatically from
     * the extension/file patterns. The encoding is the current
     * encoding ({@link #setCharset(Charset)}).
     *
     * @param file File to add
     *
     * @return True if the file has been added
     */
    public boolean addFile(Path file) {
        if (!Files.isRegularFile(file)) {
            reporter.error("Not a regular file: {0}", file);
            return false;
        }
        LanguageVersion languageVersion = discoverLanguage(file.toString());
        return languageVersion != null
            && addFileImpl(TextFile.builderForPath(file, charset, languageVersion)
                                   .setParentFsPath(outerFsPath)
                                   .build());
    }

    /**
     * Add a file with the given language (which overrides the file patterns).
     * The encoding is the current encoding ({@link #setCharset(Charset)}).
     *
     * @param file     Path to a file
     * @param language A language. The language version will be taken to be the
     *                 contextual default version.
     *
     * @return True if the file has been added
     */
    public boolean addFile(Path file, Language language) {
        AssertionUtil.requireParamNotNull("language", language);
        if (!Files.isRegularFile(file)) {
            reporter.error("Not a regular file: {0}", file);
            return false;
        }
        LanguageVersion lv = discoverer.getDefaultLanguageVersion(language);
        Objects.requireNonNull(lv);
        return addFileImpl(TextFile.builderForPath(file, charset, lv)
                                   .setParentFsPath(outerFsPath)
                                   .build());
    }

    /**
     * Add a pre-configured text file. The language version will be checked
     * to match the contextual default for the language (the file cannot be added
     * if it has a different version).
     *
     * @return True if the file has been added
     */
    public boolean addFile(TextFile textFile) {
        AssertionUtil.requireParamNotNull("textFile", textFile);
        return checkContextualVersion(textFile) && addFileImpl(textFile);
    }

    /**
     * Add a text file given its contents and a name. The language version
     * will be determined from the name as usual.
     *
     * @return True if the file has been added
     */
    public boolean addSourceFile(FileId fileId, String sourceContents) {
        AssertionUtil.requireParamNotNull("sourceContents", sourceContents);
        AssertionUtil.requireParamNotNull("pathId", fileId);

        LanguageVersion version = discoverLanguage(fileId.getFileName());
        return version != null
            && addFileImpl(TextFile.builderForCharSeq(sourceContents, fileId, version)
                                   .setParentFsPath(outerFsPath)
                                   .build());
    }

    private boolean addFileImpl(TextFile textFile) {
        LOG.trace("Adding file {} (lang: {}) ", textFile.getFileId().getAbsolutePath(), textFile.getLanguageVersion().getTerseName());

        if (!fileFilter.test(textFile.getFileId())) {
            LOG.trace("File was skipped due to fileFilter...");
            return false;
        }

        if (allFilesToProcess.add(textFile)) {
            return true;
        }
        LOG.trace("File was already collected, skipping");
        return false;
    }

    private LanguageVersion discoverLanguage(String file) {
        if (discoverer.getForcedVersion() != null) {
            return discoverer.getForcedVersion();
        }
        List<Language> languages = discoverer.getLanguagesForFile(file);

        if (languages.isEmpty()) {
            LOG.trace("File {} matches no known language, ignoring", file);
            return null;
        }
        Language lang = languages.get(0);
        if (languages.size() > 1) {
            LOG.trace("File {} matches multiple languages ({}), selecting {}", file, languages, lang);
        }
        return discoverer.getDefaultLanguageVersion(lang);
    }

    /**
     * Whether the LanguageVersion of the file matches the one set in
     * the {@link LanguageVersionDiscoverer}. This is required to ensure
     * that all files for a given language have the same language version.
     */
    private boolean checkContextualVersion(TextFile textFile) {
        LanguageVersion fileVersion = textFile.getLanguageVersion();
        Language language = fileVersion.getLanguage();
        LanguageVersion contextVersion = discoverer.getDefaultLanguageVersion(language);
        if (!fileVersion.equals(contextVersion)) {
            reporter.error(
                "Cannot add file {0}: version ''{1}'' does not match ''{2}''",
                textFile.getFileId(),
                fileVersion,
                contextVersion
            );
            return false;
        }
        return true;
    }


    /**
     * Add a directory recursively using {@link #addFile(Path)} on
     * all regular files.
     *
     * @param dir Directory path
     *
     * @return True if the directory has been added
     */
    public boolean addDirectory(Path dir) throws IOException {
        return addDirectory(dir, recursive);
    }

    public boolean addDirectory(Path dir, boolean recurse) throws IOException {
        if (!Files.isDirectory(dir)) {
            reporter.error("Not a directory {0}", dir);
            return false;
        }
        int maxDepth = recurse ? Integer.MAX_VALUE : 1;
        Files.walkFileTree(dir, EnumSet.of(FileVisitOption.FOLLOW_LINKS), maxDepth, new SimpleFileVisitor<Path>() {
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


    /**
     * Add a file or directory recursively. Language is determined automatically
     * from the extension/file patterns.
     *
     * @return True if the file or directory has been added
     */
    public boolean addFileOrDirectory(Path file) throws IOException {
        return addFileOrDirectory(file, true);
    }

    /**
     * Add a file or directory recursively. Language is determined automatically
     * from the extension/file patterns.
     *
     * @return True if the file or directory has been added
     */
    public boolean addFileOrDirectory(Path file, boolean recurseIfDirectory) throws IOException {
        if (Files.isDirectory(file)) {
            return addDirectory(file, recurseIfDirectory);
        } else if (Files.isRegularFile(file)) {
            return addFile(file);
        } else {
            reporter.error("Not a file or directory {0}", file);
            return false;
        }
    }

    /**
     * Opens a zip file and adds all files of the zip file to the list
     * of files to be processed.
     *
     * <p>The zip file is registered as a resource to close at the end of analysis.</p>
     *
     * @return True if the zip file including its content has been added without errors
     */
    public boolean addZipFileWithContent(Path zipFile) throws IOException {
        if (!Files.isRegularFile(zipFile)) {
            throw new IllegalArgumentException("Not a regular file: " + zipFile);
        }
        URI zipUri = URI.create("jar:" + zipFile.toUri());
        FileSystem fs;
        boolean isNewFileSystem = false;
        try {
            // find an existing file system, may fail
            fs = FileSystems.getFileSystem(zipUri);
        } catch (FileSystemNotFoundException ignored) {
            // if it fails, try to create it.
            try {
                fs = FileSystems.newFileSystem(zipUri, Collections.<String, Object>emptyMap());
                isNewFileSystem = true;
            } catch (ProviderNotFoundException | IOException e) {
                reporter.errorEx("Cannot open zip file " + zipFile, e);
                return false;
            }
        }
        try (FileCollector zipCollector = newZipCollector(zipFile)) {
            for (Path zipRoot : fs.getRootDirectories()) {
                zipCollector.addFileOrDirectory(zipRoot);
            }
            this.absorb(zipCollector);
            if (isNewFileSystem) {
                resourcesToClose.add(fs);
            }

        } catch (IOException ioe) {
            reporter.errorEx("Error reading zip file " + zipFile + ", will be skipped", ioe);
            fs.close();
            return false;
        }
        return true;
    }


    /** A collector that prefixes the display name of the files it will contain with the path of the zip. */
    private FileCollector newZipCollector(Path zipFilePath) {
        return new FileCollector(discoverer, reporter, FileId.fromPath(zipFilePath));
    }

    // configuration

    public void setRecursive(boolean collectFilesRecursively) {
        this.recursive = collectFilesRecursively;
    }

    /**
     * Sets the charset to use for subsequent calls to {@link #addFile(Path)}
     * and other overloads using a {@link Path}.
     *
     * @param charset A charset
     */
    public void setCharset(Charset charset) {
        this.charset = Objects.requireNonNull(charset);
    }

    /**
     * Sets an additional filter that is being called before adding the
     * file to the list.
     *
     * @param fileFilter the filter should return {@code true} if the file
     *                      should be collected and analyzed.
     * @throws NullPointerException if {@code fileFilter} is {@code null}.
     */
    public void setFileFilter(Predicate<FileId> fileFilter) {
        this.fileFilter = Objects.requireNonNull(fileFilter);
    }

    // filtering

    /**
     * Remove all files collected by the given collector from this one.
     */
    public void exclude(FileCollector excludeCollector) {
        Set<TextFile> toExclude = new HashSet<>(excludeCollector.allFilesToProcess);
        for (Iterator<TextFile> iterator = allFilesToProcess.iterator(); iterator.hasNext();) {
            TextFile file = iterator.next();
            if (toExclude.contains(file)) {
                LOG.trace("Excluding file {}", file.getFileId());
                iterator.remove();
            }
        }
    }

    /**
     * Add all files collected in the other collector into this one.
     * Transfers resources to close as well. The parameter is left empty.
     */
    public void absorb(FileCollector otherCollector) {
        this.allFilesToProcess.addAll(otherCollector.allFilesToProcess);
        this.resourcesToClose.addAll(otherCollector.resourcesToClose);
        otherCollector.allFilesToProcess.clear();
        otherCollector.resourcesToClose.clear();
    }

    /**
     * Exclude all collected files whose language is not part of the given
     * collection.
     */
    public void filterLanguages(Set<Language> languages) {
        for (Iterator<TextFile> iterator = allFilesToProcess.iterator(); iterator.hasNext();) {
            TextFile file = iterator.next();
            Language lang = file.getLanguageVersion().getLanguage();
            if (!languages.contains(lang)) {
                LOG.trace("Filtering out {}, no rules for language {}", file.getFileId(), lang);
                iterator.remove();
            }
        }
    }


    @Override
    public String toString() {
        return "FileCollector{filesToProcess=" + allFilesToProcess + '}';
    }
}
