/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class GenerateRuleDocsCmd {
    private GenerateRuleDocsCmd() {
        // Utility class
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        RuleDocGenerator generator = new RuleDocGenerator();
        Path output = FileSystems.getDefault().getPath(args[0]).resolve("..").toAbsolutePath().normalize();
        System.out.println("Generating docs into " + output);
        generator.generate(output);
        System.out.println("Generated docs in " + (System.currentTimeMillis() - start) + " ms");
    }
}
