/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.SystemUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.Yaml;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.lang.Language;

public class SidebarGenerator {
    private static final String SIDEBAR_YML = "docs/_data/sidebars/pmd_sidebar.yml";

    private final FileWriter writer;
    private final Path sidebarPath;

    public SidebarGenerator(FileWriter writer, Path basePath) {
        this.writer = Objects.requireNonNull(writer, "A file writer must be provided");
        this.sidebarPath = Objects.requireNonNull(basePath, "A base directory must be provided").resolve(SIDEBAR_YML);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractRuleReference(Map<String, Object> sidebar) {
        List<Map<String, Object>> entries = (List<Map<String, Object>>) sidebar.get("entries");
        Map<String, Object> entry = entries.get(0);
        List<Map<String, Object>> folders = (List<Map<String, Object>>) entry.get("folders");
        return folders.get(3);
    }

    public void generateSidebar(Map<Language, List<RuleSet>> sortedRulesets) throws IOException {
        Map<String, Object> sidebar = loadSidebar();
        Map<String, Object> ruleReference = extractRuleReference(sidebar);
        ruleReference.put("folderitems", generateRuleReferenceSection(sortedRulesets));
        writeSidebar(sidebar);
    }

    List<Map<String, Object>> generateRuleReferenceSection(Map<Language, List<RuleSet>> sortedRulesets) {
        List<Map<String, Object>> newFolderItems = new ArrayList<>();
        for (Map.Entry<Language, List<RuleSet>> entry : sortedRulesets.entrySet()) {
            Map<String, Object> newFolderItem = new LinkedHashMap<>();
            newFolderItem.put("title", null);
            newFolderItem.put("output", "web, pdf");

            Map<String, Object> subfolder = new LinkedHashMap<>();
            newFolderItem.put("subfolders", Arrays.asList(subfolder));

            subfolder.put("title", entry.getKey().getName() + " Rules");
            subfolder.put("output", "web, pdf");
            List<Map<String, Object>> subfolderitems = new ArrayList<>();
            subfolder.put("subfolderitems", subfolderitems);

            Map<String, Object> ruleIndexSubfolderItem = new LinkedHashMap<>();
            ruleIndexSubfolderItem.put("title", "Index");
            ruleIndexSubfolderItem.put("output", "web, pdf");
            ruleIndexSubfolderItem.put("url", "/pmd_rules_" + entry.getKey().getTerseName() + ".html");
            subfolderitems.add(ruleIndexSubfolderItem);

            for (RuleSet ruleset : entry.getValue()) {
                Map<String, Object> subfolderitem = new LinkedHashMap<>();
                subfolderitem.put("title", ruleset.getName());
                subfolderitem.put("output", "web, pdf");
                subfolderitem.put("url", "/pmd_rules_" + entry.getKey().getTerseName() + "_" + RuleSetUtils.getRuleSetFilename(ruleset) + ".html");
                subfolderitems.add(subfolderitem);
            }

            newFolderItems.add(newFolderItem);
        }
        return newFolderItems;
    }

    public Map<String, Object> loadSidebar() throws IOException {
        try (Reader reader = Files.newBufferedReader(sidebarPath, StandardCharsets.UTF_8)) {
            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String, Object> sidebar = (Map<String, Object>) yaml.load(reader);
            return sidebar;
        }
    }

    public void writeSidebar(Map<String, Object> sidebar) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        if (SystemUtils.IS_OS_WINDOWS) {
            options.setLineBreak(LineBreak.WIN);
        }
        Yaml yaml = new Yaml(options);
        writer.write(sidebarPath, Arrays.asList(yaml.dump(sidebar)));
        System.out.println("Generated " + sidebarPath);
    }
}
