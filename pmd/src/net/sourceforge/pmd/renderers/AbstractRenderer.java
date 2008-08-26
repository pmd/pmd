package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 */
public abstract class AbstractRenderer implements Renderer {

    protected boolean showSuppressedViolations = true;
    private Writer writer;
    private Report mainReport;

    /**
     * Method showSuppressedViolations.
     * @param show boolean
     * @see net.sourceforge.pmd.renderers.Renderer#showSuppressedViolations(boolean)
     */
    public void showSuppressedViolations(boolean show) {
        this.showSuppressedViolations = show;
    }

    /**
     * Method render.
     * @param report Report
     * @return String
     * @see net.sourceforge.pmd.renderers.Renderer#render(Report)
     */
    public String render(Report report) {
        StringWriter w = new StringWriter();
        try {
            render(w, report);
        } catch (IOException e) {
            throw new Error("StringWriter doesn't throw IOException", e);
        }
        return w.toString();
    }


    /**
     * Method setWriter.
     * @param writer Writer
     * @see net.sourceforge.pmd.renderers.Renderer#setWriter(Writer)
     */
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    /**
     * Method getWriter.
     * @return Writer
     * @see net.sourceforge.pmd.renderers.Renderer#getWriter()
     */
    public Writer getWriter() {
        return writer;
    }

    /**
     * Method start.
     * @throws IOException
     * @see net.sourceforge.pmd.renderers.Renderer#start()
     */
    public void start() throws IOException {
        // default (and backward compatible) behavior is to build a full report.
        // Optimized rendering is done in OnTheFlyRenderer and descendants
        mainReport = new Report();
    }

    /**
     * Method startFileAnalysis.
     * @param dataSource DataSource
     * @see net.sourceforge.pmd.renderers.Renderer#startFileAnalysis(DataSource)
     */
    public void startFileAnalysis(DataSource dataSource) {}
    
    /**
     * Method renderFileReport.
     * @param report Report
     * @throws IOException
     * @see net.sourceforge.pmd.renderers.Renderer#renderFileReport(Report)
     */
    public void renderFileReport(Report report) throws IOException {
        // default (and backward compatible) behavior is to build a full report.
        // Optimized rendering is done in OnTheFlyRenderer and descendants
        mainReport.merge(report);
    }

    /**
     * Method end.
     * @throws IOException
     * @see net.sourceforge.pmd.renderers.Renderer#end()
     */
    public void end() throws IOException {
        // default (and backward compatible) behavior is to build a full report.
        // Optimized rendering is done in OnTheFlyRenderer and descendants
        render(writer, mainReport);
    }
}
