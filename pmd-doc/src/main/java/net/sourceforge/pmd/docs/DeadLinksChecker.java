/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * Checks links to local pages for non-existing link-targets.
 */
public class DeadLinksChecker {

    // Markdown-Link: something in []'s followed by something in ()'s
    private static final Pattern LOCAL_LINK_PATTERN = Pattern.compile("\\[.*?\\]\\((.*?)\\)");

    // Markdown permalink-header and captions
    private static final Pattern MD_HEADER_PERMALINK = Pattern.compile("permalink:\\s*(.*)");
    private static final Pattern MD_CAPTION = Pattern.compile("^##+\\s+(.*)$", Pattern.MULTILINE);

    // list of link targets, where the link detection doesn't work
    private static final Pattern EXCLUDED_LINK_TARGETS = Pattern.compile(
            "^pmd_userdocs_cli_reference\\.html.*" // anchors in the CLI reference are a plain HTML include
    );

    public void checkDeadLinks(Path pagesDirectory) {
        if (!Files.isDirectory(pagesDirectory)) {
            System.err.println("can't check for dead links, didn't find \"pages\" directory at: " + pagesDirectory);
            System.exit(1);
        }

        // read all .md-files in the pages directory
        final List<Path> mdFiles = listMdFiles(pagesDirectory);


        // make a list of all valid link targets
        final Set<String> htmlPages = extractLinkTargets(mdFiles);

        // scan all .md-files for dead local links
        Path errorFile = null;
        int scannedFiles = 0;
        for (Path mdFile : mdFiles) {
            final String pageContent = fileToString(mdFile);
            scannedFiles++;

            // iterate line-by-line for better reporting the line numbers
            final String[] lines = pageContent.split("\r?\n|\n");
            for (int index = 0; index < lines.length; index++) {
                final String line = lines[index];
                final int lineNo = index + 1;


                final Matcher matcher = LOCAL_LINK_PATTERN.matcher(line);
                while (matcher.find()) {
                    String linkTarget = matcher.group(1);
                    linkTarget = linkTarget.replaceAll("^/+", ""); // remove the leading "/"

                    // ignore http/https links
                    if (linkTarget.startsWith("http://") || linkTarget.startsWith("https://")) {
                        continue;
                    }

                    // ignore local anchors
                    if (linkTarget.startsWith("#")) {
                        continue;
                    }

                    // ignore some pages where automatic link detection doesn't work
                    if (EXCLUDED_LINK_TARGETS.matcher(linkTarget).matches()) {
                        continue;
                    }

                    if (!linkTarget.isEmpty() && !htmlPages.contains(linkTarget)) {
                        if (errorFile == null) {
                            System.err.println("Found dead link(s):");
                        }
                        if (!mdFile.equals(errorFile)) {
                            System.err.println(mdFile);
                            errorFile = mdFile;
                        }
                        System.err.printf("%8d: %s%n", lineNo, matcher.group());
                    }
                }
            }
        }
        if (errorFile != null) {
            throw new AssertionError("dead links detected");
        } else {
            System.out.println("Scanned " + scannedFiles + " files for dead links - no errors found!");
        }
    }

    private Set<String> extractLinkTargets(List<Path> mdFiles) {
        final Set<String> htmlPages = new HashSet<>();
        for (Path mdFile : mdFiles) {
            final String pageContent = fileToString(mdFile);

            // extract the permalink header field
            final Matcher permalinkMatcher = MD_HEADER_PERMALINK.matcher(pageContent);
            if (!permalinkMatcher.find()) {
                continue;
            }

            final String pageUrl = permalinkMatcher.group(1)
                    .replaceAll("^/+", ""); // remove the leading "/"

            // add the root page
            htmlPages.add(pageUrl);

            // add all captions as anchors
            final Matcher captionMatcher = MD_CAPTION.matcher(pageContent);
            while (captionMatcher.find()) {
                final String anchor = captionMatcher.group(1)
                        .toLowerCase(Locale.ROOT)
                        .replaceAll("[^a-z0-9_]+", "-") // replace all non-alphanumeric characters with dashes
                        .replaceAll("^-+|-+$", ""); // trim leading or trailing dashes

                htmlPages.add(pageUrl + "#" + anchor);
            }
        }
        return htmlPages;
    }

    private List<Path> listMdFiles(Path pagesDirectory) {
        final List<Path> mdFiles = new ArrayList<>();
        try {
            Files.walk(pagesDirectory)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".md"))
                    .forEach(mdFiles::add);
        } catch (IOException ex) {
            throw new RuntimeException("error listing files in " + pagesDirectory, ex);
        }
        return mdFiles;
    }

    private String fileToString(Path mdFile) {
        try (InputStream inputStream = Files.newInputStream(mdFile)) {
            return IOUtils.toString(inputStream, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            throw new RuntimeException("error reading " + mdFile, ex);
        }
    }

}
