/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.nio.file.FileSystems;

import org.junit.Test;

public class GenerateRuleDocsTest {
    @Test
    public void generateDocs() {
        long start = System.currentTimeMillis();
        RuleDocGenerator generator = new RuleDocGenerator();
        generator.generate(FileSystems.getDefault().getPath("..").toAbsolutePath().normalize());
        System.out.println("Generated docs in " + (System.currentTimeMillis() - start) + " ms");
    }
}
