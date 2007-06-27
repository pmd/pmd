package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import net.sourceforge.pmd.DataSource;
import net.sourceforge.pmd.Report;

public abstract class AbstractRenderer implements Renderer {

    protected boolean showSuppressedViolations = true;
    private Writer writer;
    private Report mainReport;

    public void showSuppressedViolations(boolean show) {
        this.showSuppressedViolations = show;
    }

    public String render(Report report) {
        StringWriter w = new StringWriter();
        try {
            render(w, report);
        } catch (IOException e) {
            throw new Error("StringWriter doesn't throw IOException", e);
        }
        return w.toString();
    }


    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public Writer getWriter() {
        return writer;
    }

    public void start() throws IOException {
        // default (and backward compatible) behavior is to build a full report.
        // Optimized rendering is done in OnTheFlyRenderer and descendants
        mainReport = new Report();
    }

    public void startFileAnalysis(DataSource dataSource) {}
    
    public void renderFileReport(Report report) throws IOException {
        // default (and backward compatible) behavior is to build a full report.
        // Optimized rendering is done in OnTheFlyRenderer and descendants
        mainReport.merge(report);
    }

    public void end() throws IOException {
        // default (and backward compatible) behavior is to build a full report.
        // Optimized rendering is done in OnTheFlyRenderer and descendants
        render(writer, mainReport);
    }
}
