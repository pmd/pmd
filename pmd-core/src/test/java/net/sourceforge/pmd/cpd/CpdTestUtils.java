/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.reporting.Report;

final class CpdTestUtils {

    public static final FileId FOO_FILE_ID = FileId.fromPathLikeString("/var/Foo.java");
    public static final FileId BAR_FILE_ID = FileId.fromPathLikeString("/var/Bar.java");

    private CpdTestUtils() {
        // utility class
    }

    static class CpdReportBuilder {
        static final String DUMMY_FILE_CONTENT = generateDummyContent(60);

        private final Map<FileId, TextFile> files = new HashMap<>();
        private final Map<FileId, Integer> numTokensPerFile = new HashMap<>();

        private final Tokens tokens = new Tokens();
        private final List<Match> matches = new ArrayList<>();
        private final List<Report.ProcessingError> processingErrors = new ArrayList<>();

        CpdReportBuilder setFileContent(FileId fileName) {
            setFileContent(fileName, DUMMY_FILE_CONTENT);
            return this;
        }

        CpdReportBuilder setFileContent(FileId fileName, LanguageVersion version) {
            setFileContent(fileName, DUMMY_FILE_CONTENT, version);
            return this;
        }

        CpdReportBuilder setFileContent(FileId fileName, int numTokens) {
            setFileContent(fileName, DUMMY_FILE_CONTENT, DummyLanguageModule.getInstance().getDefaultVersion(), numTokens);
            return this;
        }

        CpdReportBuilder setFileContent(FileId fileName, String content) {
            setFileContent(fileName, content, DummyLanguageModule.getInstance().getDefaultVersion());
            return this;
        }

        CpdReportBuilder setFileContent(FileId fileName, String content, LanguageVersion version) {
            setFileContent(fileName, content, version, 10);
            return this;
        }

        CpdReportBuilder setFileContent(FileId fileName, String content, LanguageVersion version, int numTokens) {
            files.put(fileName, TextFile.forCharSeq(
                    content,
                    fileName,
                    version
            ));
            numTokensPerFile.put(fileName, numTokens);
            return this;
        }

        CpdReportBuilder addProcessingError(Report.ProcessingError processingError) {
            processingErrors.add(processingError);
            return this;
        }

        Mark createMark(String image, FileId fileName, int beginLine, int lineCount) {
            if (!files.containsKey(fileName)) {
                setFileContent(fileName);
            }
            return new Mark(tokens.addToken(image, fileName, beginLine, 1, beginLine + lineCount - 1, 1));
        }

        CpdReportBuilder addMatch(Match match) {
            for (Mark mark : match) {
                if (!files.containsKey(mark.getLocation().getFileId())) {
                    setFileContent(mark.getLocation().getFileId());
                }
            }
            matches.add(match);
            return this;
        }

        Mark createMark(String image, FileId fileId, int beginLine, int lineCount, int beginColumn, int endColumn) {
            if (!files.containsKey(fileId)) {
                setFileContent(fileId);
            }
            final TokenEntry beginToken = tokens.addToken(image, fileId, beginLine, beginColumn, beginLine,
                                                          beginColumn + image.length());
            final TokenEntry endToken = tokens.addToken(image, fileId,
                                                        beginLine + lineCount - 1, beginColumn,
                                                        beginLine + lineCount - 1, endColumn);
            final Mark result = new Mark(beginToken);

            result.setEndToken(endToken);
            return result;
        }

        CPDReport build() {
            assertEquals(files.size(), numTokensPerFile.size(), "files size/numTokensPerFile mismatch");

            return new CPDReport(
                    new SourceManager(new ArrayList<>(files.values())),
                    matches,
                    numTokensPerFile,
                    processingErrors
            );
        }
    }

    static String generateDummyContent(int lengthInLines) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lengthInLines; i++) {
            for (int j = 0; j < 10; j++) {
                sb.append(i).append("_");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
