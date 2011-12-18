/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.RendererFactory;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Command line options parser class.  Produces a Configuration instance to
 * use with PMD processing.
 * 
 * @deprecated  - use the new CommandLineParser. Leaving this one here for comparison purposes for now.
 */
public class CommandLineOptions {

    private final static int LANGUAGE_NAME_INDEX = 1;
    private final static int LANGUAGE_VERSION_INDEX = 2;

    private final Configuration configuration = new Configuration();

    private String[] args;
    private int optionEndIndex;

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

	configuration.setInputPaths(args[mandatoryIndex]);
	configuration.setReportFormat(args[mandatoryIndex + 1]);
	if (StringUtil.isEmpty(configuration.getReportFormat())) {
	    throw new IllegalArgumentException("Report renderer is required.");
	}
	configuration.setRuleSets(StringUtil
		.asString(RuleSetReferenceId.parse(args[mandatoryIndex + 2]).toArray(), ","));

	for (int optionsIndex = optionStartIndex; optionsIndex < optionEndIndex; optionsIndex++) {
	    String opt = args[optionsIndex];
	    if ("-debug".equals(opt)) {
		configuration.setDebug(true);
	    } else if ("-stress".equals(opt)) {
		configuration.setStressTest(true);
	    } else if ("-shortnames".equals(opt)) {
		configuration.setReportShortNames(true);
	    } else if ("-encoding".equals(opt)) {
		checkOption(opt, optionsIndex, 1);
		configuration.setSourceEncoding(args[++optionsIndex]);
	    } else if ("-threads".equals(opt)) {
		checkOption(opt, optionsIndex, 1);
		configuration.setThreads(parseInt(opt, args[++optionsIndex]));
	    } else if ("-suppressmarker".equals(opt)) {
		checkOption(opt, optionsIndex, 1);
		configuration.setSuppressMarker(args[++optionsIndex]);
	    } else if ("-version".equals(opt)) {
		checkOption(opt, optionsIndex, 2);
		configuration.setDefaultLanguageVersion(parseLanguageVersion(optionsIndex));
		optionsIndex += 2;
	    } else if ("-minimumpriority".equals(opt)) {
		checkOption(opt, optionsIndex, 1);
		configuration.setMinimumPriority(parseMinimunPriority(args[++optionsIndex]));
	    } else if ("-showsuppressed".equals(opt)) {
		configuration.setShowSuppressedViolations(true);
	    } else if ("-property".equals(opt)) {
		checkOption(opt, optionsIndex, 2);
		configuration.getReportProperties().put(args[++optionsIndex], args[++optionsIndex]);
	    } else if ("-reportfile".equals(opt)) {
		checkOption(opt, optionsIndex, 1);
		configuration.setReportFile(args[++optionsIndex]);
	    } else if ("-benchmark".equals(opt)) {
		configuration.setBenchmark(true);
	    } else if ("-auxclasspath".equals(opt)) {
		checkOption(opt, optionsIndex, 1);
		try {
		    configuration.prependClasspath(args[++optionsIndex]);
		} catch (IOException e) {
		    throw new IllegalArgumentException("Invalid auxiliary classpath: " + e.getMessage(), e);
		}
	    } else {
		throw new IllegalArgumentException("Unexpected command line argument: " + opt);
	    }
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

    private LanguageVersion parseLanguageVersion(int optionsIndex) {
	String languageName = args[optionsIndex + LANGUAGE_NAME_INDEX];
	Language language = Language.findByTerseName(languageName);
	if (language == null) {
	    throw new IllegalArgumentException("Unknown language '" + languageName + "'.  Available Languages are : "
		    + Language.commaSeparatedTerseNames(Language.findWithRuleSupport()));
	} else {
	    if (args.length > (optionsIndex + LANGUAGE_VERSION_INDEX)) {
		String version = args[optionsIndex + LANGUAGE_VERSION_INDEX];
		List<LanguageVersion> languageVersions = LanguageVersion.findVersionsForLanguageTerseName(language
			.getTerseName());
		// If there is versions for this language, it should be a valid one...
		if (!languageVersions.isEmpty()) {
		    for (LanguageVersion languageVersion : languageVersions) {
			if (version.equals(languageVersion.getVersion())) {
			    return languageVersion;
			}
		    }
		    throw new IllegalArgumentException("Language version '" + version
			    + "' is not available for language '" + language.getName()
			    + "'.\nAvailable versions are :"
			    + LanguageVersion.commaSeparatedTerseNames(languageVersions));
		}
	    }
	    return language.getDefaultVersion();
	}
    }

    public Configuration getConfiguration() {
	return configuration;
    }

    public static String usage() {
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
                "-version {name} {version}: specify version of a language PMD should use" + PMD.EOL + 
                "-debug: prints debugging information" + PMD.EOL +
                "-threads: specifies the number of threads to create" + PMD.EOL +
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
                "c:\\> java -jar pmd-" + PMD.VERSION + ".jar c:\\my\\source\\code text unusedcode,imports -version java 1.5 -debug" + PMD.EOL +
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