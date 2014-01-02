/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.ecmascript.rule.EcmascriptRuleChainVisitor;
import net.sourceforge.pmd.lang.java.rule.JavaRuleChainVisitor;
import net.sourceforge.pmd.lang.jsp.rule.JspRuleChainVisitor;
import net.sourceforge.pmd.lang.plsql.rule.PLSQLRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.RuleChainVisitor;
import net.sourceforge.pmd.lang.vm.rule.VmRuleChainVisitor;
import net.sourceforge.pmd.lang.xml.rule.XmlRuleChainVisitor;

/**
 * This is an enumeration of the Languages of which PMD is aware.  The primary
 * use of a Language is for Rules, but they are also used by utilities such as
 * CPD.
 * <p>
 * The following are key components of a Language in PMD:
 * <ul>
 * 	<li>Name - Full name of the Language</li>
 * 	<li>Short name - The common short form of the Language</li>
 * 	<li>Terse name - The shortest and simplest possible form of the Language
 * 		name, generally used for Rule configuration</li>
 * 	<li>Extensions - File extensions associated with the Language</li>
 * 	<li>Rule Chain Visitor - The RuleChainVisitor implementation used for this
 * 		Language</li>
 * 	<li>Versions - The LanguageVersions associated with the Language</li>
 * </ul>
 *
 * @see LanguageVersion
 * @see LanguageVersionDiscoverer
 */
public enum Language {

    //ANY("Any", null, null, null, (String)null),
    //UNKNOWN("Unknown", null, "unknown", null, (String)null),
    CPP("C++", null, "cpp", null, "h", "c", "cpp", "cxx", "cc", "C"),
    FORTRAN("Fortran", null, "fortran", null, "for", "f", "f66", "f77", "f90"),
    ECMASCRIPT("Ecmascript", null, "ecmascript", EcmascriptRuleChainVisitor.class, "js"),
    JAVA("Java", null, "java", JavaRuleChainVisitor.class, "java"),
    JSP("Java Server Pages", "JSP", "jsp", JspRuleChainVisitor.class, "jsp"),
    PHP("PHP: Hypertext Preprocessor", "PHP", "php", null, "php", "class"),
    PLSQL("PLSQL", null, "plsql", PLSQLRuleChainVisitor.class
         ,"sql", "trg", "prc","fnc"
	 ,"pld" // Oracle*Forms 
	 ,"pls" ,"plh" ,"plb" // Packages
	 ,"pck" ,"pks" ,"pkh" ,"pkb" // Packages
	 ,"typ" ,"tyb" // Object Types
	 ,"tps" ,"tpb" // Object Types
         ),
    RUBY("Ruby", null, "ruby", null, "rb", "cgi", "class"),
    XSL("XSL", null, "xsl", XmlRuleChainVisitor.class, "xsl", "xslt"),
    XML("XML", null, "xml", XmlRuleChainVisitor.class, "xml"),
    VM("VM", null, "vm", VmRuleChainVisitor.class, "vm");

    private final String name;
    private final String shortName;
    private final String terseName;
    private final List<String> extensions;
    private final Class<?> ruleChainVisitorClass;
    private final List<LanguageVersion> versions;

    /**
     * Language constructor.
     *
     * @param name The name of this Language.  Must not be <code>null</code>.
     * @param shortName The short name of this Language, if <code>null</code> the
     * name will be used at the short name.
     * @param terseName The terse name of this Language.
     * Must not be <code>null</code>.
     * @param ruleChainVisitorClass The RuleChainVisitor implementation class.
     * May be <code>null</code>.
     * @param extensions An array of extensions for this Language.
     * May be <code>null</code>.
     */
    private Language(String name, String shortName, String terseName, Class<?> ruleChainVisitorClass,
	    String... extensions) {
	if (name == null) {
	    throw new IllegalArgumentException("Name must not be null.");
	}
	if (terseName == null) {
	    throw new IllegalArgumentException("Terse name must not be null.");
	}
	this.name = name;
	this.shortName = shortName != null ? shortName : name;
	this.terseName = terseName;
	this.ruleChainVisitorClass = ruleChainVisitorClass;
	this.extensions = Collections.unmodifiableList(Arrays.asList(extensions));
	this.versions = new ArrayList<LanguageVersion>();

	// Sanity check: RuleChainVisitor is actually so, and has no arg-constructor?
	if (ruleChainVisitorClass != null) {
	    try {
		Object obj = ruleChainVisitorClass.newInstance();
		if (!(obj instanceof RuleChainVisitor)) {
		    throw new IllegalStateException("RuleChainVisitor class <" + ruleChainVisitorClass.getName()
			    + "> does not implement the RuleChainVisitor interface!");
		}
	    } catch (InstantiationException e) {
		throw new IllegalStateException("Unable to invoke no-arg constructor for RuleChainVisitor class <"
			+ ruleChainVisitorClass.getName() + ">!");
	    } catch (IllegalAccessException e) {
		throw new IllegalStateException("Unable to invoke no-arg constructor for RuleChainVisitor class <"
			+ ruleChainVisitorClass.getName() + ">!");
	    }
	}
    }

    /**
     * Get the full name of this Language.  This is generally the name of this
     * Language without the use of acronyms.
     * @return The full name of this Language.
     */
    public String getName() {
	return name;
    }

    /**
     * Get the short name of this Language.  This is the commonly used short
     * form of this Language's name, perhaps an acronym.
     * @return The short name of this Language.
     */
    public String getShortName() {
	return shortName;
    }

    /**
     * Get the terse name of this Language.  This is used for Rule configuration.
     * @return The terse name of this Language.
     */
    public String getTerseName() {
	return terseName;
    }

    /**
     * Get the list of file extensions associated with this Language.
     * @return List of file extensions.
     */
    public List<String> getExtensions() {
	return extensions;
    }

    /**
     * Returns whether the given Language handles the given file extension.
     * The comparison is done ignoring case.
     * @param extension A file extension.
     * @return <code>true</code> if this Language handles this extension, <code>false</code> otherwise.
     */
    public boolean hasExtension(String extension) {
	if (extension != null) {
	    for (String ext : extensions) {
		if (ext.equalsIgnoreCase(extension)) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * Get the RuleChainVisitor implementation class used when visiting the AST
     * structure for this Rules for this Language.
     * @return The RuleChainVisitor class.
     * @see RuleChainVisitor
     */
    public Class<?> getRuleChainVisitorClass() {
	return ruleChainVisitorClass;
    }

    /**
     * Gets the list of supported LanguageVersion for this Language.
     * @return The LanguageVersion for this Language.
     */
    public List<LanguageVersion> getVersions() {
	return versions;
    }

    /**
     * Get the current PMD defined default LanguageVersion for this Language.
     * This is an arbitrary choice made by the PMD product, and can change
     * between PMD releases.  Every Language has a default version.
     * @return The current default LanguageVersion for this Language.
     */
    public LanguageVersion getDefaultVersion() {
	init();
	for (LanguageVersion version : getVersions()) {
	    if (version.isDefaultVersion()) {
		return version;
	    }
	}
	throw new IllegalStateException("No default LanguageVersion configured for " + this);
    }

    /**
     * Get the LanguageVersion for the version string from this Language.
     * @param version The language version string.
     * @return The corresponding LanguageVersion, <code>null</code> if the
     * version string is not recognized.
     */
    public LanguageVersion getVersion(String version) {
	init();
	for (LanguageVersion languageVersion : getVersions()) {
	    if (languageVersion.getVersion().equals(version)) {
		return languageVersion;
	    }
	}
	return null;
    }

    /**
     * A friendly String form of the Language.
     */
    @Override
    public String toString() {
	return "Language [" + name + "]";
    }

    /**
     * A utility method to find the Languages which have Rule support.
     * @return A List of Languages with Rule support.
     */
    public static List<Language> findWithRuleSupport() {
	List<Language> languages = new ArrayList<Language>();
	for (Language language : Language.values()) {
	    if (language.getRuleChainVisitorClass() != null) {
		languages.add(language);
	    }
	}
	return languages;
    }

    /**
     * A utility method to find the Languages which are associated with
     * the given file extension.
     * @param extension The file extension.
     * @return A List of Languages which handle the extension.
     */
    public static List<Language> findByExtension(String extension) {
	List<Language> languages = new ArrayList<Language>();
	for (Language language : Language.values()) {
	    if (language.hasExtension(extension)) {
		languages.add(language);
	    }
	}
	return languages;
    }

    /**
     * A utility method to find the Language associated with the given
     * terse name, whatever the case is.
     * @param terseName The Language terse name.
     * @return The Language with this terse name, <code>null</code> if there is
     * no Language with this terse name.
     */
    public static Language findByTerseName(String terseName) {
	for (Language language : Language.values()) {
	    if (language.getTerseName().equalsIgnoreCase(terseName)) {
		return language;
	    }
	}
	return null;
    }

    /**
     * Return a comma separated list of Language terse names.
     * @param languages The languages.
     * @return Comma separated terse names.
     */
    public static String commaSeparatedTerseNames(List<Language> languages) {
	StringBuilder builder = new StringBuilder();
	for (Language language : languages) {
	    if (builder.length() > 0) {
		builder.append(", ");
	    }
	    builder.append(language.getTerseName());
	}
	return builder.toString();
    }

    private static void init() {
	// Force initialization of the LanguageVersion enum.
	// This must be done before the versions can be accessed on this enum.
	LanguageVersion.values();
    }

    /**
     * Return the default language for PMD.
     * @return the proper default language
     */
    public static Language getDefaultLanguage() {
	return Language.JAVA;
    }
}
