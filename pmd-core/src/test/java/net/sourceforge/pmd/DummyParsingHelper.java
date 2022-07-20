/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.Collections;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.util.log.internal.NoopReporter;

/**
 * @author Clément Fournier
 */
public class DummyParsingHelper implements Extension, BeforeEachCallback, AfterEachCallback {

    private LanguageProcessor dummyProcessor;

    public DummyParsingHelper() {

    }

    public DummyRootNode parse(String code) {
        return parse(code, TextFile.UNKNOWN_FILENAME);
    }

    public DummyRootNode parse(String code, String filename) {
        LanguageVersion version = DummyLanguageModule.getInstance().getDefaultVersion();
        ParserTask task = new ParserTask(
            TextDocument.readOnlyString(code, filename, version),
            SemanticErrorReporter.noop(),
            LanguageProcessorRegistry.singleton(dummyProcessor));
        return (DummyRootNode) dummyProcessor.services().getParser().parse(task);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        dummyProcessor.close();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        LanguageProcessorRegistry registry = LanguageProcessorRegistry.create(
            LanguageRegistry.PMD,
            Collections.emptyMap(),
            new NoopReporter()
        );
        dummyProcessor = registry.getProcessor(DummyLanguageModule.getInstance());
    }
}
