package net.sourceforge.pmd.util;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.SimpleRuleSetNameMapper;
import net.sourceforge.pmd.SourceFileSelector;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDKVersion;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.cpd.FileFinder;
import net.sourceforge.pmd.cpd.SourceFileOrDirectoryFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Collection;

public class Benchmark {

    private static class Result implements Comparable {
        public Rule rule;
        public long time;

        public int compareTo(Object o) {
            Result other = (Result)o;
            if (other.time < time) {
                return -1;
            } else if (other.time > time) {
                return 1;
            }

            return rule.getName().compareTo(((Result)o).rule.getName());
        }

        public Result(long elapsed, Rule rule) {
            this.rule = rule;
            this.time = elapsed;
        }
    }

    private static boolean findBooleanSwitch(String[] args, String name) {
        for (int i=0; i<args.length; i++) {
            if (args[i].equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static String findOptionalStringValue(String[] args, String name, String defaultValue) {
        for (int i=0; i<args.length; i++) {
            if (args[i].equals(name)) {
                return args[i+1];
            }
        }
        return defaultValue;
    }

    public static void main(String[] args) throws RuleSetNotFoundException, IOException, PMDException {

        String srcDir = findOptionalStringValue(args, "--source-directory", "/usr/local/java/src/java/lang/");
        List files = new FileFinder().findFilesFrom(srcDir, new SourceFileOrDirectoryFilter(new SourceFileSelector()), true);
        boolean debug = findBooleanSwitch(args, "--debug");
        boolean parseOnly = findBooleanSwitch(args, "--parse-only");

        if (parseOnly) {
            parseStress(files);
        } else {
            String ruleset = findOptionalStringValue(args, "--ruleset", "");
            if (debug) System.out.println("Checking directory " + srcDir);
            Set results = new TreeSet();
            RuleSetFactory factory = new RuleSetFactory();
            if (ruleset.length() > 0) {
                SimpleRuleSetNameMapper mapper = new SimpleRuleSetNameMapper(ruleset);
                stress(factory.createRuleSet(mapper.getRuleSets()), files, results, debug);
            } else {
                Iterator i = factory.getRegisteredRuleSets();
                while (i.hasNext()) {
                    stress((RuleSet)i.next(), files, results, debug);
                }
            }
            System.out.println("=========================================================");
            System.out.println("Rule\t\t\t\t\t\tTime in ms");
            System.out.println("=========================================================");
            for (Iterator j = results.iterator(); j.hasNext();) {
                Result result = (Result)j.next();
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

    private static void parseStress(List files) throws FileNotFoundException {
        TargetJDKVersion jdk = new TargetJDK1_4();
        long start = System.currentTimeMillis();
        for (Iterator k = files.iterator(); k.hasNext();) {
            File file = (File)k.next();
            JavaParser parser = jdk.createParser(new FileReader(file));
            parser.CompilationUnit();
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("That took " + elapsed + " ms");
    }

    private static void stress(RuleSet ruleSet, List files, Set results, boolean debug) throws PMDException, IOException {
        Collection rules = ruleSet.getRules();
        for (Iterator j = rules.iterator(); j.hasNext();) {
            Rule rule = (Rule)j.next();
            if (debug) System.out.println("Starting " + rule.getName());

            RuleSet working = new RuleSet();
            working.addRule(rule);

            PMD p = new PMD();
            RuleContext ctx = new RuleContext();
            long start = System.currentTimeMillis();
            for (Iterator k = files.iterator(); k.hasNext();) {
                File file = (File)k.next();
                FileReader reader = new FileReader(file);
                ctx.setSourceCodeFilename(file.getName());
                p.processFile(reader, working, ctx);
                reader.close();
            }
            long end = System.currentTimeMillis();
            long elapsed = end - start;
            results.add(new Result(elapsed, rule));
            if (debug) System.out.println("Done timing " + rule.getName() + "; elapsed time was " + elapsed);
        }
    }
}
