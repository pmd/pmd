package net.sourceforge.pmd;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;

/**
 * This class contains the details for the runtime configuration of PMD.
 * The aspects of the configuration, and their defaults are:
 * <ul>
 * 	<li>Debug logging, disabled by default.</li>
 * 	<li>Stress test to randomize file processing order, disabled by default.</li>
 * 	<li>The number of threads to create when invoking on multiple files,
 *	    defaults one thread per available processor.</li>
 * 	<li></li>
 * 	<li></li>
 * 	<li></li>
 * 	<li>Suppress marker defaults to {@link PMD#SUPPRESS_MARKER}.</li>
 * 	<li>ClassLoader defaults to ClassLoader of this class.</li>
 * 	<li>LanguageVersionDiscoverer uses its own defaults.</li>
 * </ul>
 */
public class Configuration {

    private boolean debug;
    private boolean stressTest;
    private int threads = Runtime.getRuntime().availableProcessors();

    private boolean shortNames;
    private String inputPath;

    private String reportFormat;
    private String reportFile;
    private String ruleSets;
    private String encoding = new InputStreamReader(System.in).getEncoding();
    private String linePrefix;
    private String linkPrefix;
    private RulePriorityEnum minPriority = RulePriorityEnum.LOW;
    private boolean benchmark;
    private String xsltFilename;
    private String auxClasspath;

    private String suppressMarker = PMD.SUPPRESS_MARKER;
    private ClassLoader classLoader = getClass().getClassLoader();
    private LanguageVersionDiscoverer languageVersionDiscoverer = new LanguageVersionDiscoverer();

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
     * @param suppressMarker
     */
    public void setSuppressMarker(String suppressMarker) {
	this.suppressMarker = suppressMarker;
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
    public LanguageVersion getLanguageVersionOfFile(String fileName) {
	LanguageVersion languageVersion = languageVersionDiscoverer.getDefaultLanguageVersionForFile(fileName);
	if (languageVersion == null) {
	    // For compatibility with older code that does not always pass in
	    // a correct filename.
	    languageVersion = languageVersionDiscoverer.getDefaultLanguageVersion(Language.JAVA);
	}
	return languageVersion;
    }
}
