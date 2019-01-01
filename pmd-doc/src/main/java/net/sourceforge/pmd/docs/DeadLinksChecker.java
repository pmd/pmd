/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * Checks links to local pages for non-existing link-targets.
 */
public class DeadLinksChecker {

    private static final String CHECK_EXTERNAL_LINKS_PROPERTY = "pmd.doc.checkExternalLinks";
    private static final boolean CHECK_EXTERNAL_LINKS = Boolean.parseBoolean(System.getProperty(CHECK_EXTERNAL_LINKS_PROPERTY));

    // Markdown-Link: something in []'s followed by something in ()'s
    private static final Pattern LOCAL_LINK_PATTERN = Pattern.compile("\\[.*?\\]\\((.*?)\\)");

    // Markdown permalink-header and captions
    private static final Pattern MD_HEADER_PERMALINK = Pattern.compile("permalink:\\s*(.*)");
    private static final Pattern MD_CAPTION = Pattern.compile("^##+\\s+(.*)$", Pattern.MULTILINE);

    // list of link targets, where the link detection doesn't work
    private static final Pattern EXCLUDED_LINK_TARGETS = Pattern.compile(
            "^pmd_userdocs_cli_reference\\.html.*" // anchors in the CLI reference are a plain HTML include
    );

    // the link is actually pointing to a file in the pmd project
    private static final String LOCAL_FILE_PREFIX = "https://github.com/pmd/pmd/blob/master/";

    // don't check links to PMD bugs/issues/pull-requests  (performance optimization)
    private static final List<String> IGNORED_URL_PREFIXES = Collections.unmodifiableList(Arrays.asList(
            "https://github.com/pmd/pmd/issues/",
            "https://github.com/pmd/pmd/pull/",
            "https://sourceforge.net/p/pmd/bugs/"   
    ));

    // prevent checking the same link multiple times
    private final Map<String, Integer> linkResultCache = new HashMap<>();

    public static void main(String[] args) throws IOException {
        final Path rootDirectory = Paths.get(args[0]).resolve("..").toRealPath();

        DeadLinksChecker deadLinksChecker = new DeadLinksChecker();
        deadLinksChecker.checkDeadLinks(rootDirectory);
    }

    public void checkDeadLinks(Path rootDirectory) {
        final Path pagesDirectory = rootDirectory.resolve("docs/pages");

        if (!Files.isDirectory(pagesDirectory)) {
            System.err.println("can't check for dead links, didn't find \"pages\" directory at: " + pagesDirectory);
            System.exit(1);
        }

        // read all .md-files in the pages directory
        final List<Path> mdFiles = listMdFiles(pagesDirectory);


        // make a list of all valid link targets
        final Set<String> htmlPages = extractLinkTargets(mdFiles);

        // buffer the report to not have it broken up by error messages while checking links
        final List<String> deadLinksReport = new ArrayList<>();

        // scan all .md-files for dead local links
        Path errorFile = null;
        int scannedFiles = 0;
        int foundExternalLinks = 0;
        int checkedExternalLinks = 0;
        for (Path mdFile : mdFiles) {
            final String pageContent = fileToString(mdFile);
            scannedFiles++;

            // iterate line-by-line for better reporting the line numbers
            final String[] lines = pageContent.split("\r?\n|\n");
            for (int index = 0; index < lines.length; index++) {
                final String line = lines[index];
                final int lineNo = index + 1;


                final Matcher matcher = LOCAL_LINK_PATTERN.matcher(line);
                linkCheck:
                while (matcher.find()) {
                    String linkTarget = matcher.group(1);
                    linkTarget = linkTarget.replaceAll("^/+", ""); // remove the leading "/"
                    boolean linkOk;

                    if (linkTarget.startsWith(LOCAL_FILE_PREFIX)) {
                        String localLinkPart = linkTarget.substring(LOCAL_FILE_PREFIX.length());
                        if (localLinkPart.contains("#")) {
                            localLinkPart = localLinkPart.substring(0, localLinkPart.indexOf('#'));
                        }

                        final Path localFile = rootDirectory.resolve(localLinkPart);
                        linkOk = Files.isRegularFile(localFile);
                        if (!linkOk) {
                            System.err.println("local file not found: " + localFile);
                            System.err.println("  linked by: " + linkTarget);
                        }

                    } else if (linkTarget.startsWith("http://") || linkTarget.startsWith("https://")) {
                        foundExternalLinks++;
                        for (String ignoredUrlPrefix : IGNORED_URL_PREFIXES) {
                            if (linkTarget.startsWith(ignoredUrlPrefix)) {
                                System.out.println("not checking link: " + linkTarget);
                                continue linkCheck;
                            }
                        }
                        if (!CHECK_EXTERNAL_LINKS) {
                            System.out.println("ignoring check of external url: " + linkTarget);
                            continue;
                        }

                        checkedExternalLinks++;
                        linkOk = checkExternalLink(linkTarget);

                    } else {
                        // ignore local anchors
                        if (linkTarget.startsWith("#")) {
                            continue;
                        }

                        // ignore some pages where automatic link detection doesn't work
                        if (EXCLUDED_LINK_TARGETS.matcher(linkTarget).matches()) {
                            continue;
                        }

                        linkOk = linkTarget.isEmpty() || htmlPages.contains(linkTarget);
                    }

                    if (!linkOk) {
                        if (errorFile == null) {
                            deadLinksReport.add("Found dead link(s):");
                        }
                        if (!mdFile.equals(errorFile)) {
                            deadLinksReport.add(String.valueOf(mdFile));
                            errorFile = mdFile;
                        }
                        deadLinksReport.add(String.format("%8d: %s", lineNo, matcher.group()));
                    }
                }
            }
        }
        System.out.println("Scanned " + scannedFiles + " files for dead links.");
        System.out.println("  Found " + foundExternalLinks + " external links, " + checkedExternalLinks + " of those where checked.");

        if (!CHECK_EXTERNAL_LINKS) {
            System.out.println("External links weren't checked, set -D" + CHECK_EXTERNAL_LINKS_PROPERTY + "=true to enable it.");
        }

        if (!deadLinksReport.isEmpty()) {
            for (String line : deadLinksReport) {
                System.err.println(line);
            }
            throw new AssertionError("dead links detected");
        } else {
            System.out.println("no errors found!");
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

    private boolean checkExternalLink(String url) {
        System.out.println("checking url: " + url + " ...");
        if (linkResultCache.containsKey(url)) {
            System.out.println("response: HTTP " + linkResultCache.get(url) + " (CACHED)");
            return linkResultCache.get(url) < 400;
        }

        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.connect();
            final int responseCode = httpURLConnection.getResponseCode();

            String response = "HTTP " + responseCode;
            if (httpURLConnection.getHeaderField("Location") != null) {
                response += ", Location: " + httpURLConnection.getHeaderField("Location");
            }

            System.out.println("response: " + response);
            linkResultCache.put(url, responseCode);

            // success (HTTP 2xx) or redirection (HTTP 3xx)
            return responseCode < 400;

        } catch (IOException ex) {
            System.out.println("response: " + ex.getClass().getName() + " " + ex.getMessage());
            linkResultCache.put(url, 599);
            return false;
        }
    }

}
