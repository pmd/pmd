/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.rule.XPathRule;

public class RuleDocGenerator {
    private static final String LANGUAGE_INDEX_FILENAME_PATTERN = "docs/pages/pmd/rules/${language.tersename}.md";
    private static final String LANGUAGE_INDEX_PERMALINK_PATTERN = "pmd_rules_${language.tersename}.html";
    private static final String RULESET_INDEX_FILENAME_PATTERN = "docs/pages/pmd/rules/${language.tersename}/${ruleset.name}.md";
    private static final String RULESET_INDEX_PERMALINK_PATTERN = "pmd_rules_${language.tersename}_${ruleset.name}.html";

    private Path root;

    public void generate(Path root) {
        this.root = Objects.requireNonNull(root, "Root directory must be provided");

        Path docsDir = root.resolve("docs");
        if (!Files.exists(docsDir) || !Files.isDirectory(docsDir)) {
            throw new IllegalArgumentException("Couldn't find \"docs\" subdirectory");
        }

        Map<Language, List<RuleSet>> rulesets;
        try {
            rulesets = loadAndSortRulesets();
            generateLanguageIndex(rulesets);
            generateRuleSetIndex(rulesets);

        } catch (RuleSetNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getAbsoluteOutputPath(String filename) {
        return root.resolve(FilenameUtils.normalize(filename));
    }

    private Map<Language, List<RuleSet>> loadAndSortRulesets() throws RuleSetNotFoundException {
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        Iterator<RuleSet> registeredRuleSets = ruleSetFactory.getRegisteredRuleSets();

        Map<Language, List<RuleSet>> rulesets = new HashMap<>();

        while (registeredRuleSets.hasNext()) {
            RuleSet ruleset = registeredRuleSets.next();
            Language language = getRuleSetLanguage(ruleset);

            if (!rulesets.containsKey(language)) {
                rulesets.put(language, new ArrayList<RuleSet>());
            }
            rulesets.get(language).add(ruleset);
        }

        for (List<RuleSet> rulesetsOfOneLanguage : rulesets.values()) {
            Collections.sort(rulesetsOfOneLanguage, new Comparator<RuleSet>() {
                @Override
                public int compare(RuleSet o1, RuleSet o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
        }
        return rulesets;
    }

    /**
     * Rulesets could potentially contain rules from various languages.
     * But for built-in rulesets, all rules within one ruleset belong to
     * one language. So we take the language of the first rule.
     * @param ruleset
     * @return the terse name of the ruleset's language
     */
    private static Language getRuleSetLanguage(RuleSet ruleset) {
        Collection<Rule> rules = ruleset.getRules();
        if (rules.isEmpty()) {
            throw new RuntimeException("Ruleset " + ruleset.getFileName() + " is empty!");
        }
        return rules.iterator().next().getLanguage();
    }

    /**
     * Writes for each language an index file, which lists the rulesets, the rules
     * and links to the ruleset pages.
     * @param rulesets all rulesets
     * @throws IOException
     */
    private void generateLanguageIndex(Map<Language, List<RuleSet>> rulesets) throws IOException {
        for (Map.Entry<Language, List<RuleSet>> entry : rulesets.entrySet()) {
            String languageTersename = entry.getKey().getTerseName();
            String filename = LANGUAGE_INDEX_FILENAME_PATTERN
                    .replace("${language.tersename}", languageTersename);
            Path path = getAbsoluteOutputPath(filename);

            List<String> lines = new LinkedList<>();
            lines.add("---");
            lines.add("title: " + entry.getKey().getName() + " Rules");
            lines.add("permalink: " + LANGUAGE_INDEX_PERMALINK_PATTERN.replace("${language.tersename}", languageTersename));
            lines.add("folder: pmd/rules");
            lines.add("---");

            lines.add("List of rulesets and rules contained in each ruleset.");
            lines.add("");

            for (RuleSet ruleset : entry.getValue()) {
                String link = RULESET_INDEX_PERMALINK_PATTERN
                        .replace("${language.tersename}", languageTersename)
                        .replace("${ruleset.name}", getRuleSetFilename(ruleset));
                lines.add("*   [" + ruleset.getName() + "](" + link + "): " + getRuleSetDescriptionSingleLine(ruleset));
            }
            lines.add("");

            for (RuleSet ruleset : entry.getValue()) {
                lines.add("## " + ruleset.getName());

                for (Rule rule : getSortedRules(ruleset)) {
                    String link = RULESET_INDEX_PERMALINK_PATTERN
                            .replace("${language.tersename}", languageTersename)
                            .replace("${ruleset.name}", getRuleSetFilename(ruleset));
                    link += "#" + rule.getName().toLowerCase(Locale.ROOT);
                    lines.add("*   [" + rule.getName() + "](" + link + "): " + getShortRuleDescription(rule));
                }
                lines.add("");
            }

            System.out.println("Generated " + path);
            Files.write(path, lines, StandardCharsets.UTF_8);
        }
    }

    /**
     * Shortens and escapes (for markdown) some special characters. Otherwise the shortened text
     * could contain some unfinished sequences.
     * @param rule
     * @return
     */
    private static String getShortRuleDescription(Rule rule) {
        return StringUtils.abbreviate(
                StringUtils.stripToEmpty(rule.getDescription().replaceAll("\n|\r", "")
                        .replaceAll("\\|", "\\\\|")
                        .replaceAll("`", "'")
                        .replaceAll("\\*", "")), 100);
    }
    
    /**
     * Gets the sanitized base name of the ruleset.
     * For some reason, the filename might contain some newlines, which are removed.
     * @param ruleset
     * @return
     */
    private static String getRuleSetFilename(RuleSet ruleset) {
        return FilenameUtils.getBaseName(StringUtils.chomp(ruleset.getFileName()));
    }

    private static String getRuleSetDescriptionSingleLine(RuleSet ruleset) {
        String description = ruleset.getDescription();
        description = description.replaceAll("\\n|\\r", " ");
        description = StringUtils.stripToEmpty(description);
        return description;
    }

    /**
     * Generates for each ruleset a page. The page contains the details for each rule.
     *
     * @param rulesets all rulesets
     * @throws IOException
     */
    private void generateRuleSetIndex(Map<Language, List<RuleSet>> rulesets) throws IOException {
        for (Map.Entry<Language, List<RuleSet>> entry : rulesets.entrySet()) {
            String languageTersename = entry.getKey().getTerseName();
            for (RuleSet ruleset : entry.getValue()) {
                String filename = RULESET_INDEX_FILENAME_PATTERN
                    .replace("${language.tersename}", languageTersename)
                    .replace("${ruleset.name}", getRuleSetFilename(ruleset));

                Path path = getAbsoluteOutputPath(filename);

                String permalink = RULESET_INDEX_PERMALINK_PATTERN
                        .replace("${language.tersename}", languageTersename)
                        .replace("${ruleset.name}", getRuleSetFilename(ruleset));

                List<String> lines = new LinkedList<>();
                lines.add("---");
                lines.add("title: " + ruleset.getName());
                lines.add("summary: " + getRuleSetDescriptionSingleLine(ruleset));
                lines.add("permalink: " + permalink);
                lines.add("folder: pmd/rules/" + languageTersename);
                lines.add("sidebaractiveurl: /" + LANGUAGE_INDEX_PERMALINK_PATTERN.replace("${language.tersename}", languageTersename));
                lines.add("editmepath: ../" + getRuleSetSourceFilepath(ruleset));
                lines.add("---");

                for (Rule rule : getSortedRules(ruleset)) {
                    lines.add("## " + rule.getName());
                    if (rule.getSince() != null) {
                        lines.add("**Since:** " + rule.getSince());
                        lines.add("");
                    }
                    lines.add("**Priority:** " + rule.getPriority() + " (" + rule.getPriority().getPriority() + ")");
                    lines.add("");

                    lines.add(StringUtils.stripToEmpty(rule.getDescription()));
                    lines.add("");
                    if (!rule.getExamples().isEmpty()) {
                        lines.add("**Example(s):**");
                        for (String example : rule.getExamples()) {
                            lines.add("```");
                            lines.add(StringUtils.stripToEmpty(example));
                            lines.add("```");
                            lines.add("");
                        }
                    }

                    List<PropertyDescriptor<?>> properties = new ArrayList<>(rule.getPropertyDescriptors());
                    // filter out standard properties
                    properties.remove(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR);
                    properties.remove(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR);
                    properties.remove(XPathRule.XPATH_DESCRIPTOR);
                    properties.remove(XPathRule.VERSION_DESCRIPTOR);

                    if (!properties.isEmpty()) {
                        lines.add("**This rule has the following properties:**");
                        lines.add("");
                        lines.add("|Name|Default Value|Description|");
                        lines.add("|----|-------------|-----------|");
                        for (PropertyDescriptor<?> propertyDescriptor : properties) {
                            lines.add("|" + propertyDescriptor.name()
                                + "|" + (propertyDescriptor.defaultValue() != null ? String.valueOf(propertyDescriptor.defaultValue()) : "")
                                + "|" + propertyDescriptor.description()
                                + "|");
                        }
                        lines.add("");
                    }
                }

                Files.createDirectories(path.getParent());
                Files.write(path, lines, StandardCharsets.UTF_8);
                System.out.println("Generated " + path);
            }
        }
    }

    private List<Rule> getSortedRules(RuleSet ruleset) {
        List<Rule> sortedRules = new ArrayList<>(ruleset.getRules());
        Collections.sort(sortedRules, new Comparator<Rule>() {
            @Override
            public int compare(Rule o1, Rule o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return sortedRules;
    }

    /**
     * Searches for the source file of the given ruleset. This provides the information
     * for the "editme" link.
     *
     * @param ruleset the ruleset to search for.
     * @return
     * @throws IOException
     */
    private static String getRuleSetSourceFilepath(RuleSet ruleset) throws IOException {
        Path root = FileSystems.getDefault().getPath("..").toAbsolutePath().normalize();
        final String rulesetFilename = FilenameUtils.normalize(StringUtils.chomp(ruleset.getFileName()));
        final List<Path> foundPathResult = new LinkedList<>();

        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String path = file.toString();
                if (path.contains("src") && path.endsWith(rulesetFilename)) {
                    foundPathResult.add(file);
                    return FileVisitResult.TERMINATE;
                }
                return super.visitFile(file, attrs);
            }
        });
        
        if (!foundPathResult.isEmpty()) {
            Path foundPath = foundPathResult.get(0);
            foundPath = root.relativize(foundPath);
            return foundPath.toString();
        }

        return StringUtils.chomp(ruleset.getFileName());
    }
}
