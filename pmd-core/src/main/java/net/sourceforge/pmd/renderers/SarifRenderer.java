/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLogBuilder;
import net.sourceforge.pmd.reporting.FileNameRenderer;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SarifRenderer extends AbstractIncrementingRenderer {
    public static final String NAME = "sarif";
    private static final String DEFAULT_DESCRIPTION = "Static Analysis Results Interchange Format (SARIF)";
    private static final String DEFAULT_FILE_EXTENSION = "sarif.json";

    private final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    private SarifLogBuilder sarifLogBuilder;
    private boolean hasFileNameRenderer;

    public SarifRenderer() {
        super(NAME, DEFAULT_DESCRIPTION);
    }

    @Override
    public String defaultFileExtension() {
        return DEFAULT_FILE_EXTENSION;
    }

    @Override
    public void start() throws IOException {
        sarifLogBuilder = SarifLogBuilder.sarifLogBuilder(this::determineFileUri);
    }

    @Override
    public void setFileNameRenderer(FileNameRenderer fileNameRenderer) {
        super.setFileNameRenderer(fileNameRenderer);
        hasFileNameRenderer = true;
    }

    private String determineFileUri(FileId fileId) {
        if (!hasFileNameRenderer) {
            return fileId.getUriString();
        }
        String fileName = determineFileName(fileId);
        Path path = Paths.get(fileName);
        if (path.isAbsolute()) {
            return path.toUri().toASCIIString();
        }
        String uriPath = fileName.replace('\\', '/');
        // A colon in the first segment would otherwise be interpreted as a URI scheme.
        if (uriPath.indexOf(':') >= 0 && uriPath.split("/", 2)[0].contains(":")) {
            uriPath = "./" + uriPath;
        }
        try {
            return new URI(null, null, uriPath, null).toASCIIString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid file name: " + fileName, e);
        }
    }

    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
        while (violations.hasNext()) {
            final RuleViolation violation = violations.next();
            sarifLogBuilder.add(violation);
        }
    }

    @Override
    public void end() throws IOException {
        addErrors();
        writeLog();
    }

    private void addErrors() {
        for (Report.ProcessingError error : this.errors) {
            sarifLogBuilder.addRunTimeError(error);
        }

        for (Report.ConfigurationError error: this.configErrors) {
            sarifLogBuilder.addConfigurationError(error);
        }
    }

    private void writeLog() throws IOException {
        final SarifLog sarifLog = sarifLogBuilder.build();
        final String json = gson.toJson(sarifLog);
        writer.write(json);
        writer.println();
    }

    @Override
    public void setReportFile(String reportFilename) {
        this.setWriter(IOUtil.createWriter(StandardCharsets.UTF_8, reportFilename));
    }
}
