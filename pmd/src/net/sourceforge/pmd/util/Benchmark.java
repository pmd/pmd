package net.sourceforge.pmd.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.util.datasource.DataSource;

public class Benchmark {

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
                StringBuffer out = new StringBuffer(result.rule.getName());
                while (out.length() < 48) {
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
            	p.processFile(reader, ruleSets, ctx);
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

    private static final Map<String, BenchmarkResult> NAME_TO_BENCHMARK_RESULT = new HashMap<String, BenchmarkResult>();

    public static final int TYPE_RULE = 0;
    public static final int TYPE_RULE_CHAIN_RULE = 1;
    public static final int TYPE_COLLECT_FILES = 2;
    public static final int TYPE_LOAD_RULES = 3;
    public static final int TYPE_PARSER = 4;
    public static final int TYPE_SYMBOL_TABLE = 5;
    public static final int TYPE_DFA = 6;
    public static final int TYPE_TYPE_RESOLUTION = 7;
    public static final int TYPE_RULE_CHAIN_VISIT = 8;
    public static final int TYPE_REPORTING = 9;
    private static final int TYPE_RULE_TOTAL = 10;
    private static final int TYPE_RULE_CHAIN_RULE_TOTAL = 11;
    private static final int TYPE_MEASURED_TOTAL = 12;
    private static final int TYPE_NON_MEASURED_TOTAL = 13;
    public static final int TYPE_TOTAL_PMD = 14;
    
    private static final String[] TYPE_NAMES = {
        null,
        null,
        "Collect Files",
        "Load Rules",
        "Parser",
        "Symbol Table",
        "Data Flow Analysis",
        "Type Resolution",
        "RuleChain Visit",
        "Reporting",
        "Rule Total",
        "RuleChain Rule Total",
        "Measured",
        "Non-measured",
        "Total PMD",
    };

    private static final class BenchmarkResult implements Comparable<BenchmarkResult> {
        private final int type;
        private final String name;
        private long time;
        private long count;
        public BenchmarkResult(int type, String name) {
            this.type = type;
            this.name = name;
        }
        public BenchmarkResult(int type, String name, long time, long count) {
            this.type = type;
            this.name = name;
            this.time = time;
            this.count = count;
        }
        public int getType() {
            return type;
        }
        public String getName() {
            return name;
        }
        public long getTime() {
            return time;
        }
        public long getCount() {
            return count;
        }
        public void update(long time, long count) {
            this.time += time;
            this.count += count;
        }
        public int compareTo(BenchmarkResult benchmarkResult) {
            int cmp = this.type - benchmarkResult.type;
            if (cmp == 0) {
                long delta = this.time - benchmarkResult.time;
                cmp = delta > 0 ? 1 : (delta < 0 ? -1 : 0);
            }
            return cmp;
        }
    }

    public static void mark(int type, long time, long count) {
        mark(type, null, time, count);
    }

    public synchronized static void mark(int type, String name, long time, long count) {
        String typeName = TYPE_NAMES[type];
        if (typeName != null && name != null) {
            throw new IllegalArgumentException("Name cannot be given for type: " + type);
        } else if (typeName == null && name == null) {
            throw new IllegalArgumentException("Name is required for type: " + type);
        } else if (typeName == null) {
            typeName = name;
        }
        BenchmarkResult benchmarkResult = NAME_TO_BENCHMARK_RESULT.get(typeName);
        if (benchmarkResult == null) {
            benchmarkResult = new BenchmarkResult(type, typeName);
            NAME_TO_BENCHMARK_RESULT.put(typeName, benchmarkResult);
        }
        benchmarkResult.update(time, count);
    }

    public static void reset() {
        NAME_TO_BENCHMARK_RESULT.clear();
    }

    public static String report() {
        List<BenchmarkResult> results = new ArrayList<BenchmarkResult>(NAME_TO_BENCHMARK_RESULT.values());

        long totalTime[] = new long[TYPE_TOTAL_PMD + 1];
        long totalCount[] = new long[TYPE_TOTAL_PMD + 1];
        for (BenchmarkResult benchmarkResult: results) {
            totalTime[benchmarkResult.getType()] += benchmarkResult.getTime();
            totalCount[benchmarkResult.getType()] += benchmarkResult.getCount();
            if (benchmarkResult.getType() < TYPE_MEASURED_TOTAL) {
                totalTime[TYPE_MEASURED_TOTAL] += benchmarkResult.getTime();
            }
        }
        results.add(new BenchmarkResult(TYPE_RULE_TOTAL, TYPE_NAMES[TYPE_RULE_TOTAL], totalTime[TYPE_RULE], 0));
        results.add(new BenchmarkResult(TYPE_RULE_CHAIN_RULE_TOTAL, TYPE_NAMES[TYPE_RULE_CHAIN_RULE_TOTAL], totalTime[TYPE_RULE_CHAIN_RULE], 0));
        results.add(new BenchmarkResult(TYPE_MEASURED_TOTAL, TYPE_NAMES[TYPE_MEASURED_TOTAL], totalTime[TYPE_MEASURED_TOTAL], 0));
        results.add(new BenchmarkResult(TYPE_NON_MEASURED_TOTAL, TYPE_NAMES[TYPE_NON_MEASURED_TOTAL], totalTime[TYPE_TOTAL_PMD] - totalTime[TYPE_MEASURED_TOTAL], 0));
        Collections.sort(results);

        StringBuffer buf = new StringBuffer();
        boolean writeRuleHeader = true;
        boolean writeRuleChainRuleHeader = true;
        long ruleCount = 0;
        long ruleChainCount = 0;
        for (BenchmarkResult benchmarkResult: results) {
            StringBuffer buf2 = new StringBuffer();
            buf2.append(benchmarkResult.getName());
            buf2.append(':');
            while (buf2.length() <= 50) {
                buf2.append(' ');
            }
            buf2.append(StringUtil.lpad(MessageFormat.format("{0,number,0.000}", Double.valueOf(benchmarkResult.getTime()/1000000000.0)), 8));
            if (benchmarkResult.getType() <= TYPE_RULE_CHAIN_RULE) {
                buf2.append(StringUtil.lpad(MessageFormat.format("{0,number,###,###,###,###,###}", benchmarkResult.getCount()), 20));
            }
            switch (benchmarkResult.getType()) {
                case TYPE_RULE:
                    if (writeRuleHeader) {
                        writeRuleHeader = false;
                        buf.append(PMD.EOL);
                        buf.append("---------------------------------<<< Rules >>>---------------------------------" + PMD.EOL);
                        buf.append("Rule name                                       Time (secs)    # of Evaluations" + PMD.EOL);
                        buf.append(PMD.EOL);
                    }
                    ruleCount++;
                    break;
                case TYPE_RULE_CHAIN_RULE:
                    if (writeRuleChainRuleHeader) {
                        writeRuleChainRuleHeader = false;
                        buf.append(PMD.EOL);
                        buf.append("----------------------------<<< RuleChain Rules >>>----------------------------" + PMD.EOL);
                        buf.append("Rule name                                       Time (secs)         # of Visits" + PMD.EOL);
                        buf.append(PMD.EOL);
                    }
                    ruleChainCount++;
                    break;
                case TYPE_COLLECT_FILES:
                    buf.append(PMD.EOL);
                    buf.append("--------------------------------<<< Summary >>>--------------------------------" + PMD.EOL);
                    buf.append("Segment                                         Time (secs)" + PMD.EOL);
                    buf.append(PMD.EOL);
                    break;
                case TYPE_MEASURED_TOTAL:
                    String s = MessageFormat.format("{0,number,###,###,###,###,###}", ruleCount);
                    buf.append("Rule Average (" + s + " rules):" + StringUtil.lpad(MessageFormat.format("{0,number,0.000}", ruleCount==0?0:totalTime[TYPE_RULE]/1000000000.0d/ruleCount), 37-s.length()) + PMD.EOL);
                    s = MessageFormat.format("{0,number,###,###,###,###,###}", ruleChainCount);
                    buf.append("RuleChain Average (" + s + " rules):" + StringUtil.lpad(MessageFormat.format("{0,number,0.000}", ruleChainCount==0?0:totalTime[TYPE_RULE_CHAIN_RULE]/1000000000.0d/ruleChainCount), 32-s.length()) + PMD.EOL);

                    buf.append(PMD.EOL);
                    buf.append("-----------------------------<<< Final Summary >>>-----------------------------" + PMD.EOL);
                    buf.append("Total                                           Time (secs)" + PMD.EOL);
                    buf.append(PMD.EOL);
                    break;
                default:
                    // Do nothing
                    break;
            }
            buf.append(buf2.toString());
            buf.append(PMD.EOL);
        }
        return buf.toString();
    }
}
