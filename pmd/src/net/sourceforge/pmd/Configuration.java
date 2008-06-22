package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.util.ClasspathClassLoader;


/**
 * This class contains the details for the runtime configuration of PMD.
 * There are several aspects to the configuration of PMD.
 * <p>
 * The aspects related to generic PMD behavior:
 * <ul>
 * 	<li>Suppress marker is used in source files to suppress a RuleViolation,
 *	    defaults to {@link PMD#SUPPRESS_MARKER}.</li>
 * 	<li>The number of threads to create when invoking on multiple files,
 *	    defaults one thread per available processor.</li>
 * 	<li>A ClassLoader to use when loading classes during Rule processing
 * 	    (e.g. during type resolution), defaults to ClassLoader of the
 * 	    Configuration class.</li>
 *	<li>A means to configure a ClassLoader using an auxiliary classpath
 *	    String, instead of directly setting it programmatically.
 *	<li>A LanguageVersionDiscoverer instance, which defaults to using the
 *	    default LanguageVersion of each Language.  Means are provided to
 *	    change the LanguageVersion for each Language.</li>
 * </ul>
 * <p>
 * The aspects related to Rules and Source files are:
 * <ul>
 * 	<li>A comma separated list of RuleSets URIs.</li>
 * 	<li>A minimum priority filter when loading Rules from RuleSets,
 * 	    defaults to {@link RulePriority#LOW}.</li>
 * 	<li></li>
 *	<li></li>
 *	<li></li>
 *	<li></li>
 *	<li></li>
 * </ul>
 * <p>
 * The aspects related to Reporting are:
 * <ul>
 * 	<li></li>
 *	<li></li>
 *	<li></li>
 *	<li></li>
 *	<li></li>
 * </ul>
 * <p>
 * The aspects related to special PMD behavior are:
 * <ul>
 * 	<li></li>
 *	<li></li>
 *	<li></li>
 *	<li></li>
 *	<li></li>
 *	<li></li>
 * </ul>
 */
public class Configuration {

    // General behavior options
    private String suppressMarker = PMD.SUPPRESS_MARKER;
    private int threads = Runtime.getRuntime().availableProcessors();
    private ClassLoader classLoader = getClass().getClassLoader();
    private String auxClasspath;
    private LanguageVersionDiscoverer languageVersionDiscoverer = new LanguageVersionDiscoverer();

    // Rule and source file options
    private String ruleSets;
    private RulePriority minPriority = RulePriority.LOW;
    private boolean shortNames;
    private String inputPath;
    private String sourceEncoding = new InputStreamReader(System.in).getEncoding();

    // Reporting options
    private String reportFormat;
    private String reportEncoding;
    private String reportFile;
    private String linePrefix;
    private String linkPrefix;
    private String xsltFilename;

    // Special behavior options
    private boolean debug;
    private boolean stressTest;
    private boolean benchmark;

    // TODO Figure out how to remove this
    private String targetJDK;

    // TODO Figure out how to remove this
    private boolean checkJavaFiles = true;

    // TODO Figure out how to remove this
    private boolean checkJspFiles;

    /**
     * Get the suppress marker.  This the source level marker used to indicate
     * a RuleViolation should be suppressed.
     * @return The suppress marker.
     */
    public String getSuppressMarker() {
	return suppressMarker;
    }

    /**
     * Set the suppress marker.
     * @param suppressMarker The suppress marker to use.
     */
    public void setSuppressMarker(String suppressMarker) {
	this.suppressMarker = suppressMarker;
    }

    /**
     * Get the number of threads to use when processing Rules.
     * @return The number of threads.
     */
    public int getThreads() {
	return threads;
    }

    /**
     * Set the number of threads to use when processing Rules.
     * @param threads The number of threads.
     */
    public void setThreads(int threads) {
	this.threads = threads;
    }

    /**
     * Get the ClassLoader being used by PMD when processing Rules.
     * @return The ClassLoader being used
     */
    public ClassLoader getClassLoader() {
	return classLoader;
    }

    /**
     * Set the ClassLoader being used by PMD when processing Rules.
     * Setting a value of <code>null</code> will cause the default
     * ClassLoader to be used.
     * @param classLoader The ClassLoader to use
     */
    public void setClassLoader(ClassLoader classLoader) {
	if (classLoader == null) {
	    classLoader = getClass().getClassLoader();
	}
	this.classLoader = classLoader;
    }

    /**
     * Get the auxiliary classpath to use when processing Rules.
     * @return The auxiliary classpath String. May be <code>null</code>.
     */
    public String getAuxClasspath() {
	return auxClasspath;
    }

    /**
     * Set the auxiliary classpath to use when processing Rules.
     * This will cause a ClassLoader to be created, and set as the
     * ClassLoader to use when processing Rules.
     * @param auxClasspath The auxiliary classpath.
     * @see Configuration#setClassLoader(ClassLoader)
     */
    public void setAuxClasspath(String auxClasspath) throws IOException {
	setClassLoader(createClasspathClassLoader(auxClasspath));
	this.auxClasspath = auxClasspath;
    }

    /**
     * Get the LanguageVersionDiscoverer, used to determine the LanguageVersion
     * of a source file.
     * @return The LanguageVersionDiscoverer.
     */
    public LanguageVersionDiscoverer getLanguageVersionDiscoverer() {
	return languageVersionDiscoverer;
    }

    /**
     * Set the given LanguageVersion as the current default for it's Language.
     *
     * @param languageVersion the LanguageVersion
     */
    public void setDefaultLanguageVersion(LanguageVersion languageVersion) {
	setDefaultLanguageVersions(Arrays.asList(languageVersion));
    }

    /**
     * Set the given LanguageVersions as the current default for their Languages.
     *
     * @param languageVersions The LanguageVersions.
     */
    public void setDefaultLanguageVersions(List<LanguageVersion> languageVersions) {
	for (LanguageVersion languageVersion : languageVersions) {
	    languageVersionDiscoverer.setDefaultLanguageVersion(languageVersion);
	}
    }

    /**
     * Get the LanguageVersion of the source file with given name. This depends on the fileName
     * extension, and the java version.
     * <p/>
     * For compatibility with older code that does not always pass in a correct filename,
     * unrecognized files are assumed to be java files.
     *
     * @param fileName Name of the file, can be absolute, or simple.
     * @return the LanguageVersion
     */
    // FUTURE Delete this? I can't think of a good reason to keep it around.  Failure to determine the LanguageVersion for a file should be a hard error, or simply cause the file to be skipped?
    public LanguageVersion getLanguageVersionOfFile(String fileName) {
	LanguageVersion languageVersion = languageVersionDiscoverer.getDefaultLanguageVersionForFile(fileName);
	if (languageVersion == null) {
	    // For compatibility with older code that does not always pass in
	    // a correct filename.
	    languageVersion = languageVersionDiscoverer.getDefaultLanguageVersion(Language.JAVA);
	}
	return languageVersion;
    }

    /**
     * Create a ClassLoader which loads classes using a CLASSPATH like String.
     * If the String looks like a URL to a file (e.g. starts with <code>file://</code>)
     * the file will be read with each line representing an entry on the classpath.
     * <p>
     * The ClassLoader used to load the <code>net.sourceforge.pmd.Configuration</code> class
     * will be used as the parent ClassLoader of the created ClassLoader.
     * 
     * @param classpath The classpath String.
     * @return A ClassLoader
     * @throws IOException
     * @see ClasspathClassLoader
     */
    public static ClassLoader createClasspathClassLoader(String classpath) throws IOException {
	ClassLoader classLoader = Configuration.class.getClassLoader();
	if (classpath != null) {
	    classLoader = new ClasspathClassLoader(classpath, classLoader);
	}
	return classLoader;
    }
}
