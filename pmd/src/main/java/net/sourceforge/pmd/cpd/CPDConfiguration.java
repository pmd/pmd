/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Properties;

import net.sourceforge.pmd.AbstractConfiguration;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;

/**
 *
 * @author Brian Remedios
 * @author Romain Pelisse - <belaran@gmail.com>
 */
public class CPDConfiguration extends AbstractConfiguration {

	public final static String DEFAULT_LANGUAGE = "java";

	public final static String DEFAULT_RENDERER = "text";

	@Parameter(names = "--language", description = "sources code language. Default value is"
			+ DEFAULT_LANGUAGE, required = false, converter = LanguageConverter.class)
	private Language language;

	@Parameter(names = "--minimum-tokens", description = "minimum", required = true)
	private int minimumTileSize;

	@Parameter(names = "--skip-duplicate-files", description = "ToDo", required = false)
	private boolean skipDuplicates;

	@Parameter(names = "--format", description = "report format. Default value is "
		+ DEFAULT_RENDERER, required = false, converter = RendererConverter.class)
	private Renderer renderer;

	@Parameter(names = "--encoding", description = "ToDo", required = false, converter = EncodingConverter.class)
	private String encoding;

	@Parameter(names = "--ignore-literals", description = "ToDo", required = false)
	private boolean ignoreLiterals;

	@Parameter(names = "--ignore-identifiers", description = "ToDo", required = false)
	private boolean ignoreIdentifiers;

	@Parameter(names = "--ignore-annotations", description = "ToDo", required = false)
	private boolean ignoreAnnotations;

	@Parameter(names = "--files", variableArity = true, description = "ToDo", required = true)
	private List<String> files;

	@Parameter(names = { "--help", "-h" }, description = "Print help text", required = false, help = true)
	private boolean help;

	static class LanguageConverter implements IStringConverter<Language> {

		public Language convert(String languageString) {
			if (languageString == null || "".equals(languageString)) {
				languageString = DEFAULT_LANGUAGE;
			}
			return new LanguageFactory().createLanguage(languageString);
		}
	}

	static class RendererConverter implements IStringConverter<Renderer> {

		public Renderer convert(String formatString) {
			if (formatString == null || "".equals(formatString)) {
				formatString = DEFAULT_RENDERER;
			}
			return getRendererFromString(formatString);
		}
	}

	class EncodingConverter implements IStringConverter<String> {

		public String convert(String encoding) {
			if (encoding == null || "".equals(encoding))
				encoding = System.getProperty("file.encoding");
			return setEncoding(encoding);
		}
	}

	public String setEncoding(String theEncoding) {
		super.setSourceEncoding(theEncoding);

		if (!theEncoding.equals(System.getProperty("file.encoding")))
			System.setProperty("file.encoding", theEncoding);
		return theEncoding;
	}

	public SourceCode sourceCodeFor(File file) {
		return new SourceCode(new SourceCode.FileCodeLoader(file,
				getSourceEncoding()));
	}

	public void postContruct() {
		if ( this.getLanguage() == null )
			this.setLanguage(CPDConfiguration.getLanguageFromString(DEFAULT_LANGUAGE));
		if ( this.getRenderer() == null )
			this.setRenderer(getRendererFromString(DEFAULT_RENDERER));

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
		}
		if (configuration.isIgnoreIdentifiers()) {
			properties.setProperty(JavaTokenizer.IGNORE_IDENTIFIERS, "true");
		}
		if (configuration.isIgnoreAnnotations()) {
			properties.setProperty(JavaTokenizer.IGNORE_ANNOTATIONS, "true");
		}
		System.setProperties(properties);
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

	public Renderer getRenderer() {
		return renderer;
	}

	public Tokenizer tokenizer() {
		if ( language == null )
			throw new IllegalStateException("Language is null.");
		return language.getTokenizer();
	}

	public FilenameFilter filenameFilter() {
		if ( language == null )
			throw new IllegalStateException("Language is null.");
		return language.getFileFilter();
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

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
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
