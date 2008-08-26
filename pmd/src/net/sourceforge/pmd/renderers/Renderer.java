/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 */
public interface Renderer {

    /**
     * Method showSuppressedViolations.
     * @param show boolean
     */
    void showSuppressedViolations(boolean show);

    /**
     * 
     * @deprecated This method consumes too much memory.
     * Use the render method with the Writer argument instead.
     * 
     * @param report Report
     * @return String
     */
    String render(Report report);

    /**
     * 
     * @deprecated This method consumes too much memory.
     * Use the start, renderFileReport and end methods instead.
     * 
     * @param writer Writer
     * @param report Report
     * @throws IOException
     */
    void render(Writer writer, Report report) throws IOException;

    /**
     * Method setWriter.
     * @param writer Writer
     */
    void setWriter(Writer writer);

    /**
     * Method getWriter.
     * @return Writer
     */
    Writer getWriter();

    /**
     * Method start.
     * @throws IOException
     */
    void start() throws IOException;

    /**
     * Method startFileAnalysis.
     * @param dataSource DataSource
     */
    void startFileAnalysis(DataSource dataSource);

    /**
     * Method renderFileReport.
     * @param report Report
     * @throws IOException
     */
    void renderFileReport(Report report) throws IOException;

    /**
     * Method end.
     * @throws IOException
     */
    void end() throws IOException;

}
