/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

import com.google.gson.stream.JsonWriter;

public class JsonRenderer extends AbstractIncrementingRenderer {
    public static final String NAME = "json";

    // TODO do we make this public? It would make it possible to write eg
    //  if (jsonObject.getInt("formatVersion") > JsonRenderer.FORMAT_VERSION)
    //    /* handle unsupported version */
    //  because the JsonRenderer.FORMAT_VERSION would be hardcoded by the compiler
    private static final int FORMAT_VERSION = 0;

    private JsonWriter jsonWriter;

    public JsonRenderer() {
        super(NAME, "JSON format.");
    }

    @Override
    public String defaultFileExtension() {
        return "json";
    }

    @Override
    public void start() throws IOException {
        jsonWriter = new JsonWriter(writer);
        jsonWriter.setHtmlSafe(true);
        jsonWriter.setIndent("  ");

        jsonWriter.beginObject();
        jsonWriter.name("formatVersion").value(FORMAT_VERSION);
        jsonWriter.name("pmdVersion").value(PMDVersion.VERSION);
        jsonWriter.name("timestamp").value(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date()));
        jsonWriter.name("files").beginArray();
    }

    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
        String filename = null;

        while (violations.hasNext()) {
            RuleViolation rv = violations.next();
            String nextFilename = determineFileName(rv.getFilename());
            if (!nextFilename.equals(filename)) {
                // New File
                if (filename != null) {
                    // Not first file ?
                    jsonWriter.endArray(); // violations
                    jsonWriter.endObject(); // file object
                }
                filename = nextFilename;
                jsonWriter.beginObject();
                jsonWriter.name("filename").value(filename);
                jsonWriter.name("violations").beginArray();
            }
            renderSingleViolation(rv);
        }

        jsonWriter.endArray(); // violations
        jsonWriter.endObject(); // file object
    }

    private void renderSingleViolation(RuleViolation rv) throws IOException {
        renderSingleViolation(rv, null, null);
    }

    private void renderSingleViolation(RuleViolation rv, String suppressionType, String userMsg) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("beginline").value(rv.getBeginLine());
        jsonWriter.name("begincolumn").value(rv.getBeginColumn());
        jsonWriter.name("endline").value(rv.getEndLine());
        jsonWriter.name("endcolumn").value(rv.getEndColumn());
        jsonWriter.name("description").value(rv.getDescription());
        jsonWriter.name("rule").value(rv.getRule().getName());
        jsonWriter.name("ruleset").value(rv.getRule().getRuleSetName());
        jsonWriter.name("priority").value(rv.getRule().getPriority().getPriority());
        if (StringUtils.isNotBlank(rv.getRule().getExternalInfoUrl())) {
            jsonWriter.name("externalInfoUrl").value(rv.getRule().getExternalInfoUrl());
        }
        if (StringUtils.isNotBlank(suppressionType)) {
            jsonWriter.name("suppressiontype").value(suppressionType);
        }
        if (StringUtils.isNotBlank(userMsg)) {
            jsonWriter.name("usermsg").value(userMsg);
        }
        jsonWriter.endObject();
    }

    @Override
    public void end() throws IOException {
        jsonWriter.endArray(); // files

        jsonWriter.name("suppressedViolations").beginArray();
        String filename = null;
        if (!this.suppressed.isEmpty()) {
            for (Report.SuppressedViolation s : this.suppressed) {
                RuleViolation rv = s.getRuleViolation();
                String nextFilename = determineFileName(rv.getFilename());
                if (!nextFilename.equals(filename)) {
                    // New File
                    if (filename != null) {
                        // Not first file ?
                        jsonWriter.endArray(); // violations
                        jsonWriter.endObject(); // file object
                    }
                    filename = nextFilename;
                    jsonWriter.beginObject();
                    jsonWriter.name("filename").value(filename);
                    jsonWriter.name("violations").beginArray();
                }
                renderSingleViolation(rv, s.suppressedByNOPMD() ? "nopmd" : "annotation", s.getUserMessage());
            }
            jsonWriter.endArray(); // violations
            jsonWriter.endObject(); // file object
        }
        jsonWriter.endArray();

        jsonWriter.name("processingErrors").beginArray();
        for (Report.ProcessingError error : this.errors) {
            jsonWriter.beginObject();
            jsonWriter.name("filename").value(error.getFile());
            jsonWriter.name("message").value(error.getMsg());
            jsonWriter.name("detail").value(error.getDetail());
            jsonWriter.endObject();
        }
        jsonWriter.endArray();

        jsonWriter.name("configurationErrors").beginArray();
        for (Report.ConfigurationError error : this.configErrors) {
            jsonWriter.beginObject();
            jsonWriter.name("rule").value(error.rule().getName());
            jsonWriter.name("ruleset").value(error.rule().getRuleSetName());
            jsonWriter.name("message").value(error.issue());
            jsonWriter.endObject();
        }
        jsonWriter.endArray();

        jsonWriter.endObject();
        jsonWriter.flush();
    }
}
