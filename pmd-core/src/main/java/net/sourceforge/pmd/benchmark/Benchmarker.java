/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.benchmark;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * @deprecated use {@link TimeTracker} instead
 */
@Deprecated
public final class Benchmarker {

    private static final Map<String, BenchmarkResult> BENCHMARKS_BY_NAME = new HashMap<>();

    private Benchmarker() { }

    /**
     * @param args
     *            String[]
     * @param name
     *            String
     * @return boolean
     */
    private static boolean findBooleanSwitch(String[] args, String name) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param args
     *            String[]
     * @param name
     *            String
     * @param defaultValue
     *            String
     * @return String
     */
    private static String findOptionalStringValue(String[] args, String name, String defaultValue) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(name)) {
                return args[i + 1];
            }
        }
        return defaultValue;
    }

    /**
     *
     * @param args
     *            String[]
     * @throws RuleSetNotFoundException
     * @throws IOException
     * @throws PMDException
     */
    public static void main(String[] args) throws RuleSetNotFoundException, IOException, PMDException {

        String targetjdk = findOptionalStringValue(args, "--targetjdk", "1.4");
        Language language = LanguageRegistry.getLanguage("Java");
        LanguageVersion languageVersion = language.getVersion(targetjdk);
        if (languageVersion == null) {
            languageVersion = language.getDefaultVersion();
        }

        String srcDir = findOptionalStringValue(args, "--source-directory", "/usr/local/java/src/java/lang/");
        List<DataSource> dataSources = FileUtil.collectFiles(srcDir, new LanguageFilenameFilter(language));

        boolean debug = findBooleanSwitch(args, "--debug");
        boolean parseOnly = findBooleanSwitch(args, "--parse-only");

        if (debug) {
            System.out.println("Using " + language.getName() + " " + languageVersion.getVersion());
        }
        if (parseOnly) {
            Parser parser = PMD.parserFor(languageVersion, null);
            parseStress(parser, dataSources, debug);
        } else {
            String ruleset = findOptionalStringValue(args, "--ruleset", "");
            if (debug) {
                System.out.println("Checking directory " + srcDir);
            }
            Set<RuleDuration> results = new TreeSet<>();
            RuleSetFactory factory = RulesetsFactoryUtils.defaultFactory();
            if (StringUtils.isNotBlank(ruleset)) {
                stress(languageVersion, factory.createRuleSet(ruleset), dataSources, results, debug);
            } else {
                Iterator<RuleSet> i = factory.getRegisteredRuleSets();
                while (i.hasNext()) {
                    stress(languageVersion, i.next(), dataSources, results, debug);
                }
            }

            TextReport report = new TextReport();
            report.generate(results, System.err);
        }
    }

    /**
     * @param parser
     *            Parser
     * @param dataSources
     *            List<DataSource>
     * @param debug
     *            boolean
     * @throws IOException
     */
    private static void parseStress(Parser parser, List<DataSource> dataSources, boolean debug) throws IOException {

        long start = System.currentTimeMillis();

        for (DataSource ds : dataSources) {
            try (DataSource dataSource = ds; InputStreamReader reader = new InputStreamReader(dataSource.getInputStream())) {
                parser.parse(dataSource.getNiceFileName(false, null), reader);
            }
        }

        if (debug) {
            long end = System.currentTimeMillis();
            long elapsed = end - start;
            System.out.println("That took " + elapsed + " ms");
        }
    }

    /**
     * @param languageVersion
     *            LanguageVersion
     * @param ruleSet
     *            RuleSet
     * @param dataSources
     *            List<DataSource>
     * @param results
     *            Set<RuleDuration>
     * @param debug
     *            boolean
     * @throws PMDException
     * @throws IOException
     */
    private static void stress(LanguageVersion languageVersion, RuleSet ruleSet, List<DataSource> dataSources,
            Set<RuleDuration> results, boolean debug) throws PMDException, IOException {

        final RuleSetFactory factory = RulesetsFactoryUtils.defaultFactory();
        for (Rule rule: ruleSet.getRules()) {
            if (debug) {
                System.out.println("Starting " + rule.getName());
            }

            final RuleSet working = factory.createSingleRuleRuleSet(rule);
            RuleSets ruleSets = new RuleSets(working);

            PMDConfiguration config = new PMDConfiguration();
            config.setDefaultLanguageVersion(languageVersion);

            RuleContext ctx = new RuleContext();
            long start = System.currentTimeMillis();
            for (DataSource ds : dataSources) {
                try (DataSource dataSource = ds; InputStream stream = new BufferedInputStream(dataSource.getInputStream())) {
                    ctx.setSourceCodeFile(new File(dataSource.getNiceFileName(false, null)));
                    new SourceCodeProcessor(config).processSourceCode(stream, ruleSets, ctx);
                }
            }
            long end = System.currentTimeMillis();
            long elapsed = end - start;
            results.add(new RuleDuration(elapsed, rule));
            if (debug) {
                System.out.println("Done timing " + rule.getName() + "; elapsed time was " + elapsed);
            }
        }
    }

    /**
     * @param type
     *            Benchmark
     * @param time
     *            long
     * @param count
     *            long
     */
    public static void mark(Benchmark type, long time, long count) {
        mark(type, null, time, count);
    }

    /**
     *
     * @param type
     *            Benchmark
     * @param name
     *            String
     * @param time
     *            long
     * @param count
     *            long
     */
    public static synchronized void mark(Benchmark type, String name, long time, long count) {
        String typeName = type.name;
        if (typeName != null && name != null) {
            throw new IllegalArgumentException("Name cannot be given for type: " + type);
        } else if (typeName == null && name == null) {
            throw new IllegalArgumentException("Name is required for type: " + type);
        } else if (typeName == null) {
            typeName = name;
        }
        BenchmarkResult benchmarkResult = BENCHMARKS_BY_NAME.get(typeName);
        if (benchmarkResult == null) {
            benchmarkResult = new BenchmarkResult(type, typeName);
            BENCHMARKS_BY_NAME.put(typeName, benchmarkResult);
        }
        benchmarkResult.update(time, count);
    }

    public static void reset() {
        BENCHMARKS_BY_NAME.clear();
    }

    public static Map<String, BenchmarkResult> values() {
        return BENCHMARKS_BY_NAME;
    }
}
