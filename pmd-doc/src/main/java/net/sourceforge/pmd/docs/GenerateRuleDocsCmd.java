/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSetParser;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;

public final class GenerateRuleDocsCmd {

    private static final Logger LOG = Logger.getLogger(GenerateRuleDocsCmd.class.getName());

    private GenerateRuleDocsCmd() {
        // Utility class
    }

    public static void main(String[] args) throws RuleSetNotFoundException {
        if (args.length != 1) {
            System.err.println("One argument is required: The base directory of the module pmd-doc.");
            System.exit(1);
        }

        long start = System.currentTimeMillis();
        Path output = FileSystems.getDefault().getPath(args[0]).resolve("..").toAbsolutePath().normalize();
        System.out.println("Generating docs into " + output);

        // important: use a RuleSetFactory that includes all rules, e.g. deprecated rule references
        Iterator<RuleSet> registeredRuleSets = getRegisteredRuleSets().iterator();
        List<String> additionalRulesets = findAdditionalRulesets(output);

        RuleDocGenerator generator = new RuleDocGenerator(new DefaultFileWriter(), output);
        generator.generate(registeredRuleSets, additionalRulesets);

        System.out.println("Generated docs in " + (System.currentTimeMillis() - start) + " ms");
    }


    /**
     * Returns an Iterator of RuleSet objects loaded from descriptions from the
     * "categories.properties" resource for each Language with Rule support.
     *
     * @return An Iterator of RuleSet objects.
     *
     * @throws RuleSetNotFoundException if the ruleset file could not be found
     */
    private static Iterable<RuleSet> getRegisteredRuleSets() throws RuleSetNotFoundException {
        String rulesetsProperties;
        RuleSetParser parser = new RuleSetParser().warnDeprecated(false).includeDeprecatedRuleReferences(true);

        List<RuleSet> ruleSets = new ArrayList<>();
        for (Language language : LanguageRegistry.getLanguages()) {
            Properties props = new Properties();
            rulesetsProperties = "category/" + language.getTerseName() + "/categories.properties";

            InputStream resource = GenerateRuleDocsCmd.class.getResourceAsStream(rulesetsProperties);
            if (resource == null) {
                LOG.warning("The language " + language.getTerseName() + " provides no " + rulesetsProperties + ".");
                continue;
            }

            try (InputStream inputStream = resource) {
                props.load(inputStream);
                String rulesetFilenames = props.getProperty("rulesets.filenames");

                if (rulesetFilenames != null) {

                    ruleSets.addAll(parser.parseFromResources(rulesetFilenames.split(",")));

                }
            } catch (IOException ioe) {
                throw new RuntimeException("Couldn't find " + rulesetsProperties
                                               + "; please ensure that the directory is on the classpath. The current classpath is: "
                                               + System.getProperty("java.class.path"));
            }
        }
        return ruleSets;
    }

    public static List<String> findAdditionalRulesets(Path basePath) {
        try {
            List<String> additionalRulesets = new ArrayList<>();
            Pattern rulesetPattern = Pattern.compile("^.+" + Pattern.quote(File.separator) + "pmd-\\w+"
                                                         + Pattern.quote(FilenameUtils.normalize("/src/main/resources/rulesets/"))
                                                         + "\\w+" + Pattern.quote(File.separator) + "\\w+.xml$");
            Files.walkFileTree(basePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (rulesetPattern.matcher(file.toString()).matches()) {
                        additionalRulesets.add(file.toString());
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
            return additionalRulesets;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
