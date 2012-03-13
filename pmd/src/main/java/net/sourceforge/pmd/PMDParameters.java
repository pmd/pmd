package net.sourceforge.pmd;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.CmdLineOption.Applicator;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;

/**
 * A collection of the various command line parameter as descriptors used by PMD
 * along with some type conversion utility methods and lookup facilities.
 * 
 * TODO provide the ability to auto-generate random legal examples for the usage() text
 * 
 * @author Brian Remedios
 */
public class PMDParameters {
	
	// Common to PMD & CPD

	private static CmdLineOption<PMDConfiguration> Debug = new CmdLineOption<PMDConfiguration>("debug",				
			"prints debugging information", 
			new Applicator<PMDConfiguration>() {
				public void apply(PMDConfiguration cfg, String[] args, int idx) {
					cfg.setDebug(true);
				}
			});
	private static CmdLineOption<PMDConfiguration> Encoding = new CmdLineOption<PMDConfiguration>("encoding",			
			"specifies the character set encoding of the source code files PMD is reading (i.e., UTF-8)", 1, 
			new Applicator<PMDConfiguration>() {
				public void apply(PMDConfiguration cfg, String[] args, int idx) {
					cfg.setSourceEncoding(args[idx + 1]);
				}
			});

	// PMD-only options
	
	private static CmdLineOption<PMDConfiguration> Threads = new CmdLineOption<PMDConfiguration>("threads",				
			"specifies the number of threads to create", 1, 
			new Applicator<PMDConfiguration>() {
				public void apply(PMDConfiguration cfg, String[] args, int idx) {
					cfg.setThreads(parseInt(args[idx], args[idx + 1]));
				}
			});

	private static CmdLineOption<PMDConfiguration> Benchmark = new CmdLineOption<PMDConfiguration>("benchmark",		
			"output a benchmark report upon completion; default to System.err",
			new Applicator<PMDConfiguration>() {
				public void apply(PMDConfiguration cfg, String[] args, int idx) {
					cfg.setBenchmark(true);
				}
			});
	private static CmdLineOption<PMDConfiguration> Stress = new CmdLineOption<PMDConfiguration>("stress",			
			"performs a stress test", 
			new Applicator<PMDConfiguration>() {
				public void apply(PMDConfiguration cfg, String[] args, int idx) {
					cfg.setStressTest(true);
				}
			});
	private static CmdLineOption<PMDConfiguration> RptShortNames = new CmdLineOption<PMDConfiguration>("shortnames", 	
			"prints shortened filenames in the report",
			new Applicator<PMDConfiguration>() {
				public void apply(PMDConfiguration cfg, String[] args, int idx) {
					cfg.setReportShortNames(true);
				}
			});
	private static CmdLineOption<PMDConfiguration> ShowSuppressed = new CmdLineOption<PMDConfiguration>("showsuppressed", 	
			"report should show suppressed rule violations",
			new Applicator<PMDConfiguration>() {
				public void apply(PMDConfiguration cfg, String[] args, int idx) {
					cfg.setShowSuppressedViolations(true);
				}
			});
	private static CmdLineOption<PMDConfiguration> SuppressMarker = new CmdLineOption<PMDConfiguration>("suppressmarker",	
			"specifies the String that marks the a line which PMD should ignore; default is NOPMD", 1,
			new Applicator<PMDConfiguration>() {
				public void apply(PMDConfiguration cfg, String[] args, int idx) {
					cfg.setSuppressMarker(args[idx + 1]);
				}
			});
	private static CmdLineOption<PMDConfiguration> MinPriority = new CmdLineOption<PMDConfiguration>("minimumpriority",	
			"rule priority threshold; rules with lower priority than they will not be used", 1, 
			new Applicator<PMDConfiguration>() {
				public void apply(PMDConfiguration cfg, String[] args, int idx) {
					cfg.setMinimumPriority(parseMinimumPriority(args[idx + 1]));
				}
			});
	private static CmdLineOption<PMDConfiguration> Property = new CmdLineOption<PMDConfiguration>("property",			
			"{name} {value}: define a property for the report", 2,
			new Applicator<PMDConfiguration>() {
				public void apply(PMDConfiguration cfg, String[] args, int idx) {
					cfg.getReportProperties().put(args[idx+1], args[idx+2]);
				}
			});
	private static CmdLineOption<PMDConfiguration> ReportFile = new CmdLineOption<PMDConfiguration>("reportfile",		
			"send report output to a file; default to System.out", 1,
			new Applicator<PMDConfiguration>() {
				public void apply(PMDConfiguration cfg, String[] args, int idx) {
					cfg.setReportFile(args[idx + 1]);
				}
			});
	private static CmdLineOption<PMDConfiguration> Version = new CmdLineOption<PMDConfiguration>("version",				
			"{name} {version}: specify version of a language PMD should use", 2, 
			new Applicator<PMDConfiguration>() {
				public void apply(PMDConfiguration cfg, String[] args, int idx) {
					cfg.setDefaultLanguageVersion(parseLanguageVersion(args, idx));
				}
			});
	private static CmdLineOption<PMDConfiguration> AuxClasspath = new CmdLineOption<PMDConfiguration>("auxclasspath",	
			"specifies the classpath for libraries used by the source code (used by type resolution)\n(alternatively, a 'file://' URL to a text file containing path elements on consecutive lines)",
			1, new Applicator<PMDConfiguration>() {
				public void apply(PMDConfiguration cfg, String[] args, int idx) {
					setClassPath(cfg, args[idx + 1]);
				}
			});

	private static final CmdLineOption[] AllOptions = new CmdLineOption[] {
			AuxClasspath, Benchmark, Debug, Encoding, MinPriority, Property,
			ReportFile, RptShortNames, ShowSuppressed, SuppressMarker, Stress,
			Threads, Version
			};

	public static CmdLineOption<?> optionFor(String id) {
		return OptionsById.get(id);
	}
	
	private static final Map<String, CmdLineOption<?>> OptionsById = asMapById(AllOptions);
	
	private static Map<String, CmdLineOption<?>> asMapById(CmdLineOption<?>[] options) {
		
		 Map<String, CmdLineOption<?>> map = new  HashMap<String, CmdLineOption<?>>(options.length);
		 for (CmdLineOption<?> opt : options) map.put(opt.id, opt);
		 return map;
	}

	private final static int LANGUAGE_NAME_INDEX = 1;
	private final static int LANGUAGE_VERSION_INDEX = 2;
	
	private static LanguageVersion parseLanguageVersion(String[] args, int optionsIndex) {
		String languageName = args[optionsIndex + LANGUAGE_NAME_INDEX];
		Language language = Language.findByTerseName(languageName);
		if (language == null) {
			throw new IllegalArgumentException("Unknown language '" + languageName
					+ "'.  Available Languages are : " + Language.commaSeparatedTerseNames(Language.findWithRuleSupport()));
		} else {
			if (args.length > (optionsIndex + LANGUAGE_VERSION_INDEX)) {
				String version = args[optionsIndex + LANGUAGE_VERSION_INDEX];
				List<LanguageVersion> languageVersions = LanguageVersion.findVersionsForLanguageTerseName(language.getTerseName());
				// If there is versions for this language, it should be a valid
				// one...
				if (!languageVersions.isEmpty()) {
					for (LanguageVersion languageVersion : languageVersions) {
						if (version.equals(languageVersion.getVersion())) {
							return languageVersion;
						}
					}
					throw new IllegalArgumentException("Language version '" + version
							+ "' is not available for language '" + language.getName()
							+ "'.\nAvailable versions are :" + LanguageVersion.commaSeparatedTerseNames(languageVersions));
				}
			}
			return language.getDefaultVersion();
		}
	}	

	private static RulePriority parseMinimumPriority(String priority) {
		try {
			return RulePriority.valueOf(Integer.parseInt(priority));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Minimum priority must be a whole number between "
							+ RulePriority.HIGH + " and " + RulePriority.LOW
							+ ", " + priority + " received", e);
		}
	}
	
	private static void setClassPath(PMDConfiguration cfg, String classPath) {
		try {
			cfg.prependClasspath(classPath);
		} catch (IOException e) {
			throw new IllegalArgumentException("Invalid auxiliary classpath: " + e.getMessage(), e);
		}
	}
	
	private static int parseInt(String opt, String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(opt + " parameter must be a whole number, " + s + " received");
		}
	}
	
	/**
	 * Render all the known option as descriptions on a string using the specified 
	 * line prefix and terminators.
	 *  
	 * @param prefix
	 * @param cr
	 * @return
	 */
	public static String allOptionDescriptions(String prefix, String cr) {
		
		StringBuilder sb = new StringBuilder();
		
		int maxLength = AllOptions[0].id.length();
		for (int i=1; i<AllOptions.length; i++) {
			maxLength = Math.max(maxLength, AllOptions[i].id.length());
		}		
		
		maxLength += 3;
		
		for (CmdLineOption<?> option : AllOptions) {
			sb.append(prefix);
			sb.append('-').append(option.id).append(": ");
			int spaces = maxLength - prefix.length() - 3 - option.id.length();
			for (int i=0; i<spaces; i++) sb.append(' ');
			sb.append(option.description).append(cr);
		}
		
		return sb.toString();
	}
}
