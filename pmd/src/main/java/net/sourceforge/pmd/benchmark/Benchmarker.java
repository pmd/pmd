package net.sourceforge.pmd.benchmark;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * 
 *
 */
public class Benchmarker {

	private static final int TIME_COLUMN = 48;

    private static class Result implements Comparable<Result> {
        public Rule rule;
        public long time;

        public int compareTo(Result other) {
            if (other.time < time) {
                return -1;
            } else if (other.time > time) {
                return 1;
            }

            return rule.getName().compareTo(other.rule.getName());
        }

        public Result(long elapsed, Rule rule) {
            this.rule = rule;
            this.time = elapsed;
        }
    }

    private static boolean findBooleanSwitch(String[] args, String name) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static String findOptionalStringValue(String[] args, String name, String defaultValue) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(name)) {
                return args[i + 1];
            }
        }
        return defaultValue;
    }

    public static void main(String[] args) throws RuleSetNotFoundException, IOException, PMDException {
    	
        String targetjdk = findOptionalStringValue(args, "--targetjdk", "1.4");
        Language language = Language.JAVA;
        LanguageVersion languageVersion = language.getVersion(targetjdk);
        if (languageVersion == null) {
        	languageVersion = language.getDefaultVersion();
        }

        String srcDir = findOptionalStringValue(args, "--source-directory", "/usr/local/java/src/java/lang/");
        List<DataSource> dataSources = FileUtil.collectFiles(srcDir, new LanguageFilenameFilter(language));

        boolean debug = findBooleanSwitch(args, "--debug");
        boolean parseOnly = findBooleanSwitch(args, "--parse-only");

        if (debug) {
            System.out.println("Using " +language.getName() + " " + languageVersion.getVersion());
        }
        if (parseOnly) {
            parseStress(languageVersion, dataSources);
        } else {
            String ruleset = findOptionalStringValue(args, "--ruleset", "");
            if (debug) {
        		System.out.println("Checking directory " + srcDir);
            }
            Set<Result> results = new TreeSet<Result>();
            RuleSetFactory factory = new RuleSetFactory();
            if (ruleset.length() > 0) {
                stress(languageVersion, factory.createRuleSet(ruleset), dataSources, results, debug);
            } else {
                Iterator<RuleSet> i = factory.getRegisteredRuleSets();
                while (i.hasNext()) {
                    stress(languageVersion, i.next(), dataSources, results, debug);
                }
            }
            System.out.println("=========================================================");
            System.out.println("Rule\t\t\t\t\t\tTime in ms");
            System.out.println("=========================================================");
            for (Result result: results) {
                StringBuilder out = new StringBuilder(result.rule.getName());
                while (out.length() < TIME_COLUMN) {
                    out.append(' ');
                }
                out.append(result.time);
                System.out.println(out.toString());
            }
        }

        System.out.println("=========================================================");
    }

    private static void parseStress(LanguageVersion languageVersion, List<DataSource> dataSources) throws IOException {
        long start = System.currentTimeMillis();
        for (DataSource dataSource: dataSources) {
            LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
	    languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions()).parse(
		    dataSource.getNiceFileName(false, null), new InputStreamReader(dataSource.getInputStream()));
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("That took " + elapsed + " ms");
    }

    private static void stress(LanguageVersion languageVersion, RuleSet ruleSet, List<DataSource> dataSources, Set<Result> results, boolean debug) throws PMDException, IOException {
        Collection<Rule> rules = ruleSet.getRules();
        for (Rule rule: rules) {
            if (debug) {
            	System.out.println("Starting " + rule.getName());
            }

            RuleSet working = new RuleSet();
            working.addRule(rule);
            RuleSets ruleSets = new RuleSets();
            ruleSets.addRuleSet(working);

            PMD p = new PMD();
            p.getConfiguration().setDefaultLanguageVersion(languageVersion);
            RuleContext ctx = new RuleContext();
            long start = System.currentTimeMillis();
            Reader reader = null;
            for (DataSource dataSource: dataSources) {
            	reader = new InputStreamReader(dataSource.getInputStream());
            	ctx.setSourceCodeFilename(dataSource.getNiceFileName(false, null));
            	new SourceCodeProcessor(p.getConfiguration()).processSourceCode(reader, ruleSets, ctx);
            	IOUtil.closeQuietly(reader);
            	}
            long end = System.currentTimeMillis();
            long elapsed = end - start;
            results.add(new Result(elapsed, rule));
            if (debug) {
            	System.out.println("Done timing " + rule.getName() + "; elapsed time was " + elapsed);
            }
        }
    }

    private static final Map<String, BenchmarkResult> BenchmarksByName = new HashMap<String, BenchmarkResult>();

    public static void mark(Benchmark type, long time, long count) {
        mark(type, null, time, count);
    }

    public synchronized static void mark(Benchmark type, String name, long time, long count) {
        String typeName = type.name;
        if (typeName != null && name != null) {
            throw new IllegalArgumentException("Name cannot be given for type: " + type);
        } else if (typeName == null && name == null) {
            throw new IllegalArgumentException("Name is required for type: " + type);
        } else if (typeName == null) {
            typeName = name;
        }
        BenchmarkResult benchmarkResult = BenchmarksByName.get(typeName);
        if (benchmarkResult == null) {
            benchmarkResult = new BenchmarkResult(type, typeName);
            BenchmarksByName.put(typeName, benchmarkResult);
        }
        benchmarkResult.update(time, count);
    }

    public static void reset() {
        BenchmarksByName.clear();
    }

    public static Map<String, BenchmarkResult> values() {
    	return BenchmarksByName;
    }
}
