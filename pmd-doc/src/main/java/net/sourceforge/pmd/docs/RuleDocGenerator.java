/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.io.File;
import java.io.IOException;
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
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.XPathRule;

public class RuleDocGenerator {
    private static final String LANGUAGE_INDEX_FILENAME_PATTERN = "docs/pages/pmd/rules/${language.tersename}.md";
    private static final String LANGUAGE_INDEX_PERMALINK_PATTERN = "pmd_rules_${language.tersename}.html";
    private static final String RULESET_INDEX_FILENAME_PATTERN = "docs/pages/pmd/rules/${language.tersename}/${ruleset.name}.md";
    private static final String RULESET_INDEX_PERMALINK_PATTERN = "pmd_rules_${language.tersename}_${ruleset.name}.html";

    private static final String DEPRECATION_LABEL_SMALL = "<span style=\"border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f; font-size: 75%;\">Deprecated</span> ";
    private static final String DEPRECATION_LABEL = "<span style=\"border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;\">Deprecated</span> ";

    private static final String GITHUB_SOURCE_LINK = "https://github.com/pmd/pmd/blob/master/";

    private final Path root;
    private final FileWriter writer;

    /** Maintains mapping from pmd terse language name to rouge highlighter language */
    private static final Map<String, String> LANGUAGE_HIGHLIGHT_MAPPER = new HashMap<>();

    static {
        LANGUAGE_HIGHLIGHT_MAPPER.put("ecmascript", "javascript");
        LANGUAGE_HIGHLIGHT_MAPPER.put("pom", "xml");
        LANGUAGE_HIGHLIGHT_MAPPER.put("apex", "java");
        LANGUAGE_HIGHLIGHT_MAPPER.put("plsql", "sql");
    }

    public RuleDocGenerator(FileWriter writer, Path root) {
        this.root = Objects.requireNonNull(root, "Root directory must be provided");
        this.writer = Objects.requireNonNull(writer, "A file writer must be provided");

        Path docsDir = root.resolve("docs");
        if (!Files.exists(docsDir) || !Files.isDirectory(docsDir)) {
            throw new IllegalArgumentException("Couldn't find \"docs\" subdirectory");
        }
    }

    public void generate(Iterator<RuleSet> rulesets) {
        Map<Language, List<RuleSet>> sortedRulesets;
        try {
            sortedRulesets = sortRulesets(rulesets);
            generateLanguageIndex(sortedRulesets);
            generateRuleSetIndex(sortedRulesets);

        } catch (RuleSetNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getAbsoluteOutputPath(String filename) {
        return root.resolve(FilenameUtils.normalize(filename));
    }

    private Map<Language, List<RuleSet>> sortRulesets(Iterator<RuleSet> rulesets) throws RuleSetNotFoundException {
        Map<Language, List<RuleSet>> rulesetsByLanguage = new HashMap<>();

        while (rulesets.hasNext()) {
            RuleSet ruleset = rulesets.next();
            Language language = getRuleSetLanguage(ruleset);

            if (!rulesetsByLanguage.containsKey(language)) {
                rulesetsByLanguage.put(language, new ArrayList<RuleSet>());
            }
            rulesetsByLanguage.get(language).add(ruleset);
        }

        for (List<RuleSet> rulesetsOfOneLanguage : rulesetsByLanguage.values()) {
            Collections.sort(rulesetsOfOneLanguage, new Comparator<RuleSet>() {
                @Override
                public int compare(RuleSet o1, RuleSet o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
        }
        return rulesetsByLanguage;
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
                    if (rule instanceof RuleReference) {
                        RuleReference ref = (RuleReference) rule;
                        if (ruleset.getFileName().equals(ref.getRuleSetReference().getRuleSetFileName())) {
                            // rule renamed within same ruleset
                            lines.add("*   [" + rule.getName() + "](" + link + "#" + rule.getName().toLowerCase(Locale.ROOT) + "): "
                                    + DEPRECATION_LABEL_SMALL
                                    + "The rule has been renamed. Use instead "
                                    + "[" + ref.getRule().getName() + "](" + link + "#" + ref.getRule().getName().toLowerCase(Locale.ROOT) + ").");
                        } else {
                            // rule moved to another ruleset...
                            String otherLink = RULESET_INDEX_PERMALINK_PATTERN
                                    .replace("${language.tersename}", languageTersename)
                                    .replace("${ruleset.name}", getRuleSetFilename(ref.getRuleSetReference().getRuleSetFileName()));
                            lines.add("*   [" + rule.getName() + "](" + link + "#" + rule.getName().toLowerCase(Locale.ROOT) + "): "
                                    + DEPRECATION_LABEL_SMALL
                                    + "The rule has been moved to another ruleset. Use instead "
                                    + "[" + ref.getRule().getName() + "](" + otherLink + "#" + ref.getRule().getName().toLowerCase(Locale.ROOT) + ").");
                        }
                    } else {
                        link += "#" + rule.getName().toLowerCase(Locale.ROOT);
                        lines.add("*   [" + rule.getName() + "](" + link + "): "
                                + (rule.isDeprecated() ? DEPRECATION_LABEL_SMALL : "")
                                + getShortRuleDescription(rule));
                    }
                }
                lines.add("");
            }

            System.out.println("Generated " + path);
            writer.write(path, lines);
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
        return getRuleSetFilename(ruleset.getFileName());
    }

    private static String getRuleSetFilename(String rulesetFileName) {
        return FilenameUtils.getBaseName(StringUtils.chomp(rulesetFileName));
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
                lines.add("keywords: " + getRuleSetKeywords(ruleset));
                lines.add("---");

                for (Rule rule : getSortedRules(ruleset)) {
                    lines.add("## " + rule.getName());
                    lines.add("");

                    if (rule instanceof RuleReference) {
                        RuleReference ref = (RuleReference) rule;
                        if (ruleset.getFileName().equals(ref.getRuleSetReference().getRuleSetFileName())) {
                            // rule renamed within same ruleset
                            lines.add(DEPRECATION_LABEL);
                            lines.add("");
                            lines.add("This rule has been renamed. Use instead: ["
                                    + ref.getRule().getName() + "](" + "#" + ref.getRule().getName().toLowerCase(Locale.ROOT) + ")");
                            lines.add("");
                        } else {
                            // rule moved to another ruleset
                            String otherLink = RULESET_INDEX_PERMALINK_PATTERN
                                    .replace("${language.tersename}", languageTersename)
                                    .replace("${ruleset.name}", getRuleSetFilename(ref.getRuleSetReference().getRuleSetFileName()));
                            lines.add(DEPRECATION_LABEL);
                            lines.add("");
                            lines.add("The rule has been moved to another ruleset. Use instead: ["
                                    + ref.getRule().getName() + "](" + otherLink + "#" + ref.getRule().getName().toLowerCase(Locale.ROOT) + ")");
                            lines.add("");
                        }
                    }

                    if (rule.isDeprecated()) {
                        lines.add(DEPRECATION_LABEL);
                        lines.add("");
                    }
                    if (rule.getSince() != null) {
                        lines.add("**Since:** PMD " + rule.getSince());
                        lines.add("");
                    }
                    lines.add("**Priority:** " + rule.getPriority() + " (" + rule.getPriority().getPriority() + ")");
                    lines.add("");

                    if (rule.getMinimumLanguageVersion() != null) {
                        lines.add("**Minimum Language Version:** "
                                + rule.getLanguage().getName() + " " + rule.getMinimumLanguageVersion().getVersion());
                        lines.add("");
                    }

                    lines.add(StringUtils.stripToEmpty(rule.getDescription()));
                    lines.add("");

                    if (rule instanceof XPathRule || rule instanceof RuleReference && ((RuleReference) rule).getRule() instanceof XPathRule) {
                        lines.add("```");
                        lines.add(StringUtils.stripToEmpty(rule.getProperty(XPathRule.XPATH_DESCRIPTOR)));
                        lines.add("```");
                        lines.add("");
                    } else {
                        lines.add("**This rule is defined by the following Java class:** "
                                + "[" + rule.getRuleClass() + "]("
                                + GITHUB_SOURCE_LINK + getRuleClassSourceFilepath(rule.getRuleClass())
                                + ")");
                        lines.add("");
                    }

                    if (!rule.getExamples().isEmpty()) {
                        lines.add("**Example(s):**");
                        lines.add("");
                        for (String example : rule.getExamples()) {
                            lines.add("``` " + mapLanguageForHighlighting(languageTersename));
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

                writer.write(path, lines);
                System.out.println("Generated " + path);
            }
        }
    }

    /**
     * Simply maps PMD languages to rouge languages
     *
     * @param languageTersename
     * @return
     * @see <a href="https://github.com/jneen/rouge/wiki/List-of-supported-languages-and-lexers">List of supported languages</a>
     */
    private static String mapLanguageForHighlighting(String languageTersename) {
        if (LANGUAGE_HIGHLIGHT_MAPPER.containsKey(languageTersename)) {
            return LANGUAGE_HIGHLIGHT_MAPPER.get(languageTersename);
        }
        return languageTersename;
    }

    private String getRuleSetKeywords(RuleSet ruleset) {
        List<String> ruleNames = new LinkedList<>();
        for (Rule rule : ruleset.getRules()) {
            ruleNames.add(rule.getName());
        }
        return ruleset.getName() + ", " + StringUtils.join(ruleNames, ", ");
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
    private String getRuleSetSourceFilepath(RuleSet ruleset) throws IOException {
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

    private String getRuleClassSourceFilepath(String ruleClass) throws IOException {
        final String relativeSourceFilename = ruleClass.replaceAll("\\.", File.separator) + ".java";
        final List<Path> foundPathResult = new LinkedList<>();

        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String path = file.toString();
                if (path.contains("src") && path.endsWith(relativeSourceFilename)) {
                    foundPathResult.add(file);
                    return FileVisitResult.TERMINATE;
                }
                return super.visitFile(file, attrs);
            }
        });

        if (!foundPathResult.isEmpty()) {
            Path foundPath = foundPathResult.get(0);
            foundPath = root.relativize(foundPath);
            return FilenameUtils.normalize(foundPath.toString(), true);
        }

        return FilenameUtils.normalize(relativeSourceFilename, true);
    }
}
