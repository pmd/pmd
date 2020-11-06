/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FilenameFilter;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sourceforge.pmd.AbstractConfiguration;
import net.sourceforge.pmd.cpd.renderer.CPDRenderer;
import net.sourceforge.pmd.util.FileFinder;
import net.sourceforge.pmd.util.FileUtil;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

/**
 *
 * @author Brian Remedios
 * @author Romain Pelisse - &lt;belaran@gmail.com&gt;
 */
public class CPDConfiguration extends AbstractConfiguration {

    public static final String DEFAULT_LANGUAGE = "java";
    public static final String DEFAULT_RENDERER = "text";

    private static final Map<String, Class<? extends CPDRenderer>> RENDERERS = new HashMap<>();

    static {
        RENDERERS.put(DEFAULT_RENDERER, SimpleRenderer.class);
        RENDERERS.put("xml", XMLRenderer.class);
        RENDERERS.put("csv", CSVRenderer.class);
        RENDERERS.put("csv_with_linecount_per_file", CSVWithLinecountPerFileRenderer.class);
        RENDERERS.put("vs", VSRenderer.class);
    }

    @Parameter(names = "--language", description = "Sources code language. Default value is " + DEFAULT_LANGUAGE,
            required = false, converter = LanguageConverter.class)
    private Language language;

    @Parameter(names = "--minimum-tokens",
            description = "The minimum token length which should be reported as a duplicate.", required = true)
    private int minimumTileSize;

    @Parameter(names = "--skip-duplicate-files",
            description = "Ignore multiple copies of files of the same name and length in comparison", required = false)
    private boolean skipDuplicates;

    @Parameter(names = "--format", description = "Report format. Default value is " + DEFAULT_RENDERER,
            required = false)
    private String rendererName;

    /**
     * The actual renderer. constructed by using the {@link #rendererName}. This
     * property is only valid after {@link #postContruct()} has been called!
     */
    @Deprecated
    private Renderer renderer;

    private CPDRenderer cpdRenderer;

    private String encoding;

    @Parameter(names = "--ignore-literals",
            description = "Ignore number values and string contents when comparing text", required = false)
    private boolean ignoreLiterals;

    @Parameter(names = "--ignore-identifiers", description = "Ignore constant and variable names when comparing text",
            required = false)
    private boolean ignoreIdentifiers;

    @Parameter(names = "--ignore-annotations", description = "Ignore language annotations when comparing text",
            required = false)
    private boolean ignoreAnnotations;

    @Parameter(names = "--ignore-usings", description = "Ignore using directives in C#", required = false)
    private boolean ignoreUsings;

    @Parameter(names = "--skip-lexical-errors",
            description = "Skip files which can't be tokenized due to invalid characters instead of aborting CPD",
            required = false)
    private boolean skipLexicalErrors = false;

    @Parameter(names = "--no-skip-blocks",
            description = "Do not skip code blocks marked with --skip-blocks-pattern (e.g. #if 0 until #endif)",
            required = false)
    private boolean noSkipBlocks = false;

    @Parameter(names = "--skip-blocks-pattern",
            description = "Pattern to find the blocks to skip. Start and End pattern separated by |. " + "Default is \""
                    + Tokenizer.DEFAULT_SKIP_BLOCKS_PATTERN + "\".",
            required = false)
    private String skipBlocksPattern = Tokenizer.DEFAULT_SKIP_BLOCKS_PATTERN;

    @Parameter(names = "--files", variableArity = true, description = "List of files and directories to process",
            required = false, converter = FileConverter.class)
    private List<File> files;

    @Parameter(names = "--filelist", description = "Path to a file containing a list of files to analyze.",
            required = false)
    private String fileListPath;

    @Parameter(names = "--exclude", variableArity = true, description = "Files to be excluded from CPD check",
            required = false, converter = FileConverter.class)
    private List<File> excludes;

    @Parameter(names = "--non-recursive", description = "Don't scan subdirectiories", required = false)
    private boolean nonRecursive;

    @Parameter(names = "--uri", description = "URI to process", required = false)
    private String uri;

    @Parameter(names = { "--help", "-h" }, description = "Print help text", required = false, help = true)
    private boolean help;

    @Parameter(names = { "--failOnViolation", "-failOnViolation" }, arity = 1,
            description = "By default CPD exits with status 4 if code duplications are found. Disable this option with '-failOnViolation false' to exit with 0 instead and just write the report.")
    private boolean failOnViolation = true;

    // this has to be a public static class, so that JCommander can use it!
    public static class LanguageConverter implements IStringConverter<Language> {

        @Override
        public Language convert(String languageString) {
            if (languageString == null || "".equals(languageString)) {
                languageString = DEFAULT_LANGUAGE;
            }
            return LanguageFactory.createLanguage(languageString);
        }
    }

    @Parameter(names = "--encoding", description = "Character encoding to use when processing files", required = false)
    public void setEncoding(String encoding) {
        this.encoding = encoding;
        setSourceEncoding(encoding);
    }

    public SourceCode sourceCodeFor(File file) {
        return new SourceCode(new SourceCode.FileCodeLoader(file, getSourceEncoding().name()));
    }

    public SourceCode sourceCodeFor(Reader reader, String sourceCodeName) {
        return new SourceCode(new SourceCode.ReaderCodeLoader(reader, sourceCodeName));
    }

    public void postContruct() {
        if (getLanguage() == null) {
            setLanguage(CPDConfiguration.getLanguageFromString(DEFAULT_LANGUAGE));
        }
        if (getRendererName() == null) {
            setRendererName(DEFAULT_RENDERER);
        }
        if (getRenderer() == null && getCPDRenderer() == null) {
            try {
                setCPDRenderer(getCPDRendererFromString(getRendererName(), getEncoding()));
            } catch (ClassCastException e) {
                // The renderer class configured is not using the new CPDRenderer interface...
                setRenderer(getRendererFromString(getRendererName(), getEncoding()));
            }
        }
    }

    /**
     * @deprecated Use {@link #getCPDRendererFromString(String, String)} instead
     */
    @Deprecated
    public static Renderer getRendererFromString(String name, String encoding) {
        String clazzname = name;
        if (clazzname == null || "".equals(clazzname)) {
            clazzname = DEFAULT_RENDERER;
        }
        @SuppressWarnings("unchecked") // Safe, all standard implementations implement both interfaces
        Class<? extends Renderer> clazz = (Class<? extends Renderer>) RENDERERS.get(clazzname.toLowerCase(Locale.ROOT));
        if (clazz == null) {
            try {
                clazz = Class.forName(clazzname).asSubclass(Renderer.class);
            } catch (ClassNotFoundException e) {
                System.err.println("Can't find class '" + name + "', defaulting to SimpleRenderer.");
                clazz = SimpleRenderer.class;
            }
        }
        try {
            Renderer renderer = clazz.getDeclaredConstructor().newInstance();
            setRendererEncoding(renderer, encoding);
            return renderer;
        } catch (Exception e) {
            System.err.println("Couldn't instantiate renderer, defaulting to SimpleRenderer: " + e);
            return new SimpleRenderer();
        }
    }

    public static CPDRenderer getCPDRendererFromString(String name, String encoding) {
        String clazzname = name;
        if (clazzname == null || "".equals(clazzname)) {
            clazzname = DEFAULT_RENDERER;
        }
        Class<? extends CPDRenderer> clazz = RENDERERS.get(clazzname.toLowerCase(Locale.ROOT));
        if (clazz == null) {
            try {
                clazz = Class.forName(clazzname).asSubclass(CPDRenderer.class);
            } catch (ClassNotFoundException e) {
                System.err.println("Can't find class '" + name + "', defaulting to SimpleRenderer.");
                clazz = SimpleRenderer.class;
            }
        }
        try {
            CPDRenderer renderer = clazz.getDeclaredConstructor().newInstance();
            setRendererEncoding(renderer, encoding);
            return renderer;
        } catch (Exception e) {
            System.err.println("Couldn't instantiate renderer, defaulting to SimpleRenderer: " + e);
            return new SimpleRenderer();
        }
    }

    private static void setRendererEncoding(Object renderer, String encoding)
            throws IllegalAccessException, InvocationTargetException {
        try {
            PropertyDescriptor encodingProperty = new PropertyDescriptor("encoding", renderer.getClass());
            Method method = encodingProperty.getWriteMethod();
            if (method != null) {
                method.invoke(renderer, encoding);
            }
        } catch (IntrospectionException ignored) {
            // ignored - maybe this renderer doesn't have a encoding property
        }
    }

    public static String[] getRenderers() {
        String[] result = RENDERERS.keySet().toArray(new String[RENDERERS.size()]);
        Arrays.sort(result);
        return result;
    }

    public static Language getLanguageFromString(String languageString) {
        return LanguageFactory.createLanguage(languageString);
    }

    public static void setSystemProperties(CPDConfiguration configuration) {
        Properties properties = new Properties();
        if (configuration.isIgnoreLiterals()) {
            properties.setProperty(Tokenizer.IGNORE_LITERALS, "true");
        } else {
            properties.remove(Tokenizer.IGNORE_LITERALS);
        }
        if (configuration.isIgnoreIdentifiers()) {
            properties.setProperty(Tokenizer.IGNORE_IDENTIFIERS, "true");
        } else {
            properties.remove(Tokenizer.IGNORE_IDENTIFIERS);
        }
        if (configuration.isIgnoreAnnotations()) {
            properties.setProperty(Tokenizer.IGNORE_ANNOTATIONS, "true");
        } else {
            properties.remove(Tokenizer.IGNORE_ANNOTATIONS);
        }
        if (configuration.isIgnoreUsings()) {
            properties.setProperty(Tokenizer.IGNORE_USINGS, "true");
        } else {
            properties.remove(Tokenizer.IGNORE_USINGS);
        }
        properties.setProperty(Tokenizer.OPTION_SKIP_BLOCKS, Boolean.toString(!configuration.isNoSkipBlocks()));
        properties.setProperty(Tokenizer.OPTION_SKIP_BLOCKS_PATTERN, configuration.getSkipBlocksPattern());
        configuration.getLanguage().setProperties(properties);
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public int getMinimumTileSize() {
        return minimumTileSize;
    }

    public void setMinimumTileSize(int minimumTileSize) {
        this.minimumTileSize = minimumTileSize;
    }

    public boolean isSkipDuplicates() {
        return skipDuplicates;
    }

    public void setSkipDuplicates(boolean skipDuplicates) {
        this.skipDuplicates = skipDuplicates;
    }

    public String getRendererName() {
        return rendererName;
    }

    public void setRendererName(String rendererName) {
        this.rendererName = rendererName;
    }

    /**
     * @deprecated Use {@link #getCPDRenderer()} instead
     */
    @Deprecated
    public Renderer getRenderer() {
        return renderer;
    }

    public CPDRenderer getCPDRenderer() {
        return cpdRenderer;
    }

    public Tokenizer tokenizer() {
        if (language == null) {
            throw new IllegalStateException("Language is null.");
        }
        return language.getTokenizer();
    }

    public FilenameFilter filenameFilter() {
        if (language == null) {
            throw new IllegalStateException("Language is null.");
        }

        final FilenameFilter languageFilter = language.getFileFilter();
        final Set<String> exclusions = new HashSet<>();

        if (excludes != null) {
            FileFinder finder = new FileFinder();
            for (File excludedFile : excludes) {
                if (excludedFile.isDirectory()) {
                    List<File> files = finder.findFilesFrom(excludedFile, languageFilter, true);
                    for (File f : files) {
                        exclusions.add(FileUtil.normalizeFilename(f.getAbsolutePath()));
                    }
                } else {
                    exclusions.add(FileUtil.normalizeFilename(excludedFile.getAbsolutePath()));
                }
            }
        }

        return new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                File f = new File(dir, name);
                if (exclusions.contains(FileUtil.normalizeFilename(f.getAbsolutePath()))) {
                    System.err.println("Excluding " + f.getAbsolutePath());
                    return false;
                }
                return languageFilter.accept(dir, name);
            }
        };
    }

    /**
     * @deprecated Use {@link #setCPDRenderer(CPDRenderer)} instead
     * @param renderer
     */
    @Deprecated
    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
        this.cpdRenderer = null;
    }

    public void setCPDRenderer(CPDRenderer renderer) {
        this.cpdRenderer = renderer;
        this.renderer = null;
    }

    public boolean isIgnoreLiterals() {
        return ignoreLiterals;
    }

    public void setIgnoreLiterals(boolean ignoreLiterals) {
        this.ignoreLiterals = ignoreLiterals;
    }

    public boolean isIgnoreIdentifiers() {
        return ignoreIdentifiers;
    }

    public void setIgnoreIdentifiers(boolean ignoreIdentifiers) {
        this.ignoreIdentifiers = ignoreIdentifiers;
    }

    public boolean isIgnoreAnnotations() {
        return ignoreAnnotations;
    }

    public void setIgnoreAnnotations(boolean ignoreAnnotations) {
        this.ignoreAnnotations = ignoreAnnotations;
    }

    public boolean isIgnoreUsings() {
        return ignoreUsings;
    }

    public void setIgnoreUsings(boolean ignoreUsings) {
        this.ignoreUsings = ignoreUsings;
    }

    public boolean isSkipLexicalErrors() {
        return skipLexicalErrors;
    }

    public void setSkipLexicalErrors(boolean skipLexicalErrors) {
        this.skipLexicalErrors = skipLexicalErrors;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public String getFileListPath() {
        return fileListPath;
    }

    public void setFileListPath(String fileListPath) {
        this.fileListPath = fileListPath;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public List<File> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<File> excludes) {
        this.excludes = excludes;
    }

    public boolean isNonRecursive() {
        return nonRecursive;
    }

    public void setNonRecursive(boolean nonRecursive) {
        this.nonRecursive = nonRecursive;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public String getEncoding() {
        return encoding;
    }

    public boolean isNoSkipBlocks() {
        return noSkipBlocks;
    }

    public void setNoSkipBlocks(boolean noSkipBlocks) {
        this.noSkipBlocks = noSkipBlocks;
    }

    public String getSkipBlocksPattern() {
        return skipBlocksPattern;
    }

    public void setSkipBlocksPattern(String skipBlocksPattern) {
        this.skipBlocksPattern = skipBlocksPattern;
    }

    public boolean isFailOnViolation() {
        return failOnViolation;
    }

    public void setFailOnViolation(boolean failOnViolation) {
        this.failOnViolation = failOnViolation;
    }
}
