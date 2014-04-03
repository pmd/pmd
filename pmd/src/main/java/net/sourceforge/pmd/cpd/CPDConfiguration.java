/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import net.sourceforge.pmd.AbstractConfiguration;
import net.sourceforge.pmd.util.FileFinder;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import java.io.Reader;

/**
 *
 * @author Brian Remedios
 * @author Romain Pelisse - <belaran@gmail.com>
 */
public class CPDConfiguration extends AbstractConfiguration {

	public final static String DEFAULT_LANGUAGE = "java";

	public final static String DEFAULT_RENDERER = "text";

	@Parameter(names = "--language", description = "Sources code language. Default value is "
			+ DEFAULT_LANGUAGE, required = false, converter = LanguageConverter.class)
	private Language language;

	@Parameter(names = "--minimum-tokens", description = "The minimum token length which should be reported as a duplicate.", required = true)
	private int minimumTileSize;

	@Parameter(names = "--skip-duplicate-files", description = "Ignore multiple copies of files of the same name and length in comparison", required = false)
	private boolean skipDuplicates;

	@Parameter(names = "--format", description = "Report format. Default value is "
		+ DEFAULT_RENDERER, required = false)
	private String rendererName;

	/**
	 * The actual renderer. constructed by using the {@link #rendererName}.
	 * This property is only valid after {@link #postContruct()} has been called!
	 */
	private Renderer renderer;

	@Parameter(names = "--encoding", description = "Characterset to use when processing files", required = false)
	private String encoding;

	@Parameter(names = "--ignore-literals", description = "Ignore number values and string contents when comparing text", required = false)
	private boolean ignoreLiterals;

	@Parameter(names = "--ignore-identifiers", description = "Ignore constant and variable names when comparing text", required = false)
	private boolean ignoreIdentifiers;

	@Parameter(names = "--ignore-annotations", description = "Ignore language annotations when comparing text", required = false)
	private boolean ignoreAnnotations;

	@Parameter(names = "--skip-lexical-errors", description = "Skip files which can't be tokenized due to invalid characters instead of aborting CPD", required = false)
	private boolean skipLexicalErrors = false;

	@Parameter(names = "--files", variableArity = true, description = "List of files and directories to process", required = false)
	private List<String> files;

	@Parameter(names = "--exclude", variableArity = true, description = "Files to be excluded from CPD check", required = false)
	private List<String> excludes;

	@Parameter(names = "--non-recursive", description = "Don't scan subdirectiories", required = false)
        private boolean nonRecursive;

	@Parameter(names = "--uri", description = "URI to process", required = false)
	private String uri;

	@Parameter(names = { "--help", "-h" }, description = "Print help text", required = false, help = true)
	private boolean help;

	// this has to be a public static class, so that JCommander can use it!
	public static class LanguageConverter implements IStringConverter<Language> {


		public Language convert(String languageString) {
			if (languageString == null || "".equals(languageString)) {
				languageString = DEFAULT_LANGUAGE;
			}
			return new LanguageFactory().createLanguage(languageString);
		}
	}

	public CPDConfiguration()
	{
	}

	@Deprecated
	public CPDConfiguration(int minimumTileSize, Language language, String encoding)
	{
		setMinimumTileSize(minimumTileSize);
		setLanguage(language);
		setEncoding(encoding);
	}


	public void setEncoding(String encoding) {
	    this.encoding = encoding;
	}

	public SourceCode sourceCodeFor(File file) {
		return new SourceCode(new SourceCode.FileCodeLoader(file,
				getSourceEncoding()));
	}

	public SourceCode sourceCodeFor(Reader reader, String sourceCodeName ) {
		return new SourceCode(new SourceCode.ReaderCodeLoader(reader, sourceCodeName ));
	}

	public void postContruct() {
        if (getEncoding() != null) {
            super.setSourceEncoding(getEncoding());
            if (!getEncoding().equals(System.getProperty("file.encoding")))
                System.setProperty("file.encoding", getEncoding());
        }
		if ( this.getLanguage() == null )
			this.setLanguage(CPDConfiguration.getLanguageFromString(DEFAULT_LANGUAGE));
		if (this.getRendererName() == null )
		    this.setRendererName(DEFAULT_RENDERER);
		if ( this.getRenderer() == null )
			this.setRenderer(getRendererFromString(getRendererName()));
	}

	public static Renderer getRendererFromString(String name /* , String encoding */) {
		if (name.equalsIgnoreCase(DEFAULT_RENDERER) || name.equals("")) {
			return new SimpleRenderer();
		} else if ("xml".equals(name)) {
			return new XMLRenderer();
		} else if ("csv".equals(name)) {
			return new CSVRenderer();
		} else if ("vs".equals(name)) {
			return new VSRenderer();
		}
		try {
			return (Renderer) Class.forName(name).newInstance();
		} catch (Exception e) {
			System.out.println("Can't find class '" + name
					+ "', defaulting to SimpleRenderer.");
		}
		return new SimpleRenderer();
	}

	public static Language getLanguageFromString(String languageString) {
		return new LanguageFactory().createLanguage(languageString);
	}

	public static void setSystemProperties(CPDConfiguration configuration) {
		Properties properties = System.getProperties();
		if (configuration.isIgnoreLiterals()) {
			properties.setProperty(JavaTokenizer.IGNORE_LITERALS, "true");
		} else {
		    properties.remove(JavaTokenizer.IGNORE_LITERALS);
		}
		if (configuration.isIgnoreIdentifiers()) {
			properties.setProperty(JavaTokenizer.IGNORE_IDENTIFIERS, "true");
        } else {
            properties.remove(JavaTokenizer.IGNORE_IDENTIFIERS);
		}
		if (configuration.isIgnoreAnnotations()) {
			properties.setProperty(JavaTokenizer.IGNORE_ANNOTATIONS, "true");
        } else {
            properties.remove(JavaTokenizer.IGNORE_ANNOTATIONS);
		}
		System.setProperties(properties);
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

	public Renderer getRenderer() {
		return renderer;
	}

	public Tokenizer tokenizer() {
		if ( language == null )
			throw new IllegalStateException("Language is null.");
		return language.getTokenizer();
	}

    public FilenameFilter filenameFilter() {
        if (language == null)
            throw new IllegalStateException("Language is null.");

        final FilenameFilter languageFilter = language.getFileFilter();
        final Set<String> exclusions = new HashSet<String>();

        if (excludes != null) {
            FileFinder finder = new FileFinder();
            for (String excludedFile : excludes) {
                File exFile = new File(excludedFile);
                if (exFile.isDirectory()) {
                    List<File> files = finder.findFilesFrom(excludedFile, languageFilter, true);
                    for (File f : files) {
                        exclusions.add(f.getAbsolutePath());
                    }
                } else {
                    exclusions.add(exFile.getAbsolutePath());
                }
            }
        }

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                File f = new File(dir, name);
                if (exclusions.contains(f.getAbsolutePath())) {
                    System.err.println("Excluding " + f.getAbsolutePath());
                    return false;
                }
                return languageFilter.accept(dir, name);
            }
        };
        return filter;
    }

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
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

	public boolean isSkipLexicalErrors() {
	    return skipLexicalErrors;
	}

	public void setSkipLexicalErrors(boolean skipLexicalErrors) {
	    this.skipLexicalErrors = skipLexicalErrors;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public String getURI() {
		return uri;
	}

	public void setURI(String uri) {
		this.uri = uri;
	}

	public List<String> getExcludes() {
	    return excludes;
	}

	public void setExcludes(List<String> excludes) {
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
}
