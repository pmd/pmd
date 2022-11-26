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
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.cpd.renderer.CPDRenderer;
import net.sourceforge.pmd.cpd.renderer.CPDRendererAdapter;
import net.sourceforge.pmd.cpd.renderer.CPDReportRenderer;
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

    private static final Map<String, Class<?>> RENDERERS = new HashMap<>();

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

    @Deprecated
    private CPDRenderer cpdRenderer;

    private CPDReportRenderer cpdReportRenderer;

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

    @Parameter(names = "--ignore-literal-sequences", description = "Ignore sequences of literals", required = false)
    private boolean ignoreLiteralSequences = false;

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

    @Parameter(names = { "--files", "-d", "--dir" }, variableArity = true, description = "List of files and directories to process",
            required = false, converter = FileConverter.class)
    private List<File> files;

    @Parameter(names = { "--filelist", "--file-list" }, description = "Path to a file containing a list of files to analyze.",
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

    @Parameter(names = { "--fail-on-violation", "--failOnViolation", "-failOnViolation" }, arity = 1,
            description = "By default CPD exits with status 4 if code duplications are found. Disable this option with '-failOnViolation false' to exit with 0 instead and just write the report.")
    private boolean failOnViolation = true;

    @Parameter(names = { "--debug", "--verbose", "-v", "-D" }, description = "Debug mode.")
    private boolean debug = false;

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

    @Parameter(names = { "--encoding", "-e" }, description = "Character encoding to use when processing files", required = false)
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
            Object renderer = createRendererByName(getRendererName(), getEncoding());
            String className = getRendererName();

            if (renderer instanceof CPDReportRenderer) {
                setRenderer((CPDReportRenderer) renderer);
            } else if (renderer instanceof CPDRenderer) {
                setCPDRenderer((CPDRenderer) renderer);
            } else if (renderer instanceof Renderer) {
                setRenderer((Renderer) renderer);
            } else {
                System.err.println("Class '" + className + "' is not a supported renderer, defaulting to SimpleRenderer.");
                setRenderer(new SimpleRenderer());
            }
        }
    }

    private static Object createRendererByName(String name, String encoding) {
        if (name == null || "".equals(name)) {
            name = DEFAULT_RENDERER;
        }
        Class<?> rendererClass = RENDERERS.get(name.toLowerCase(Locale.ROOT));
        if (rendererClass == null) {
            try {
                rendererClass = Class.forName(name);
            } catch (ClassNotFoundException e) {
                System.err.println("Can't find class '" + name + "', defaulting to SimpleRenderer.");
                rendererClass = SimpleRenderer.class;
            }
        }

        Object renderer = null;
        try {
            renderer = rendererClass.getDeclaredConstructor().newInstance();
            setRendererEncoding(renderer, encoding);
        } catch (Exception e) {
            System.err.println("Couldn't instantiate renderer, defaulting to SimpleRenderer: " + e);
            renderer = new SimpleRenderer();
        }
        return renderer;
    }

    /**
     * @deprecated Internal API
     */
    @Deprecated
    @InternalApi
    public static Renderer getRendererFromString(String name, String encoding) {
        // will throw a ClassCastException if the renderer is of wrong type
        return (Renderer) createRendererByName(name, encoding);
    }

    /**
     * @deprecated Internal API
     */
    @Deprecated
    @InternalApi
    public static CPDRenderer getCPDRendererFromString(String name, String encoding) {
        // will throw a ClassCastException if the renderer is of wrong type
        return (CPDRenderer) createRendererByName(name, encoding);
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
        String[] result = RENDERERS.keySet().toArray(new String[0]);
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
        if (configuration.isIgnoreLiteralSequences()) {
            properties.setProperty(Tokenizer.OPTION_IGNORE_LITERAL_SEQUENCES, "true");
        } else {
            properties.remove(Tokenizer.OPTION_IGNORE_LITERAL_SEQUENCES);
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
     * @deprecated Internal API.
     */
    @Deprecated
    @InternalApi
    public Renderer getRenderer() {
        return renderer;
    }

    /**
     * @deprecated Internal API.
     */
    @Deprecated
    @InternalApi
    public CPDRenderer getCPDRenderer() {
        return cpdRenderer;
    }

    public CPDReportRenderer getCPDReportRenderer() {
        return cpdReportRenderer;
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
     * @deprecated Internal API. Use {@link #setRendererName(String)} instead.
     * @param renderer
     */
    @Deprecated
    @InternalApi
    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
        this.cpdRenderer = null;
        this.cpdReportRenderer = null;
    }

    /**
     * @deprecated Internal API. Use {@link #setRendererName(String)} instead.
     * @param renderer
     */
    @Deprecated
    @InternalApi
    public void setCPDRenderer(CPDRenderer renderer) {
        this.renderer = null;
        this.cpdRenderer = renderer;
        this.cpdReportRenderer = new CPDRendererAdapter(renderer);
    }

    void setRenderer(CPDReportRenderer renderer) {
        this.renderer = null;
        this.cpdRenderer = null;
        this.cpdReportRenderer = renderer;
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

    public boolean isIgnoreLiteralSequences() {
        return ignoreLiteralSequences;
    }

    public void setIgnoreLiteralSequences(boolean ignoreLiteralSequences) {
        this.ignoreLiteralSequences = ignoreLiteralSequences;
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

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
