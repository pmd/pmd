/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

final class RendererHelper {
    private RendererHelper() {
        // utility class
    }

    static void render(Iterator<Match> matches, Writer writer, CPDReportRenderer renderer) throws IOException {
        List<Match> matchesList = new ArrayList<>();
        matches.forEachRemaining(matchesList::add);

        List<TextFile> textFiles = new ArrayList<>();
        Set<String> paths = new HashSet<>();
        for (Match match : matchesList) {
            for (Mark mark : match.getMarkSet()) {
                paths.add(mark.getFilename());
            }
        }
        for (String path : paths) {
            textFiles.add(TextFile.forPath(Paths.get(path), StandardCharsets.UTF_8, JavaLanguageModule.getInstance().getDefaultVersion()));
        }

        try (SourceManager sourceManager = new SourceManager(textFiles)) {
            CPDReport report = new CPDReport(sourceManager, matchesList, Collections.emptyMap());
            renderer.render(report, writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
