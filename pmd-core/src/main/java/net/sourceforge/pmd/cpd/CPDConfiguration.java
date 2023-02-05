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
import net.sourceforge.pmd.cpd.renderer.CPDReportRenderer;
import net.sourceforge.pmd.internal.util.FileFinder;
import net.sourceforge.pmd.internal.util.FileUtil;

/**
 *
 * @author Brian Remedios
 * @author Romain Pelisse - &lt;belaran@gmail.com&gt;
 */
public class CPDConfiguration extends AbstractConfiguration {

    public static final String DEFAULT_LANGUAGE = "java";
    public static final String DEFAULT_RENDERER = "text";

    private static final Map<String, Class<? extends CPDReportRenderer>> RENDERERS = new HashMap<>();

    static {
        RENDERERS.put(DEFAULT_RENDERER, SimpleRenderer.class);
        RENDERERS.put("xml", XMLRenderer.class);
        RENDERERS.put("csv", CSVRenderer.class);
        RENDERERS.put("csv_with_linecount_per_file", CSVWithLinecountPerFileRenderer.class);
        RENDERERS.put("vs", VSRenderer.class);
    }

    private Language language;

    private int minimumTileSize;

    private boolean skipDuplicates;

    private String rendererName;

    private CPDReportRenderer cpdReportRenderer;

    private boolean ignoreLiterals;

    private boolean ignoreIdentifiers;

    private boolean ignoreAnnotations;

    private boolean ignoreUsings;

    private boolean ignoreLiteralSequences = false;

    private boolean skipLexicalErrors = false;

    private boolean noSkipBlocks = false;

    private String skipBlocksPattern = Tokenizer.DEFAULT_SKIP_BLOCKS_PATTERN;

    private List<File> files;

    private String fileListPath;

    private List<File> excludes;

    private boolean nonRecursive;

    private String uri;

    private boolean help;

    private boolean failOnViolation = true;

    private boolean debug = false;

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
        if (this.cpdReportRenderer == null) {
            //may throw
            CPDReportRenderer renderer = createRendererByName(getRendererName(), getSourceEncoding().name());
            setRenderer(renderer);
        }
    }

    static CPDReportRenderer createRendererByName(String name, String encoding) {
        if (name == null || "".equals(name)) {
            name = DEFAULT_RENDERER;
        }
        Class<? extends CPDReportRenderer> rendererClass = RENDERERS.get(name.toLowerCase(Locale.ROOT));
        if (rendererClass == null) {
            Class<?> klass;
            try {
                klass = Class.forName(name);
                if (CPDReportRenderer.class.isAssignableFrom(klass)) {
                    rendererClass = (Class) klass;
                } else {
                    throw new IllegalArgumentException("Class " + name + " does not implement " + CPDReportRenderer.class);
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Cannot find class " + name);
            }
        }

        CPDReportRenderer renderer = null;
        try {
            renderer = rendererClass.getDeclaredConstructor().newInstance();
            setRendererEncoding(renderer, encoding);
        } catch (Exception e) {
            System.err.println("Couldn't instantiate renderer, defaulting to SimpleRenderer: " + e);
            renderer = new SimpleRenderer();
        }
        return renderer;
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

        return (dir, name) -> {
            File f = new File(dir, name);
            if (exclusions.contains(FileUtil.normalizeFilename(f.getAbsolutePath()))) {
                System.err.println("Excluding " + f.getAbsolutePath());
                return false;
            }
            return languageFilter.accept(dir, name);
        };
    }


    void setRenderer(CPDReportRenderer renderer) {
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
