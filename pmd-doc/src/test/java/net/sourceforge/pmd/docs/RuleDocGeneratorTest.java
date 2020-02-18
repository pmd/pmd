/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.docs.MockedFileWriter.FileEntry;

public class RuleDocGeneratorTest {

    private MockedFileWriter writer = new MockedFileWriter();
    private Path root;

    @Before
    public void setup() throws IOException {
        writer.reset();

        root = Files.createTempDirectory("pmd-ruledocgenerator-test");
        Files.createDirectories(root.resolve("docs/_data/sidebars"));
        List<String> mockedSidebar = Arrays.asList(
                "entries:",
                "- title: sidebar",
                "  folders:",
                "  - title: 1",
                "  - title: 2",
                "  - title: 3",
                "  - title: Rules");
        Files.write(root.resolve("docs/_data/sidebars/pmd_sidebar.yml"), mockedSidebar);
    }

    @After
    public void cleanup() throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static String loadResource(String name) throws IOException {
        return MockedFileWriter.normalizeLineSeparators(
                IOUtils.toString(RuleDocGeneratorTest.class.getResourceAsStream(name), StandardCharsets.UTF_8));
    }

    @Test
    public void testSingleRuleset() throws RuleSetNotFoundException, IOException {
        RuleDocGenerator generator = new RuleDocGenerator(writer, root);

        RuleSetFactory rsf = RulesetsFactoryUtils.defaultFactory();
        RuleSet ruleset = rsf.createRuleSet("rulesets/ruledoctest/sample.xml");

        generator.generate(Arrays.asList(ruleset).iterator(),
                Arrays.asList(
                        "rulesets/ruledoctest/sample-deprecated.xml",
                        "rulesets/ruledoctest/other-ruleset.xml"));

        assertEquals(3, writer.getData().size());
        FileEntry languageIndex = writer.getData().get(0);
        assertTrue(FilenameUtils.normalize(languageIndex.getFilename(), true).endsWith("docs/pages/pmd/rules/java.md"));
        assertEquals(loadResource("/expected/java.md"), languageIndex.getContent());

        FileEntry ruleSetIndex = writer.getData().get(1);
        assertTrue(FilenameUtils.normalize(ruleSetIndex.getFilename(), true).endsWith("docs/pages/pmd/rules/java/sample.md"));
        assertEquals(loadResource("/expected/sample.md"), ruleSetIndex.getContent());

        FileEntry sidebar = writer.getData().get(2);
        assertTrue(FilenameUtils.normalize(sidebar.getFilename(), true).endsWith("docs/_data/sidebars/pmd_sidebar.yml"));
        assertEquals(loadResource("/expected/pmd_sidebar.yml"), sidebar.getContent());
    }
}
