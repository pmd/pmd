/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetLoader;
import net.sourceforge.pmd.docs.MockedFileWriter.FileEntry;
import net.sourceforge.pmd.internal.util.IOUtil;

class RuleDocGeneratorTest {

    private MockedFileWriter writer = new MockedFileWriter();
    private Path root;

    @TempDir
    public Path folder;

    @BeforeEach
    void setup() throws IOException {
        writer.reset();

        root = Files.createTempDirectory(folder, null);
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

    private static String loadResource(String name) throws IOException {
        return MockedFileWriter.normalizeLineSeparators(
                IOUtil.readToString(RuleDocGeneratorTest.class.getResourceAsStream(name), StandardCharsets.UTF_8));
    }

    @Test
    void testSingleRuleset() throws IOException {
        RuleDocGenerator generator = new RuleDocGenerator(writer, root);

        RuleSetLoader rsl = new RuleSetLoader().includeDeprecatedRuleReferences(true);
        RuleSet ruleset = rsl.loadFromResource("rulesets/ruledoctest/sample.xml");

        generator.generate(Arrays.asList(ruleset),
                Arrays.asList(
                        "rulesets/ruledoctest/sample-deprecated.xml",
                        "rulesets/ruledoctest/other-ruleset.xml"));

        assertEquals(3, writer.getData().size());
        FileEntry languageIndex = writer.getData().get(0);
        assertTrue(IOUtil.normalizePath(languageIndex.getFilename()).endsWith(Paths.get("docs", "pages", "pmd", "rules", "java.md").toString()));
        assertEquals(loadResource("/expected/java.md"), languageIndex.getContent());

        FileEntry ruleSetIndex = writer.getData().get(1);
        assertTrue(IOUtil.normalizePath(ruleSetIndex.getFilename()).endsWith(Paths.get("docs", "pages", "pmd", "rules", "java", "sample.md").toString()));
        assertEquals(loadResource("/expected/sample.md"), ruleSetIndex.getContent());

        FileEntry sidebar = writer.getData().get(2);
        assertTrue(IOUtil.normalizePath(sidebar.getFilename()).endsWith(Paths.get("docs", "_data", "sidebars", "pmd_sidebar.yml").toString()));
        assertEquals(loadResource("/expected/pmd_sidebar.yml"), sidebar.getContent());
    }
}
