package net.sourceforge.pmd.util;

import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.cpd.FileFinder;
import net.sourceforge.pmd.cpd.JavaLanguage;

import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

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
            return 0;
        }

        public Result(long elapsed, Rule rule) {
            this.rule = rule;
            this.time = elapsed;
        }
    }

    private static final String JAVA_SRC_DIR = "/usr/local/java/src/java/lang/";
    private static final boolean DEBUG = true;

    public static void main(String[] args) throws RuleSetNotFoundException, IOException, PMDException {
        Set results = new TreeSet();

        FileFinder finder = new FileFinder();
        List files = finder.findFilesFrom(JAVA_SRC_DIR, new JavaLanguage.JavaFileOrDirectoryFilter(), true);

        RuleSetFactory factory = new RuleSetFactory();
        Iterator i = factory.getRegisteredRuleSets();
        while (i.hasNext()) {
            RuleSet ruleSet = (RuleSet)i.next();
            Set rules = ruleSet.getRules();
            for (Iterator j = rules.iterator(); j.hasNext();) {
                Rule rule = (Rule)j.next();
                if (DEBUG) System.out.println("Starting " + rule.getName());
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
                if (DEBUG) System.out.println("Done timing " + rule.getName() + "; elapsed time was " + elapsed);
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
        System.out.println("=========================================================");
    }
}
