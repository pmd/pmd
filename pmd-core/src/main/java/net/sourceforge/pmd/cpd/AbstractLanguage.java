/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sourceforge.pmd.internal.util.PredicateUtil;

public abstract class AbstractLanguage implements Language {
    private final String name;
    private final String terseName;
    private final Tokenizer tokenizer;
    private final Predicate<String> fileFilter;
    private final List<String> extensions;

    public AbstractLanguage(String name, String terseName, Tokenizer tokenizer, String... extensions) {
        this(name, terseName, tokenizer, Arrays.asList(extensions));
    }

    protected AbstractLanguage(String name, String terseName, Tokenizer tokenizer, List<String> extensions) {
        this.name = name;
        this.terseName = terseName;
        this.tokenizer = tokenizer;
        List<String> extensionsWithDot = extensions.stream().map(e -> {
            if (e.length() > 0 && e.charAt(0) != '.') {
                return "." + e;
            }
            return e;
        }).collect(Collectors.toList());
        this.fileFilter = PredicateUtil.toNormalizedFileFilter(
                PredicateUtil.getFileExtensionFilter(extensionsWithDot.toArray(new String[0]))
                        .or(it -> Files.isDirectory(Paths.get(it))));
        this.extensions = extensionsWithDot;
    }

    @Override
    public FilenameFilter getFileFilter() {
        return (dir, name) -> fileFilter.test(dir.toPath().resolve(name).toString());
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
