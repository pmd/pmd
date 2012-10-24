/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cli;

import java.util.Map;
import java.util.Properties;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.RendererFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * @author Romain Pelisse <belaran@gmail.com>
 *
 */
public class PMDCommandLineInterface {

	private static JCommander jcommander = null;	
	public static final String PROG_NAME = "pmd";
	
	public static final String NO_EXIT_AFTER_RUN = "net.sourceforge.pmd.cli.noExit";
	public static final String STATUS_CODE_PROPERTY = "net.sourceforge.pmd.cli.status";

	public static PMDParameters extractParameters(PMDParameters arguments, String[] args, String progName) {
		try {
			jcommander = new JCommander(arguments, args);
			jcommander.setProgramName(progName);
			if (arguments.isHelp()) {
				jcommander.usage();
				System.exit(0);
			}
		} catch (ParameterException e) {
			System.out.println(buildUsageText());
			System.out.println(e.getMessage());
			System.exit(1);
		}
		return arguments;
	}
	
	public static String buildUsageText() {
		final String launchCmd = "java -jar " + jarName();

		StringBuilder usage = new StringBuilder();
		
		String allCommandsDescription = null;
		if ( jcommander != null && jcommander.getCommands() != null ) {
			for ( String command : jcommander.getCommands().keySet() )
				allCommandsDescription += jcommander.getCommandDescription(command) + PMD.EOL;
		}		
		
		// TODO: Externalize that to a file available within the classpath ? - with a poor's man templating ?
		String fullText = PMD.EOL
		+ "Mandatory arguments:"																+ PMD.EOL
		+ "1) A java source code filename or directory"											+ PMD.EOL
		+ "2) A report format "																	+ PMD.EOL
		+ "3) A ruleset filename or a comma-delimited string of ruleset filenames"				+ PMD.EOL
		+ PMD.EOL
		+ "For example: "																		+ PMD.EOL
		+ "c:\\> " + launchCmd + " c:\\my\\source\\code html java-unusedcode"					+ PMD.EOL
		+ PMD.EOL;
		
		fullText += supportedVersions() + PMD.EOL;
		
		if ( allCommandsDescription != null ) {
			fullText += "Optional arguments that may be put before or after the mandatory arguments: "		+ PMD.EOL
					+ allCommandsDescription							 									+ PMD.EOL;
		}
		
		fullText += "Available report formats and their configuration properties are:"					+ PMD.EOL
		+ getReports()																			+ PMD.EOL
		+ getExamples(launchCmd) + PMD.EOL
		+ PMD.EOL + PMD.EOL;
			
		return fullText += usage.toString();
	}

	private static String getExamples(String launchCmd) {
		return getWindowsExample(launchCmd) + getUnixExample(launchCmd);		
	}
	
	private static String getWindowsExample(String launchCmd) {
		final String WINDOWS_PROMPT = "c:\\> ";
		final String WINDOWS_PATH_TO_CODE = "c:\\my\\source\\code";

		return "For example on windows: "															+ PMD.EOL
		+ WINDOWS_PROMPT + launchCmd + " -dir" + WINDOWS_PATH_TO_CODE + "-format text java-unusedcode,java-imports -version 1.5 -language java -debug" + PMD.EOL
		+ WINDOWS_PROMPT + launchCmd + " -dir" + WINDOWS_PATH_TO_CODE + "-f xml -rulesets java-basic,java-design -encoding UTF-8"					+ PMD.EOL
		+ WINDOWS_PROMPT + launchCmd + " -d" + WINDOWS_PATH_TO_CODE + "-rulesets java-typeresolution -auxclasspath commons-collections.jar;derby.jar" + PMD.EOL
		+ WINDOWS_PROMPT + launchCmd + " -d" + WINDOWS_PATH_TO_CODE + "-f html java-typeresolution -auxclasspath file:///C:/my/classpathfile" + PMD.EOL
		+ PMD.EOL;
	}
	
	private static String getUnixExample(String launchCmd) {
		final String UNIX_PROMPT = "$ ";
		return "For example on *nix: "				+ PMD.EOL
		+ UNIX_PROMPT + launchCmd + " -dir /home/workspace/src/main/java/code -f nicehtml -rulesets java-basic,java-design"				+ PMD.EOL
		+ UNIX_PROMPT + launchCmd + " -d ./src/main/java/code -f nicehtml -r java-basic,java-design -xslt my-own.xsl" + PMD.EOL
		+ UNIX_PROMPT + launchCmd + " -d ./src/main/java/code -f nicehtml -r java-typeresolution -auxclasspath commons-collections.jar:derby.jar"
		+ PMD.EOL;
	}
	
	private static String supportedVersions() {
		String supportedVersion = "Languages and version suported:" + PMD.EOL;
		for ( LanguageVersion version : LanguageVersion.values() ) {
			supportedVersion += version.getName() + ", ";
		}
		supportedVersion += PMD.EOL;
		supportedVersion += "Note that some language are not supported by PMD - only by CPD" + PMD.EOL;
		return supportedVersion;
	}

	/**
	 * For testing purpose only... 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(PMDCommandLineInterface.buildUsageText());
	}
	
	public static String jarName() {
		return "pmd-" + PMD.VERSION + ".jar";
	}
	
	private static String getReports() {
		StringBuilder buf = new StringBuilder();
		for (String reportName : RendererFactory.REPORT_FORMAT_TO_RENDERER.keySet()) {
			Renderer renderer = RendererFactory.createRenderer(reportName, new Properties());
			buf.append("   ").append(reportName).append(": ");
			if (!reportName.equals(renderer.getName())) {
				buf.append(" Deprecated alias for '" + renderer.getName()).append(PMD.EOL);
				continue;
			}
			buf.append(renderer.getDescription()).append(PMD.EOL);
			for (Map.Entry<String, String> entry : renderer
					.getPropertyDefinitions().entrySet()) {
				buf.append("       ").append(entry.getKey()).append(" - ");
				buf.append(entry.getValue()).append(PMD.EOL);
			}
		}
		return buf.toString();
	}

	public static void run(String[] args) {
    	if ( isExitAfterRunSet() )
    		System.exit(PMD.run(args));
    	else
    		setStatusCode(PMD.run(args));
    }

    private static boolean isExitAfterRunSet() {
    	return (System.getenv(NO_EXIT_AFTER_RUN) == null ? false : true);
    }

    private static void setStatusCode(int statusCode) {
    	System.setProperty(STATUS_CODE_PROPERTY, Integer.toString(statusCode));
    }	

}
