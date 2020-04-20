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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;


/**
 * Checks links to local pages for non-existing link-targets.
 */
public class DeadLinksChecker {
    private static final Logger LOG = Logger.getLogger(DeadLinksChecker.class.getName());

    private static final String CHECK_EXTERNAL_LINKS_PROPERTY = "pmd.doc.checkExternalLinks";
    private static final boolean CHECK_EXTERNAL_LINKS = Boolean.parseBoolean(System.getProperty(CHECK_EXTERNAL_LINKS_PROPERTY));

    // Markdown-Link: something in []'s followed by something in ()'s
    private static final Pattern LOCAL_LINK_PATTERN = Pattern.compile("(!)?\\[.*?]\\((.*?)\\)");

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
    private final Map<String, CompletableFuture<Integer>> urlResponseCache = new ConcurrentHashMap<>();

    private final ExecutorService executorService = Executors.newCachedThreadPool();


    public void checkDeadLinks(Path rootDirectory) {
        final Path pagesDirectory = rootDirectory.resolve("docs/pages");
        final Path docsDirectory = rootDirectory.resolve("docs");

        if (!Files.isDirectory(pagesDirectory)) {
            // docsDirectory is implicitly checked by this statement too
            LOG.severe("can't check for dead links, didn't find \"pages\" directory at: " + pagesDirectory);
            System.exit(1);
        }

        // read all .md-files in the pages directory
        final List<Path> mdFiles = listMdFiles(pagesDirectory);

        // Stores file path to the future deadlinks. If a future evaluates to null, the link is not dead
        final Map<Path, List<Future<String>>> fileToDeadLinks = new HashMap<>();
        // make a list of all valid link targets
        final Set<String> htmlPages = extractLinkTargets(mdFiles);

        // scan all .md-files for dead local links
        int scannedFiles = 0;
        int foundExternalLinks = 0;
        int checkedExternalLinks = 0;

        for (Path mdFile : mdFiles) {
            final String pageContent = fileToString(mdFile);
            scannedFiles++;

            // iterate line-by-line for better reporting the line numbers
            final String[] lines = pageContent.split("\r?\n");
            for (int index = 0; index < lines.length; index++) {
                final String line = lines[index];
                final int lineNo = index + 1;

                final Matcher matcher = LOCAL_LINK_PATTERN.matcher(line);
                linkCheck:
                while (matcher.find()) {
                    final String linkText = matcher.group();
                    final boolean isImageLink = matcher.group(1) != null;
                    final String linkTarget = matcher.group(2);
                    boolean linkOk;

                    if (linkTarget.charAt(0) == '/') {
                        // links must never start with / - they must be relative or start with https?//...
                        linkOk = false;
                    } else if (linkTarget.startsWith(LOCAL_FILE_PREFIX)) {
                        String localLinkPart = linkTarget.substring(LOCAL_FILE_PREFIX.length());
                        if (localLinkPart.contains("#")) {
                            localLinkPart = localLinkPart.substring(0, localLinkPart.indexOf('#'));
                        }

                        final Path localFile = rootDirectory.resolve(localLinkPart);
                        linkOk = Files.isRegularFile(localFile);
                        if (!linkOk) {
                            LOG.warning("local file not found: " + localFile);
                            LOG.warning("  linked by: " + linkTarget);
                        }

                    } else if (linkTarget.startsWith("http://") || linkTarget.startsWith("https://")) {
                        foundExternalLinks++;

                        if (!CHECK_EXTERNAL_LINKS) {
                            LOG.finer("ignoring check of external url: " + linkTarget);
                            continue;
                        }

                        for (String ignoredUrlPrefix : IGNORED_URL_PREFIXES) {
                            if (linkTarget.startsWith(ignoredUrlPrefix)) {
                                LOG.finer("not checking link: " + linkTarget);
                                continue linkCheck;
                            }
                        }

                        checkedExternalLinks++;
                        linkOk = true;

                        Future<String> futureMessage =
                            getCachedFutureResponse(linkTarget)
                                .thenApply(c -> c >= 400)
                                // It's important not to use the matcher in this mapper!
                                // It may be exhausted at the time of execution
                                .thenApply(dead -> dead ? String.format("%8d: %s", lineNo, linkText) : null);

                        addDeadLink(fileToDeadLinks, mdFile, futureMessage);

                    } else {
                        // ignore local anchors
                        if (linkTarget.startsWith("#")) {
                            continue;
                        }

                        // ignore some pages where automatic link detection doesn't work
                        if (EXCLUDED_LINK_TARGETS.matcher(linkTarget).matches()) {
                            continue;
                        }

                        if (isImageLink) {
                            Path localResource = docsDirectory.resolve(linkTarget);
                            linkOk = Files.exists(localResource);
                        } else {
                            linkOk = linkTarget.isEmpty() || htmlPages.contains(linkTarget);
                        }

                        // maybe a local file
                        if (!linkOk) {
                            Path localResource = docsDirectory.resolve(linkTarget);
                            linkOk = Files.exists(localResource);
                        }
                    }

                    if (!linkOk) {
                        RunnableFuture<String> futureTask = new FutureTask<>(() -> String.format("%8d: %s", lineNo, linkText));
                        // execute this task immediately in this thread.
                        // External links are checked by another executor and don't end up here.
                        futureTask.run();
                        addDeadLink(fileToDeadLinks, mdFile, futureTask);
                    }
                }
            }
        }

        executorService.shutdown();

        LOG.info("Scanned " + scannedFiles + " files for dead links.");
        LOG.info("  Found " + foundExternalLinks + " external links, " + checkedExternalLinks + " of those where checked.");

        if (!CHECK_EXTERNAL_LINKS) {
            LOG.info("External links weren't checked, set -D" + CHECK_EXTERNAL_LINKS_PROPERTY + "=true to enable it.");
        }

        Map<Path, List<String>> joined = joinFutures(fileToDeadLinks);

        if (joined.isEmpty()) {
            LOG.info("No errors found!");
        } else {
            LOG.warning("Found dead link(s):");
            for (Path file : joined.keySet()) {
                System.err.println(rootDirectory.relativize(file).toString());
                joined.get(file).forEach(LOG::warning);
            }
            throw new AssertionError("Dead links detected");
        }
    }


    private Map<Path, List<String>> joinFutures(Map<Path, List<Future<String>>> map) {
        Map<Path, List<String>> joined = new HashMap<>();

        for (Path p : map.keySet()) {

            List<String> evaluatedResult = map.get(p).stream()
                                              .map(f -> {
                                                  try {
                                                      return f.get();
                                                  } catch (InterruptedException | ExecutionException e) {
                                                      e.printStackTrace();
                                                      return null;
                                                  }
                                              })
                                              .filter(Objects::nonNull)
                                              .sorted(Comparator.naturalOrder())
                                              .collect(Collectors.toList());

            if (!evaluatedResult.isEmpty()) {
                joined.put(p, evaluatedResult);
            }
        }
        return joined;
    }


    private void addDeadLink(Map<Path, List<Future<String>>> fileToDeadLinks, Path file, Future<String> line) {
        fileToDeadLinks.computeIfAbsent(file, k -> new ArrayList<>()).add(line);
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
        try {
            return Files.walk(pagesDirectory)
                 .filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".md"))
                 .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new RuntimeException("error listing files in " + pagesDirectory, ex);
        }
    }


    private String fileToString(Path mdFile) {
        try (InputStream inputStream = Files.newInputStream(mdFile)) {
            return IOUtils.toString(inputStream, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            throw new RuntimeException("error reading " + mdFile, ex);
        }
    }


    private CompletableFuture<Integer> getCachedFutureResponse(String url) {
        if (urlResponseCache.containsKey(url)) {
            LOG.info("response: HTTP " + urlResponseCache.get(url) + " (CACHED) on " + url);
            return urlResponseCache.get(url);
        } else {
            // process asynchronously
            CompletableFuture<Integer> futureResponse = CompletableFuture.supplyAsync(() -> computeHttpResponse(url), executorService);
            urlResponseCache.put(url, futureResponse);
            return futureResponse;
        }
    }


    private int computeHttpResponse(String url) {
        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("HEAD");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.connect();
            final int responseCode = httpURLConnection.getResponseCode();

            String response = "HTTP " + responseCode;
            if (httpURLConnection.getHeaderField("Location") != null) {
                response += ", Location: " + httpURLConnection.getHeaderField("Location");
            }

            LOG.fine("response: " + response + " on " + url);

            // success (HTTP 2xx) or redirection (HTTP 3xx)
            return responseCode;

        } catch (IOException ex) {
            LOG.fine("response: " + ex.getClass().getName() + " on " + url + " : " + ex.getMessage());
            return 599;
        }
    }


    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Wrong arguments!");
            System.err.println();
            System.err.println("java " + DeadLinksChecker.class.getSimpleName() + " <project base directory>");
            System.exit(1);
        }
        final Path rootDirectory = Paths.get(args[0]).resolve("..").toRealPath();

        DeadLinksChecker deadLinksChecker = new DeadLinksChecker();
        deadLinksChecker.checkDeadLinks(rootDirectory);
    }



}
