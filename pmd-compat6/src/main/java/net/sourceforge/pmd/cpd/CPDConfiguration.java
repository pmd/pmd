/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// This class has been taken from 7.0.0-SNAPSHOT
// Changes: setLanguage, setSourceEncoding, filenameFilter

package net.sourceforge.pmd.cpd;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.AbstractConfiguration;
import net.sourceforge.pmd.cpd.internal.CpdLanguagePropertiesDefaults;
import net.sourceforge.pmd.internal.util.FileFinder;
import net.sourceforge.pmd.internal.util.FileUtil;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptLanguageModule;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.jsp.JspLanguageModule;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

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


    private int minimumTileSize;

    private boolean skipDuplicates;

    private String rendererName = DEFAULT_RENDERER;

    private @Nullable CPDReportRenderer cpdReportRenderer;

    private boolean ignoreLiterals;

    private boolean ignoreIdentifiers;

    private boolean ignoreAnnotations;

    private boolean ignoreUsings;

    private boolean ignoreLiteralSequences = false;

    private boolean ignoreIdentifierAndLiteralSequences = false;

    private boolean skipLexicalErrors = false;

    private boolean noSkipBlocks = false;

    private String skipBlocksPattern = CpdLanguagePropertiesDefaults.DEFAULT_SKIP_BLOCKS_PATTERN;

    private boolean help;

    private boolean failOnViolation = true;


    public CPDConfiguration() {
        this(LanguageRegistry.CPD);
    }

    public CPDConfiguration(LanguageRegistry languageRegistry) {
        super(languageRegistry, new SimpleMessageReporter(LoggerFactory.getLogger(CpdAnalysis.class)));
    }

    @Override
    public void setSourceEncoding(Charset sourceEncoding) {
        super.setSourceEncoding(sourceEncoding);
        if (cpdReportRenderer != null) {
            setRendererEncoding(cpdReportRenderer, sourceEncoding);
        }
    }

    static CPDReportRenderer createRendererByName(String name, Charset encoding) {
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

        CPDReportRenderer renderer;
        try {
            renderer = rendererClass.getDeclaredConstructor().newInstance();
            setRendererEncoding(renderer, encoding);
        } catch (Exception e) {
            System.err.println("Couldn't instantiate renderer, defaulting to SimpleRenderer: " + e);
            renderer = new SimpleRenderer();
        }
        return renderer;
    }

    private static void setRendererEncoding(@NonNull Object renderer, Charset encoding) {
        try {
            PropertyDescriptor encodingProperty = new PropertyDescriptor("encoding", renderer.getClass());
            Method method = encodingProperty.getWriteMethod();
            if (method == null) {
                return;
            }
            if (method.getParameterTypes()[0] == Charset.class) {
                method.invoke(renderer, encoding);
            } else if (method.getParameterTypes()[0] == String.class) {
                method.invoke(renderer, encoding.name());
            }
        } catch (IntrospectionException | ReflectiveOperationException ignored) {
            // ignored - maybe this renderer doesn't have a encoding property
        }
    }

    public static Set<String> getRenderers() {
        return Collections.unmodifiableSet(RENDERERS.keySet());
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
        if (rendererName == null) {
            this.cpdReportRenderer = null;
        }
        this.cpdReportRenderer = createRendererByName(rendererName, getSourceEncoding());
    }


    public CPDReportRenderer getCPDReportRenderer() {
        return cpdReportRenderer;
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

    public boolean isIgnoreIdentifierAndLiteralSequences() {
        return ignoreIdentifierAndLiteralSequences;
    }

    public void setIgnoreIdentifierAndLiteralSequences(boolean ignoreIdentifierAndLiteralSequences) {
        this.ignoreIdentifierAndLiteralSequences = ignoreIdentifierAndLiteralSequences;
    }

    public boolean isSkipLexicalErrors() {
        return skipLexicalErrors;
    }

    public void setSkipLexicalErrors(boolean skipLexicalErrors) {
        this.skipLexicalErrors = skipLexicalErrors;
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

    // ------------------- compat extensions --------------------
    private FilenameFilter filenameFilter;

    public void setLanguage(Language language) {
        if (language instanceof JavaLanguage) {
            filenameFilter = language.getFileFilter();
            setForceLanguageVersion(JavaLanguageModule.getInstance().getDefaultVersion());
        } else if (language instanceof EcmascriptLanguage) {
            filenameFilter = language.getFileFilter();
            setForceLanguageVersion(EcmascriptLanguageModule.getInstance().getDefaultVersion());
        } else if (language instanceof JSPLanguage) {
            filenameFilter = language.getFileFilter();
            setForceLanguageVersion(JspLanguageModule.getInstance().getDefaultVersion());
        } else if (language instanceof LanguageFactory.CpdLanguageAdapter) {
            filenameFilter = language.getFileFilter();
            setForceLanguageVersion(((LanguageFactory.CpdLanguageAdapter) language).getLanguage().getDefaultVersion());
        } else {
            throw new UnsupportedOperationException("Language " + language.getName() + " is not supported");
        }
    }

    public void setSourceEncoding(String sourceEncoding) {
        setSourceEncoding(Charset.forName(Objects.requireNonNull(sourceEncoding)));
    }

    public FilenameFilter filenameFilter() {
        if (getForceLanguageVersion() == null) {
            throw new IllegalStateException("Language is null.");
        }

        final FilenameFilter languageFilter = filenameFilter;
        final Set<String> exclusions = new HashSet<>();

        if (getExcludes() != null) {
            FileFinder finder = new FileFinder();
            for (Path excludedFile : getExcludes()) {
                if (Files.isDirectory(excludedFile)) {
                    List<File> files = finder.findFilesFrom(excludedFile.toFile(), languageFilter, true);
                    for (File f : files) {
                        exclusions.add(FileUtil.normalizeFilename(f.getAbsolutePath()));
                    }
                } else {
                    exclusions.add(FileUtil.normalizeFilename(excludedFile.toAbsolutePath().toString()));
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
}
