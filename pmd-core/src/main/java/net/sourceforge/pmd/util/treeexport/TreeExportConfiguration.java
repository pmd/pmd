/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.AbstractConfiguration;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.util.log.MessageReporter;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

public class TreeExportConfiguration extends AbstractConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(TreeExportConfiguration.class);
    protected Path reportFile;

    private String format = "xml";
    private Language language = LanguageRegistry.PMD.getLanguageById("java");
    private Properties properties = new Properties();
    private Properties languageProperties = new Properties();
    private Path file;
    private boolean readStdin;
    private MessageReporter messageReporter = new SimpleMessageReporter(LOG);

    public TreeExportConfiguration(LanguageRegistry registry) {
        super(registry, new SimpleMessageReporter(LoggerFactory.getLogger(TreeExporter.class)));
    }

    public TreeExportConfiguration() {
        this(LanguageRegistry.PMD);
    }

    public String getFormat() {
        return format;
    }

    public Language getLanguage() {
        return language;
    }

    public Properties getProperties() {
        return properties;
    }
    
    public Path getFile() {
        return file;
    }

    public Properties getLanguageProperties() {
        return languageProperties;
    }

    public boolean isReadStdin() {
        return readStdin;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public void setLanguage(Language language) {
        this.language = language;
    }
    
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setLanguageProperties(Properties properties) {
        this.languageProperties = properties;
    }

    public void setFile(Path file) {
        this.file = file;
    }
    
    public void setReadStdin(boolean readStdin) {
        this.readStdin = readStdin;
    }

    public MessageReporter getMessageReporter() {
        return messageReporter;
    }

    public void setMessageReporter(MessageReporter messageReporter) {
        this.messageReporter = messageReporter;
    }

    /**
     * Get the file to which the report should render.
     *
     * @return The file to which to render.
     * @deprecated Use {@link #getReportFilePath()}
     */
    @Deprecated
    public String getReportFile() {
        return reportFile == null ? null : reportFile.toString();
    }

    /**
     * Get the file to which the report should render.
     *
     * @return The file to which to render.
     */
    public Path getReportFilePath() {
        return reportFile;
    }

    /**
     * Set the file to which the report should render.
     *
     * @param reportFile the file to set
     * @deprecated Use {@link #setReportFile(Path)}
     */
    @Deprecated
    public void setReportFile(String reportFile) {
        this.reportFile = reportFile == null ? null : Paths.get(reportFile);
    }

    /**
     * Set the file to which the report should render.
     *
     * @param reportFile the file to set
     */
    public void setReportFile(Path reportFile) {
        this.reportFile = reportFile;
    }
}
