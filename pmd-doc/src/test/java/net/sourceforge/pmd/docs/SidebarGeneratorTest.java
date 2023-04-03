/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;

class SidebarGeneratorTest {
    private MockedFileWriter writer = new MockedFileWriter();

    @BeforeEach
    void setup() {
        writer.reset();
    }

    @Test
    void testSidebar() throws IOException {
        Map<Language, List<RuleSet>> rulesets = new TreeMap<>();
        RuleSet ruleSet1 = RuleSet.create("test", "test", "bestpractices.xml", Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        RuleSet ruleSet2 = RuleSet.create("test2", "test", "codestyle.xml", Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        rulesets.put(LanguageRegistry.PMD.getLanguageById("java"), Arrays.asList(ruleSet1, ruleSet2));
        rulesets.put(LanguageRegistry.PMD.getLanguageById("ecmascript"), Arrays.asList(ruleSet1));
        rulesets.put(LanguageRegistry.PMD.getLanguageById("scala"), Collections.emptyList());

        SidebarGenerator generator = new SidebarGenerator(writer, FileSystems.getDefault().getPath(".."));
        List<Map<String, Object>> result = generator.generateRuleReferenceSection(rulesets);

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
        if (SystemUtils.IS_OS_WINDOWS) {
            dumperOptions.setLineBreak(LineBreak.WIN);
        }
        String yaml = new Yaml(new SafeConstructor(new LoaderOptions()), new Representer(dumperOptions), dumperOptions).dump(result);

        String expected = MockedFileWriter.normalizeLineSeparators(
                IOUtil.readToString(SidebarGeneratorTest.class.getResourceAsStream("sidebar.yml"), StandardCharsets.UTF_8));
        assertEquals(expected, yaml);
    }
}
