/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SarifRenderer extends AbstractIncrementingRenderer {
    public static final String NAME = "sarif";
    private static final String DEFAULT_DESCRIPTION = "Sarif integration.";
    private static final String DEFAULT_FILE_EXTENSION = "sarif.json";

    private final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    private SarifLogBuilder sarifLogBuilder;

    public SarifRenderer() {
        super(NAME, DEFAULT_DESCRIPTION);
    }

    @Override
    public String defaultFileExtension() {
        return DEFAULT_FILE_EXTENSION;
    }

    @Override
    public void start() throws IOException {
        sarifLogBuilder = SarifLogBuilder.sarifLogBuilder();
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
        if (!hasErrors()) {
            writeLog();
        } else {
            writeErrors();
        }
    }

    private boolean hasErrors() {
        return !errors.isEmpty() || !configErrors.isEmpty();
    }

    private void writeLog() throws IOException {
        final SarifLog sarifLog = sarifLogBuilder.build();

        final String json = gson.toJson(sarifLog);
        writer.write(json);
    }

    private void writeErrors() throws IOException {
        final Map<String, Map<String, String>> errors = new HashMap<>();
        final Map<String, String> processingErrors = new HashMap<>();
        final Map<String, String> configErrors = new HashMap<>();

        for (Report.ProcessingError error : this.errors) {
            processingErrors.put("filename", error.getFile());
            processingErrors.put("message", error.getMsg());
            processingErrors.put("detail", error.getDetail());
        }

        for (Report.ConfigurationError error: this.configErrors) {
            configErrors.put("rule", error.rule().getName());
            configErrors.put("ruleset", error.rule().getRuleSetName());
            configErrors.put("message", error.issue());
        }

        errors.put("processing-errors", processingErrors);
        errors.put("config-errors", configErrors);

        final String json = gson.toJson(errors);
        writer.write(json);
    }
}
