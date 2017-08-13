/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.docs.MockedFileWriter.FileEntry;

public class RuleDocGeneratorTest {

    private MockedFileWriter writer = new MockedFileWriter();
    private Path root;

    @Before
    public void setup() throws IOException {
        writer.reset();

        root = Files.createTempDirectory("pmd-ruledocgenerator-test");
        Files.createDirectory(root.resolve("docs"));
    }

    @After
    public void cleanup() throws IOException {
        Files.delete(root.resolve("docs"));
        Files.delete(root);
    }

    @Test
    public void testSingleRuleset() throws RuleSetNotFoundException, IOException {
        RuleDocGenerator generator = new RuleDocGenerator(writer, root);
        
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet ruleset = rsf.createRuleSet("rulesets/ruledoctest/sample.xml");
        
        generator.generate(Arrays.asList(ruleset).iterator());

        assertEquals(2, writer.getData().size());
        FileEntry languageIndex = writer.getData().get(0);
        assertTrue(languageIndex.getFilename().endsWith("docs/pages/pmd/rules/java.md"));
        assertEquals(IOUtils.toString(RuleDocGeneratorTest.class.getResourceAsStream("/expected/java.md")),
                languageIndex.getContent());
        
        FileEntry ruleSetIndex = writer.getData().get(1);
        assertTrue(ruleSetIndex.getFilename().endsWith("docs/pages/pmd/rules/java/sample.md"));
        assertEquals(IOUtils.toString(RuleDocGeneratorTest.class.getResourceAsStream("/expected/sample.md")),
                ruleSetIndex.getContent());
    }
}
