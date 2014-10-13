/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * This is an interface for rendering a Report.  When a Renderer is being
 * invoked, the sequence of method calls is something like the following:
 * <ol>
 * 	<li>Renderer construction/initialization</li>
 * 	<li>{@link Renderer#setShowSuppressedViolations(boolean)}</li>
 * 	<li>{@link Renderer#setWriter(Writer)}</li>
 * 	<li>{@link Renderer#start()}</li>
 * 	<li>{@link Renderer#startFileAnalysis(DataSource)} for each source file processed</li>
 * 	<li>{@link Renderer#renderFileReport(Report)} for each Report instance</li>
 * 	<li>{@link Renderer#end()}</li>
 * </ol>
 * <p>
 * An implementation of the Renderer interface is expected to have a default constructor.
 * Properties should be defined using the {@link #definePropertyDescriptor(net.sourceforge.pmd.PropertyDescriptor)}
 * method. After the instance is created, the property values are set. This means, you won't
 * have access to property values in your constructor.
 */
// TODO Are implementations expected to be thread-safe?
public interface Renderer extends PropertySource {

    /**
     * Get the name of the Renderer.
     * @return The name of the Renderer.
     */
    String getName();

    /**
     * Set the name of the Renderer.
     * @param name The name of the Renderer.
     */
    void setName(String name);

    /**
     * Get the description of the Renderer.
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
     * @param description The description of the Renderer.
     */
    void setDescription(String description);

    /**
     * Get the configuration property definitions for Renderer.
     * The keys in the map are the configuration property names, with the
     * corresponding value being a description.
     * @return The configuration property definition map.
     */
    @Deprecated // use PropertySource.getPropertyDescriptors() instead
    Map<String, String> getPropertyDefinitions();

    /**
     * Get the indicator for whether to show suppressed violations. 
     * @return <code>true</code> if suppressed violations should show, <code>false</code> otherwise.
     */
    boolean isShowSuppressedViolations();

    /**
     * Set the indicator for whether to show suppressed violations. 
     * @param showSuppressedViolations Whether to show suppressed violations.
     */
    void setShowSuppressedViolations(boolean showSuppressedViolations);

    /**
     * Get the Writer for the Renderer.
     * @return The Writer.
     */
    Writer getWriter();

    /**
     * Set the Writer for the Renderer.
     * @param writer The Writer.
     */
    void setWriter(Writer writer);

    /**
     * This method is called before any source files are processed.
     * The Renderer will have been fully initialized by the time this method
     * is called, so the Writer and other state will be available.
     * @throws IOException
     */
    void start() throws IOException;

    /**
     * This method is called each time a source file is processed.  It is called
     * after {@link Renderer#start()}, but before
     * {@link Renderer#renderFileReport(Report)} and {@link Renderer#end()}.
     * 
     * This method may be invoked by different threads which are processing
     * files independently.  Therefore, any non-trivial implementation of this
     * method needs to be thread-safe.
     * 
     * @param dataSource The source file.
     */
    void startFileAnalysis(DataSource dataSource);

    /**
     * Render the given file Report.  There may be multiple Report instances
     * which need to be rendered if produced by different threads.
     * It is called after {@link Renderer#start()} and
     * {@link Renderer#startFileAnalysis(DataSource)}, but before {@link Renderer#end()}.
     * 
     * @param report A file Report.
     * @throws IOException
     * 
     * @see Report
     */
    void renderFileReport(Report report) throws IOException;

    /**
     * This method is at the very end of the Rendering process, after
     * {@link Renderer#renderFileReport(Report)}.
     * @throws IOException
     */
    void end() throws IOException;
    
    void flush() throws IOException;
}
