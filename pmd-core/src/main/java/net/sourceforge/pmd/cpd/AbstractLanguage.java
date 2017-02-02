/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import net.sourceforge.pmd.util.filter.Filters;

public abstract class AbstractLanguage implements Language {
    private final String name;
    private final String terseName;
    private final Tokenizer tokenizer;
    private final FilenameFilter fileFilter;
    private final List<String> extensions;

    public AbstractLanguage(String name, String terseName, Tokenizer tokenizer, String... extensions) {
        this.name = name;
        this.terseName = terseName;
        this.tokenizer = tokenizer;
        fileFilter = Filters.toFilenameFilter(Filters.getFileExtensionOrDirectoryFilter(extensions));
        this.extensions = Arrays.asList(extensions);
    }

    @Override
    public FilenameFilter getFileFilter() {
        return fileFilter;
    }

    @Override
    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    @Override
    public void setProperties(Properties properties) {
        // needs to be implemented by subclasses.
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTerseName() {
        return terseName;
    }

    @Override
    public List<String> getExtensions() {
        return extensions;
    }
}
