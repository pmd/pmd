/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

public class GenerateRuleDocsCmd {
    private GenerateRuleDocsCmd() {
        // Utility class
    }

    public static void main(String[] args) throws RuleSetNotFoundException {
        long start = System.currentTimeMillis();
        Path output = FileSystems.getDefault().getPath(args[0]).resolve("..").toAbsolutePath().normalize();
        System.out.println("Generating docs into " + output);
        RuleDocGenerator generator = new RuleDocGenerator(new DefaultFileWriter(), output);

        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        Iterator<RuleSet> registeredRuleSets = ruleSetFactory.getRegisteredRuleSets();

        generator.generate(registeredRuleSets);
        System.out.println("Generated docs in " + (System.currentTimeMillis() - start) + " ms");
    }
}
