/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.AbstractConfiguration;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.util.log.PmdReporter;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

public class TreeExportConfiguration extends AbstractConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(TreeExportConfiguration.class);

    private String format = "xml";
    private Language language = LanguageRegistry.PMD.getLanguageById("java");
    private Properties properties = new Properties();
    private Properties languageProperties = new Properties();
    private Path file;
    private boolean readStdin;
    private PmdReporter messageReporter = new SimpleMessageReporter(LOG);

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

    public PmdReporter getMessageReporter() {
        return messageReporter;
    }

    public void setMessageReporter(PmdReporter messageReporter) {
        this.messageReporter = messageReporter;
    }

    @Override
    public List<Path> getRelativizeRoots() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addRelativizeRoot(Path path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addRelativizeRoots(List<Path> paths) {
        throw new UnsupportedOperationException();
    }
}
