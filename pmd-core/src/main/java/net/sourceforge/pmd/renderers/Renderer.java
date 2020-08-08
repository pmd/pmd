/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Report.ReportBuilderListener;
import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * This is an interface for rendering a Report. When a Renderer is being
 * invoked, the sequence of method calls is something like the following:
 * <ol>
 * <li>Renderer construction/initialization</li>
 * <li>{@link Renderer#setShowSuppressedViolations(boolean)}</li>
 * <li>{@link Renderer#setUseShortNames(List)}</li>
 * <li>{@link Renderer#setWriter(Writer)}</li>
 * <li>{@link Renderer#start()}</li>
 * <li>{@link Renderer#startFileAnalysis(DataSource)} for each source file
 * processed</li>
 * <li>{@link Renderer#renderFileReport(Report)} for each Report instance</li>
 * <li>{@link Renderer#end()}</li>
 * </ol>
 * <p>
 * An implementation of the Renderer interface is expected to have a default
 * constructor. Properties should be defined using the
 * {@link #definePropertyDescriptor(PropertyDescriptor)}
 * method. After the instance is created, the property values are set. This
 * means, you won't have access to property values in your constructor.
 */
// TODO Are implementations expected to be thread-safe?
public interface Renderer extends PropertySource {

    /**
     * Get the name of the Renderer.
     *
     * @return The name of the Renderer.
     */
    @Override
    String getName();

    /**
     * Set the name of the Renderer.
     *
     * @param name
     *            The name of the Renderer.
     */
    void setName(String name);

    /**
     * Get the description of the Renderer.
     *
     * @return The description of the Renderer.
     */
    String getDescription();

    /**
     * Return the default filename extension to use.
     *
     * @return String
     */
    String defaultFileExtension();

    /**
     * Set the description of the Renderer.
     *
     * @param description
     *            The description of the Renderer.
     */
    void setDescription(String description);

    /**
     * Get the indicator for whether to show suppressed violations.
     *
     * @return <code>true</code> if suppressed violations should show,
     *         <code>false</code> otherwise.
     */
    boolean isShowSuppressedViolations();

    /**
     * Set the indicator for whether to show suppressed violations.
     *
     * @param showSuppressedViolations
     *            Whether to show suppressed violations.
     */
    void setShowSuppressedViolations(boolean showSuppressedViolations);

    /**
     * Render the filenames of found violations with short names. That is, any prefix
     * given as inputPaths is removed.
     * By default, the full pathnames are used. If the given list of {@code inputPaths}
     * is empty, then the full pathnames are used.
     *
     * @param inputPaths
     */
    void setUseShortNames(List<String> inputPaths);

    /**
     * Get the Writer for the Renderer.
     *
     * @return The Writer.
     */
    Writer getWriter();

    /**
     * Set the Writer for the Renderer.
     *
     * @param writer
     *            The Writer.
     */
    void setWriter(Writer writer);

    /**
     * This method is called before any source files are processed. The Renderer
     * will have been fully initialized by the time this method is called, so
     * the Writer and other state will be available.
     *
     * @throws IOException
     */
    void start() throws IOException;

    /**
     * This method is called each time a source file is processed. It is called
     * after {@link Renderer#start()}, but before
     * {@link Renderer#renderFileReport(Report)} and {@link Renderer#end()}.
     *
     * This method may be invoked by different threads which are processing
     * files independently. Therefore, any non-trivial implementation of this
     * method needs to be thread-safe.
     *
     * @param dataSource
     *            The source file.
     */
    void startFileAnalysis(DataSource dataSource);

    /**
     * Render the given file Report. There may be multiple Report instances
     * which need to be rendered if produced by different threads. It is called
     * after {@link Renderer#start()} and
     * {@link Renderer#startFileAnalysis(DataSource)}, but before
     * {@link Renderer#end()}.
     *
     * @param report
     *            A file Report.
     * @throws IOException
     *
     * @see Report
     */
    void renderFileReport(Report report) throws IOException;

    /**
     * This method is at the very end of the Rendering process, after
     * {@link Renderer#renderFileReport(Report)}.
     *
     * @throws IOException
     */
    void end() throws IOException;

    void flush() throws IOException;

    /**
     * Sets the filename where the report should be written to. If no filename is provided,
     * the renderer should write to stdout.
     *
     * <p>Implementations must initialize the writer of the renderer.
     *
     * <p>See {@link AbstractRenderer#setReportFile(String)} for the default impl.
     *
     * @param reportFilename the filename (optional).
     */
    @Experimental
    void setReportFile(String reportFilename);



    /**
     * Returns a new analysis listener, that handles violations by rendering
     * them in an implementation-defined way.
     */
    // TODO the default implementation matches the current behavior,
    //  ie violations are batched by file and forwarded to the renderer
    //  when the file is done. Many renderers could directly handle
    //  violations as they come though.
    default GlobalAnalysisListener newListener() throws IOException {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.REPORTING)) {
            this.start();
        }

        return new GlobalAnalysisListener() {

            // guard for the close routine
            final Object reportMergeLock = new Object();

            @Override
            public FileAnalysisListener startFileAnalysis(DataSource file) {
                Renderer renderer = Renderer.this;

                renderer.startFileAnalysis(file); // this routine is thread-safe by contract
                return new FileAnalysisListener() {
                    final ReportBuilderListener reportBuilder = new ReportBuilderListener();

                    @Override
                    public void onRuleViolation(RuleViolation violation) {
                        reportBuilder.onRuleViolation(violation);
                    }

                    @Override
                    public void onSuppressedRuleViolation(SuppressedViolation violation) {
                        reportBuilder.onSuppressedRuleViolation(violation);
                    }

                    @Override
                    public void onError(ProcessingError error) {
                        reportBuilder.onError(error);
                    }

                    @Override
                    public void close() throws Exception {
                        reportBuilder.close();
                        synchronized (reportMergeLock) {
                            // TODO renderFileReport should be thread-safe instead
                            try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.REPORTING)) {
                                renderer.renderFileReport(reportBuilder.getResult());
                            }
                        }
                    }
                };
            }

            @Override
            public void close() throws Exception {
                try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.REPORTING)) {
                    end();
                    flush();
                }
            }
        };
    }
}
