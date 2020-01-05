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
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RulesetsFactoryUtils;

public final class GenerateRuleDocsCmd {
    private GenerateRuleDocsCmd() {
        // Utility class
    }

    public static void main(String[] args) throws RuleSetNotFoundException {
        long start = System.currentTimeMillis();
        Path output = FileSystems.getDefault().getPath(args[0]).resolve("..").toAbsolutePath().normalize();
        System.out.println("Generating docs into " + output);

        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
        Iterator<RuleSet> registeredRuleSets = ruleSetFactory.getRegisteredRuleSets();
        List<String> additionalRulesets = findAdditionalRulesets(output);

        RuleDocGenerator generator = new RuleDocGenerator(new DefaultFileWriter(), output);
        generator.generate(registeredRuleSets, additionalRulesets);

        System.out.println("Generated docs in " + (System.currentTimeMillis() - start) + " ms");
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
