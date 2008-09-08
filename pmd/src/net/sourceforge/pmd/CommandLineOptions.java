/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.EmacsRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.IDEAJRenderer;
import net.sourceforge.pmd.renderers.PapariTextRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.SummaryHTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.VBHTMLRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.renderers.XSLTRenderer;
import net.sourceforge.pmd.renderers.YAHTMLRenderer;

import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.List;

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
    private String ruleSets;
    private String encoding = new InputStreamReader(System.in).getEncoding();
    private String linePrefix;
    private String linkPrefix;
    private String xsltFilename;
    private String auxClasspath;

    private String[] args;

    private RulePriority minPriority = RulePriority.LOW;

    private Language language;
    private LanguageVersion version;
   
    
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

    public CommandLineOptions(String[] args) {

        this.args = args; // needed by createRenderer
        
        if (args == null || args.length < 3) {
            throw new IllegalArgumentException(usage());
        }
        int optIndex = 0;
        if (args[0].charAt(0) == '-') {
            optIndex = args.length - 3;
        }

        inputPath = args[optIndex];
        reportFormat = args[optIndex+1];
        ruleSets = new SimpleRuleSetNameMapper(args[optIndex+2]).getRuleSets();

        for (int optionsIndex = 0; optionsIndex < args.length; optionsIndex++) {
            if (args[optionsIndex].equals("-debug")) {
                debugEnabled = true;
            } else if (args[optionsIndex].equals("-stress")) {
                stressTestEnabled = true;
            } else if (args[optionsIndex].equals("-shortnames")) {
                shortNamesEnabled = true;
            } else if (args[optionsIndex].equals("-encoding")) {
                encoding = args[++optionsIndex];
            } else if (args[optionsIndex].equals("-cpus")) {
        	checkCpuOptions(optionsIndex);
            } else if (args[optionsIndex].equals("-suppressmarker")) {
                suppressMarker = args[++optionsIndex];
            } else if (args[optionsIndex].equals("-lang") ) { //We are waiting for 2 argument after this one
        	this.extractLanguageSpecification(optionsIndex);        		
            } else if (args[optionsIndex].equals("-lineprefix")) {
                linePrefix = args[++optionsIndex];
            } else if (args[optionsIndex].equals("-linkprefix")) {
                linkPrefix = args[++optionsIndex];
            } else if (args[optionsIndex].equals("-minimumpriority")) {
        	checkingMinimunPriority(optionsIndex);
            } else if (args[optionsIndex].equals("-reportfile")) {
                reportFile = args[++optionsIndex];
            } else if (args[optionsIndex].equals("-benchmark")) {
                benchmark = true;
            } else if ( args[optionsIndex].equals("-xslt") ) {
        	settingXsltRendering(optionsIndex);
            } else if (args[optionsIndex].equals("-auxclasspath")) {
        	settingAuxClasspath(optionsIndex);
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
    
    private void settingAuxClasspath(int optionsIndex) {
	optionsIndex++;
	if ( optionsIndex >= args.length ) {
		throw new IllegalArgumentException(usage());
       	}
	this.auxClasspath = args[optionsIndex];  
    }

    private void settingXsltRendering(int optionsIndex) {
    	optionsIndex++;
    	if ( optionsIndex >= args.length ) {
    		 throw new IllegalArgumentException(usage());
    	}
    	this.xsltFilename = args[optionsIndex];
    }

    private void checkingMinimunPriority(int optionsIndex) {
        try {
            minPriority = RulePriority.valueOf(Integer.parseInt(args[++optionsIndex]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "minimumpriority parameter must be a whole number between " + RulePriority.HIGH + " and " + RulePriority.LOW + ", {0} received", args[optionsIndex]),e);
        }
    }

    private void checkCpuOptions(int optionsIndex) {
        try {
            cpus = Integer.parseInt(args[++optionsIndex]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "cpus parameter must be a whole number, {0} received", args[optionsIndex]));
        }
    }

    private void extractLanguageSpecification(int optionsIndex) {
	String languageSpecified = args[optionsIndex + LANG_NAME_INDEX];
	this.language = Language.findByTerseName(languageSpecified);
	if ( language == null ) {
	    throw new IllegalArgumentException("language '" + languageSpecified + "' is not recognized. Availaible language are : " + Language.commaSeparatedTerseNames(Language.findWithRuleSupport()));
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
        if (reportFormat.equals("xml")) {
            return new XMLRenderer();
        } else if (reportFormat.equals("ideaj")) {
            return new IDEAJRenderer(args);
        } else if (reportFormat.equals("papari")) {
            return new PapariTextRenderer();
        } else if (reportFormat.equals("text")) {
            return new TextRenderer();
        } else if (reportFormat.equals("emacs")) {
            return new EmacsRenderer();
        } else if (reportFormat.equals("csv")) {
            return new CSVRenderer();
        } else if (reportFormat.equals("html")) {
            return new HTMLRenderer();
        } else if (reportFormat.equals("nicehtml")) {
            return new XSLTRenderer(this.xsltFilename);
        } else if (reportFormat.equals("yahtml")) {
            return new YAHTMLRenderer();
        } else if (reportFormat.equals("summaryhtml")) {
            return new SummaryHTMLRenderer(linkPrefix, linePrefix);
        } else if (reportFormat.equals("vbhtml")) {
            return new VBHTMLRenderer();
        }
        if (!reportFormat.equals("")) {
            try {
                return (Renderer) Class.forName(reportFormat).newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Can't find the custom format " + reportFormat + ": " + e.getClass().getName());
            }
        }

        throw new IllegalArgumentException("Can't create report with format of " + reportFormat);
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
                "-lang name version: specifiy which version of which language PMD should looks for" + PMD.EOL + 
                "-debug: prints debugging information" + PMD.EOL +
                "-cpus: specifies the number of threads to create" + PMD.EOL +
                "-encoding: specifies the character set encoding of the source code files PMD is reading (i.e., UTF-8)" + PMD.EOL +
                "-excludemarker: specifies the String that marks the a line which PMD should ignore; default is NOPMD" + PMD.EOL +
                "-shortnames: prints shortened filenames in the report" + PMD.EOL +
                "-linkprefix: path to HTML source, for summary html renderer only" + PMD.EOL +
                "-lineprefix: custom anchor to affected line in the source file, for summary html renderer only" + PMD.EOL +
                "-minimumpriority: rule priority threshold; rules with lower priority than they will not be used" + PMD.EOL +
                "-reportfile: send report output to a file; default to System.out" + PMD.EOL +
                "-benchmark: output a benchmark report upon completion; default to System.err" + PMD.EOL +
                "-xslt: override default xslt for 'nicehtml' output." + PMD.EOL +
                "-auxclasspath: specifies the classpath for libraries used by the source code (used by type resolution)" + PMD.EOL +
                "   (alternatively, a 'file://' URL to a text file containing path elements on consecutive lines)" + PMD.EOL +
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
}




