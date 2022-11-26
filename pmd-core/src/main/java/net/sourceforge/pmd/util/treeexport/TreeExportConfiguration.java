/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.nio.file.Path;
import java.util.Properties;

import net.sourceforge.pmd.AbstractConfiguration;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;

public class TreeExportConfiguration extends AbstractConfiguration {

    private String format = "xml";
    private Language language = LanguageRegistry.PMD.getLanguageById("java");
    private Properties properties = new Properties();
    private Path file;
    private boolean readStdin;
    
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
    
    public void setFile(Path file) {
        this.file = file;
    }
    
    public void setReadStdin(boolean readStdin) {
        this.readStdin = readStdin;
    }
}
