/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetLoader;
import net.sourceforge.pmd.internal.util.IOUtil;

public final class GenerateRuleDocsCmd {

    private GenerateRuleDocsCmd() {
        // Utility class
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("One argument is required: The base directory of the module pmd-doc.");
            System.exit(1);
        }

        long start = System.currentTimeMillis();
        Path output = FileSystems.getDefault().getPath(args[0]).resolve("..").toAbsolutePath().normalize();
        System.out.println("Generating docs into " + output);

        // important: use a RuleSetFactory that includes all rules, e.g. deprecated rule references
        List<RuleSet> registeredRuleSets = new RuleSetLoader().includeDeprecatedRuleReferences(true)
                .getStandardRuleSets();
        List<String> additionalRulesets = findAdditionalRulesets(output);

        RuleDocGenerator generator = new RuleDocGenerator(new DefaultFileWriter(), output);
        generator.generate(registeredRuleSets, additionalRulesets);

        System.out.println("Generated docs in " + (System.currentTimeMillis() - start) + " ms");
    }

    static final Pattern ADDITIONAL_RULESET_PATTERN = Pattern.compile("^.+" + Pattern.quote(File.separator) + "pmd-\\w+"
            + Pattern.quote(IOUtil.normalizePath(File.separator + Paths.get("src", "main", "resources", "rulesets").toString()) + File.separator)
            + "\\w+" + Pattern.quote(File.separator) + "\\w+.xml$");

    public static List<String> findAdditionalRulesets(Path basePath) {
        try {
            List<String> additionalRulesets = new ArrayList<>();
            Files.walkFileTree(basePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (ADDITIONAL_RULESET_PATTERN.matcher(file.toString()).matches()) {
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
