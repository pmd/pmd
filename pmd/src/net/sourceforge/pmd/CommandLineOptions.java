/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.RendererFactory;
import net.sourceforge.pmd.util.StringUtil;

public class CommandLineOptions {

    private final static int LANG_NAME_INDEX = 1;
    private final static int LANG_VERSION_INDEX = 2;
    
    
    private boolean debugEnabled;
    private boolean stressTestEnabled;
    private boolean benchmark;
    private boolean shortNamesEnabled;

    private int cpus = Runtime.getRuntime().availableProcessors();

    private String suppressMarker = PMD.SUPPRESS_MARKER;
    private String inputPath;
    private String reportFormat;
    private String reportFile;
    private boolean showSuppressedViolations = false;
    private Properties properties = new Properties();
    private String ruleSets;
    private String encoding = new InputStreamReader(System.in).getEncoding();
    private String auxClasspath;

    private String[] args;
    private int optionEndIndex;

    private RulePriority minPriority = RulePriority.LOW;

    private Language language;
    private LanguageVersion version;

    public CommandLineOptions(String[] args) {

        this.args = args;
        
        if (args == null || args.length < 3) {
            throw new IllegalArgumentException(usage());
        }
        int mandatoryIndex = 0;
        int optionStartIndex = 3;
        optionEndIndex = args.length;
        if (args[0].charAt(0) == '-') {
            mandatoryIndex = args.length - 3;
            optionStartIndex = 0;
            optionEndIndex = args.length - 3;
        }

        inputPath = args[mandatoryIndex];
        reportFormat = args[mandatoryIndex+1];
        if (StringUtil.isEmpty(reportFormat)) {
	    throw new IllegalArgumentException("Report renderer is required.");
	}
        ruleSets = new SimpleRuleSetNameMapper(args[mandatoryIndex+2]).getRuleSets();

        for (int optionsIndex = optionStartIndex; optionsIndex < optionEndIndex; optionsIndex++) {
            String opt = args[optionsIndex];
            if ("-debug".equals(opt)) {
                this.debugEnabled = true;
            } else if ("-stress".equals(opt)) {
                this.stressTestEnabled = true;
            } else if ("-shortnames".equals(opt)) {
                this.shortNamesEnabled = true;
            } else if ("-encoding".equals(opt)) {
        	checkOption(opt, optionsIndex, 1);
                this.encoding = args[++optionsIndex];
            } else if ("-cpus".equals(opt)) {
        	checkOption(opt, optionsIndex, 1);
        	this.cpus = parseInt(opt, args[++optionsIndex]);
            } else if ("-suppressmarker".equals(opt)) {
        	checkOption(opt, optionsIndex, 1);
                this.suppressMarker = args[++optionsIndex];
            } else if ("-lang".equals(opt)) { //We are waiting for 2 argument after this one
        	this.extractLanguageSpecification(optionsIndex);        		
        	optionsIndex += 2;
            } else if ("-minimumpriority".equals(opt)) {
        	checkOption(opt, optionsIndex, 1);
        	this.minPriority = parseMinimunPriority(args[++optionsIndex]);
            } else if ("-showsuppressed".equals(opt)) {
        	this.showSuppressedViolations = true;
            } else if ("-property".equals(opt)) {
        	checkOption(opt, optionsIndex, 2);
        	this.properties.put(args[++optionsIndex], args[++optionsIndex]);
            } else if ("-reportfile".equals(opt)) {
        	checkOption(opt, optionsIndex, 1);
                this.reportFile = args[++optionsIndex];
            } else if ("-benchmark".equals(opt)) {
                this.benchmark = true;
            } else if ("-auxclasspath".equals(opt)) {
        	checkOption(opt, optionsIndex, 1);
        	this.auxClasspath = args[++optionsIndex];
            }
        }
        // If no language has been specified, we fall back to default value
        if ( language == null )	{
            language = Language.getDefaultLanguage();
        }
        if ( version == null )	{
            version = LanguageVersion.getDefaultVersion();
        }
    }

    private void checkOption(String opt, int index, int count) {
	boolean valid = true;
	if (index + count >= optionEndIndex) {
	    valid = false;
	} else {
	    for (int i = 1; i <= count; i++) {
		if (args[index + i].charAt(0) == '-') {
		    valid = false;
		    break;
		}
	    }
	}
	if (!valid) {
	    throw new IllegalArgumentException(opt + " requires " + count + " parameters.\n\n" + usage());
	}
    }

    private RulePriority parseMinimunPriority(String priority) {
	try {
	    return RulePriority.valueOf(Integer.parseInt(priority));
	} catch (NumberFormatException e) {
	    throw new IllegalArgumentException("Minimum priority must be a whole number between " + RulePriority.HIGH
		    + " and " + RulePriority.LOW + ", " + priority + " received", e);
	}
    }
    
    private int parseInt(String opt, String s) {
	try {
	    return Integer.parseInt(s);
	} catch (NumberFormatException e) {
	    throw new IllegalArgumentException(opt + " parameter must be a whole number, " + s + " received");
	}
    }

    private void extractLanguageSpecification(int optionsIndex) {
	String languageSpecified = args[optionsIndex + LANG_NAME_INDEX];
	this.language = Language.findByTerseName(languageSpecified);
	if ( language == null ) {
	    throw new IllegalArgumentException("language '" + languageSpecified + "' is not recognized. Available language are : " + Language.commaSeparatedTerseNames(Language.findWithRuleSupport()));
	}
	else {
	    if ( args.length > (optionsIndex + LANG_VERSION_INDEX) ) {
        	    String specifiedVersion = args[optionsIndex + LANG_VERSION_INDEX];
        	    List<LanguageVersion> versions = LanguageVersion.findVersionsForLanguageTerseName(language.getTerseName());
        	    // If there is versions for this language, it should be a valid one...
        	    if ( ! versions.isEmpty() ) {
        		for (LanguageVersion version : versions ) {
                	    	if ( specifiedVersion.equals( version.getVersion() ) ) {
                	    	    this.version = version;
                	    	}
            	    	}
            	    	if ( version == null ) {
            	    	    throw new IllegalArgumentException("version '" + languageSpecified + "' is not availaible for " + language.getName() + "\nAvailable version are :" + LanguageVersion.commaSeparatedTerseNames(versions) );        		
                	}
        	    }
        	    // ... otherwise, version stays 'null'
	    }
	}
    }

    public Renderer createRenderer() {
	Renderer renderer = RendererFactory.createRenderer(reportFormat, new Properties());
	renderer.setShowSuppressedViolations(this.showSuppressedViolations);
	return renderer;
    }

    public boolean containsCommaSeparatedFileList() {
        return inputPath.indexOf(',') != -1;
    }

    public String getInputPath() {
        return this.inputPath;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String getReportFormat() {
        return this.reportFormat;
    }

    public String getReportFile() {
        return this.reportFile;
    }

    public String getRulesets() {
        return this.ruleSets;
    }

    public String getSuppressMarker() {
        return this.suppressMarker;
    }

    public boolean debugEnabled() {
        return debugEnabled;
    }

    public boolean stressTestEnabled() {
        return stressTestEnabled;
    }

    public int getCpus() {
        return cpus;
    }

    public boolean shortNamesEnabled() {
        return shortNamesEnabled;
    }

    public RulePriority getMinPriority() {
        return minPriority;
    }

    public boolean benchmark() {
        return benchmark;
    }

    public String getAuxClasspath() {
    	return auxClasspath;
    }
   
    public boolean isShowSuppressedViolations() {
        return showSuppressedViolations;
    }

    /**
     * @return the language
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * @return the version
     */
    public LanguageVersion getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(LanguageVersion version) {
        this.version = version;
    }

    public String usage() {
        return PMD.EOL + PMD.EOL +
                "Mandatory arguments:" + PMD.EOL +
                "1) A java source code filename or directory" + PMD.EOL +
                "2) A report format " + PMD.EOL +
                "3) A ruleset filename or a comma-delimited string of ruleset filenames" + PMD.EOL +
                PMD.EOL +
                "For example: " + PMD.EOL +
                "c:\\> java -jar pmd-" + PMD.VERSION + ".jar c:\\my\\source\\code html unusedcode" + PMD.EOL +
                PMD.EOL +
                "Optional arguments that may be put before or after the mandatory arguments: " + PMD.EOL +
                "-lang {name} {version}: specifiy version of a language PMD should use" + PMD.EOL + 
                "-debug: prints debugging information" + PMD.EOL +
                "-cpus: specifies the number of threads to create" + PMD.EOL +
                "-encoding: specifies the character set encoding of the source code files PMD is reading (i.e., UTF-8)" + PMD.EOL +
                "-suppressmarker: specifies the String that marks the a line which PMD should ignore; default is NOPMD" + PMD.EOL +
                "-shortnames: prints shortened filenames in the report" + PMD.EOL +
                "-minimumpriority: rule priority threshold; rules with lower priority than they will not be used" + PMD.EOL +
                "-showsuppressed: report should show suppressed rule violations" + PMD.EOL +
                "-property {name} {value}: define a property for the report" + PMD.EOL +
                "-reportfile: send report output to a file; default to System.out" + PMD.EOL +
                "-benchmark: output a benchmark report upon completion; default to System.err" + PMD.EOL +
                "-auxclasspath: specifies the classpath for libraries used by the source code (used by type resolution)" + PMD.EOL +
                "   (alternatively, a 'file://' URL to a text file containing path elements on consecutive lines)" + PMD.EOL +
                PMD.EOL +
                "Available report formats and their configuration properties are:" + PMD.EOL +
                getReports() +
                PMD.EOL +
                "For example on windows: " + PMD.EOL +
                "c:\\> java -jar pmd-" + PMD.VERSION + ".jar c:\\my\\source\\code text unusedcode,imports -lang java 1.5 -debug" + PMD.EOL +
                "c:\\> java -jar pmd-" + PMD.VERSION + ".jar c:\\my\\source\\code xml basic,design -encoding UTF-8" + PMD.EOL +
                "c:\\> java -jar pmd-" + PMD.VERSION + ".jar c:\\my\\source\\code html typeresolution -auxclasspath commons-collections.jar;derby.jar" + PMD.EOL +                
                "c:\\> java -jar pmd-" + PMD.VERSION + ".jar c:\\my\\source\\code html typeresolution -auxclasspath file:///C:/my/classpathfile" + PMD.EOL +
                PMD.EOL +
                "For example on *nix: " + PMD.EOL +
                "$ java -jar pmd-" + PMD.VERSION + ".jar /home/workspace/src/main/java/code nicehtml basic,design" + PMD.EOL +
                "$ java -jar pmd-" + PMD.VERSION + ".jar /home/workspace/src/main/java/code nicehtml basic,design -xslt my-own.xsl" + PMD.EOL +
                "$ java -jar pmd-" + PMD.VERSION + ".jar /home/workspace/src/main/java/code nicehtml typeresolution -auxclasspath commons-collections.jar:derby.jar" + PMD.EOL +
                PMD.EOL;
    }

    private static String getReports() {
	StringBuilder buf = new StringBuilder();
	for (String reportName : RendererFactory.REPORT_FORMAT_TO_RENDERER.keySet()) {
	    Renderer renderer = RendererFactory.createRenderer(reportName, new Properties());
	    buf.append("   ");
	    buf.append(reportName);
	    buf.append(": ");
	    if (!reportName.equals(renderer.getName())) {
		buf.append(" Deprecated alias for '" + renderer.getName());
		buf.append(PMD.EOL);
		continue;
	    }
	    buf.append(renderer.getDescription());
	    buf.append(PMD.EOL);
	    for (Map.Entry<String, String> entry : renderer.getPropertyDefinitions().entrySet()) {
		buf.append("       ");
		buf.append(entry.getKey());
		buf.append(" - ");
		buf.append(entry.getValue());
		buf.append(PMD.EOL);
	    }
	}
	return buf.toString();
    }
}




