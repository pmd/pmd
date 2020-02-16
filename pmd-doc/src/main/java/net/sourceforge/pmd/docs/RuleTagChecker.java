/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleTagChecker {
    private static final Logger LOG = Logger.getLogger(DeadLinksChecker.class.getName());

    private static final Pattern RULE_TAG = Pattern.compile("\\{%\\s*rule\\s+\"(.*?)\"\\s*");
    private static final Pattern RULE_REFERENCE = Pattern.compile("(\\w+)\\/(\\w+)\\/(\\w+)");

    private final Path pagesDirectory;
    private final List<String> issues = new ArrayList<>();
    private final Map<Path, Set<String>> rulesCache = new HashMap<>();

    public RuleTagChecker(Path rootDirectory) {
        final Path pagesDirectory = rootDirectory.resolve("docs/pages");

        if (!Files.isDirectory(pagesDirectory)) {
            LOG.severe("can't check rule tags, didn't find \"docs/pages\" directory at: " + pagesDirectory);
            System.exit(1);
        }

        this.pagesDirectory = pagesDirectory;
    }

    public List<String> check() throws IOException {
        Files.walkFileTree(pagesDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                checkFile(file);
                return super.visitFile(file, attrs);
            }
        });
        return issues;
    }

    private void checkFile(Path file) throws IOException {
        if (file == null || !file.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".md")) {
            return;
        }

        LOG.finer("Checking " + file);
        int lineNo = 0;
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            lineNo++;
            Matcher ruleTagMatcher = RULE_TAG.matcher(line);
            while (ruleTagMatcher.find()) {
                String ruleReference = ruleTagMatcher.group(1);
                int pos = ruleTagMatcher.end();
                if (line.charAt(pos) != '%' || line.charAt(pos + 1) != '}') {
                    addIssue(file, lineNo, "Rule tag for " + ruleReference + " is not closed properly");
                } else if (!ruleReferenceTargetExists(ruleReference)) {
                    addIssue(file, lineNo, "Rule " + ruleReference + " is not found");
                }
            }
        }
    }

    private boolean ruleReferenceTargetExists(String ruleReference) {
        Matcher ruleRefMatcher = RULE_REFERENCE.matcher(ruleReference);
        if (ruleRefMatcher.matches()) {
            String language = ruleRefMatcher.group(1);
            String category = ruleRefMatcher.group(2);
            String rule = ruleRefMatcher.group(3);

            Path ruleDocPage = pagesDirectory.resolve("pmd/rules/" + language + "/" + category.toLowerCase(Locale.ROOT) + ".md");
            Set<String> rules = getRules(ruleDocPage);
            return rules.contains(rule);
        }
        return false;
    }

    private Set<String> getRules(Path ruleDocPage) {
        Set<String> result = rulesCache.get(ruleDocPage);

        if (result == null) {
            result = new HashSet<>();
            try {
                for (String line : Files.readAllLines(ruleDocPage, StandardCharsets.UTF_8)) {
                    if (line.startsWith("## ")) {
                        result.add(line.substring(3));
                    }
                    rulesCache.put(ruleDocPage, result);
                }
            } catch (NoSuchFileException e) {
                LOG.warning("File " + ruleDocPage + " not found.");
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Unable to read rules from " + ruleDocPage, e);
            }
        }

        return result;
    }

    private void addIssue(Path file, int lineNo, String message) {
        issues.add(String.format("%s:%2d: %s", pagesDirectory.relativize(file).toString(), lineNo, message));
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Wrong arguments!");
            System.err.println();
            System.err.println("java " + RuleTagChecker.class.getSimpleName() + " <project base directory>");
            System.exit(1);
        }
        final Path rootDirectory = Paths.get(args[0]).resolve("..").toRealPath();

        RuleTagChecker ruleTagChecker = new RuleTagChecker(rootDirectory);
        List<String> issues = ruleTagChecker.check();

        if (!issues.isEmpty()) {
            issues.forEach(System.err::println);
            throw new AssertionError("Wrong rule tags detected");
        }
    }
}
