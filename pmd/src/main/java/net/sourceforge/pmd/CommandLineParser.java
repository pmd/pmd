/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.Map;
import java.util.Properties;

import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.RendererFactory;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Replaces the previous CommandLineOptions class.
 * 
 * Command line options parser class. Produces a Configuration instance to use with PMD.
 * 
 * TODO 
 *      validate any incoming report arguments, note missing ones
 *      migrate the mandatory args to their own parameters as well
 *      auto-generate the usage() text as much as possible
 */
public class CommandLineParser {

	private final PMDConfiguration configuration = new PMDConfiguration();

	private static final int MIN_ARG_COUNT = 3;		// # of mandatory args required

	public CommandLineParser(String[] args) {

		if (args == null || args.length < MIN_ARG_COUNT) {
			throw new IllegalArgumentException(usage());
		}
		int mandatoryIndex = 0;
		int optionStartIndex = MIN_ARG_COUNT;
		int optionEndIndex = args.length;
		
		if (args[0].charAt(0) == '-') {
			mandatoryIndex = args.length - MIN_ARG_COUNT;
			optionStartIndex = 0;
			optionEndIndex = args.length - MIN_ARG_COUNT;
		}

		setMandatoryArgs(args, mandatoryIndex);

		for (int optionsIndex = optionStartIndex; optionsIndex < optionEndIndex; optionsIndex++) {

			String opt = args[optionsIndex];
			if (opt.charAt(0) != '-') {
				throw new IllegalArgumentException("Unknown option: " + opt);
			}
			CmdLineOption option = PMDParameters.optionFor(opt.substring(1));
			if (option == null) {
				throw new IllegalArgumentException("Unexpected command line argument: " + opt);
				}
			checkOption(args, opt, optionsIndex, optionEndIndex, option.parameterCount);
			option.apply(configuration, args, optionsIndex);
			optionsIndex += option.parameterCount;
		}
	}

	private void setMandatoryArgs(String[] args, int mandatoryIndex) {
		
		configuration.setInputPaths(args[mandatoryIndex]);
		configuration.setReportFormat(args[mandatoryIndex + 1]);
		
		if (StringUtil.isEmpty(configuration.getReportFormat())) {
			throw new IllegalArgumentException("Report renderer is required.");
			}

		configuration.setRuleSets(
				StringUtil.asString(
					RuleSetReferenceId.parse(args[mandatoryIndex + 2]).toArray(), ",")
					);
	}

	private static void checkOption(String[] args, String opt, int index, int optionEndIndex, int count) {
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

	public PMDConfiguration getConfiguration() {
		return configuration;
	}

	public static String jarName() {
		return "pmd-" + PMD.VERSION + ".jar";
	}
	
	public static String usage() {
		
		final String launchCmd = "java -jar " + jarName();
		
		return PMD.EOL
				+ PMD.EOL
				+ "Mandatory arguments:"																+ PMD.EOL
				+ "1) A java source code filename or directory"											+ PMD.EOL
				+ "2) A report format "																	+ PMD.EOL
				+ "3) A ruleset filename or a comma-delimited string of ruleset filenames"				+ PMD.EOL
				+ PMD.EOL
				+ "For example: "																		+ PMD.EOL
				+ "c:\\> " + launchCmd + " c:\\my\\source\\code html java-unusedcode"					+ PMD.EOL
				+ PMD.EOL
				+ "Optional arguments that may be put before or after the mandatory arguments: "		+ PMD.EOL
				+ PMDParameters.allOptionDescriptions("  ", PMD.EOL) 									+ PMD.EOL
				+ "Available report formats and their configuration properties are:"					+ PMD.EOL
				+ getReports()																			+ PMD.EOL
				+ "For example on windows: "															+ PMD.EOL
				+ "c:\\> " + launchCmd + " c:\\my\\source\\code text java-unusedcode,java-imports -version java 1.5 -debug" + PMD.EOL
				+ "c:\\> " + launchCmd + " c:\\my\\source\\code xml java-basic,java-design -encoding UTF-8"					+ PMD.EOL
				+ "c:\\> " + launchCmd + " c:\\my\\source\\code html java-typeresolution -auxclasspath commons-collections.jar;derby.jar" + PMD.EOL
				+ "c:\\> " + launchCmd + " c:\\my\\source\\code html java-typeresolution -auxclasspath file:///C:/my/classpathfile" + PMD.EOL
				+ PMD.EOL
				+ "For example on *nix: "				+ PMD.EOL
				+ "$ " + launchCmd + " /home/workspace/src/main/java/code nicehtml java-basic,java-design"				+ PMD.EOL
				+ "$ " + launchCmd + " /home/workspace/src/main/java/code nicehtml java-basic,java-design -xslt my-own.xsl" + PMD.EOL
				+ "$ " + launchCmd + " /home/workspace/src/main/java/code nicehtml java-typeresolution -auxclasspath commons-collections.jar:derby.jar"
				+ PMD.EOL + PMD.EOL;
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

//   still debugging
//	
//	private static String getReports2() {
//		StringBuilder buf = new StringBuilder();
//		for (String reportName : RendererFactory.REPORT_FORMAT_TO_RENDERER.keySet()) {
//			Renderer renderer = RendererFactory.createRenderer(reportName, new Properties());
//			buf.append("   ").append(reportName).append(": ");
//			if (!reportName.equals(renderer.getName())) {
//				buf.append(" Deprecated alias for '" + renderer.getName()).append(PMD.EOL);
//				continue;
//			}
//			buf.append(renderer.getDescription()).append(PMD.EOL);
//			for (Map.Entry<PropertyDescriptor<?>, Object> entry : renderer.getPropertiesByPropertyDescriptor().entrySet()) {
//				PropertyDescriptor<?> desc = entry.getKey();
//				buf.append("       ").append(desc.name()).append(": ").append(desc.description());
//				Object deflt = desc.defaultValue();
//				if (deflt != null) buf.append("   default: ").append(deflt);
//				buf.append(PMD.EOL);
//			}
//		}
//		return buf.toString();
//	}
}