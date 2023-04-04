/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Properties;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;

@Experimental
public class TreeExporter {

    private final TreeExportConfiguration configuration;
    private final Io io;
    
    public TreeExporter(final TreeExportConfiguration configuration) {
        this(configuration, Io.system());
    }
    
    TreeExporter(final TreeExportConfiguration configuration, final Io io) {
        this.configuration = configuration;
        this.io = io;
    }
    
    public void export() throws IOException {
        TreeRendererDescriptor descriptor = TreeRenderers.findById(configuration.getFormat());
        if (descriptor == null) {
            throw this.bail("Unknown format '" + configuration.getFormat() + "'");
        }

        PropertySource bundle = parseProperties(descriptor.newPropertyBundle(), configuration.getProperties());
        LanguagePropertyBundle langProperties = parseProperties(configuration.getLanguage().newPropertyBundle(), configuration.getLanguageProperties());

        LanguageRegistry lang = LanguageRegistry.PMD.getDependenciesOf(configuration.getLanguage());
        try (LanguageProcessorRegistry lpRegistry = LanguageProcessorRegistry.create(lang,
                                                                                     Collections.singletonMap(configuration.getLanguage(), langProperties),
                                                                                     configuration.getMessageReporter())) {
            run(lpRegistry, descriptor.produceRenderer(bundle));
        }
    }
    
    private void run(LanguageProcessorRegistry langRegistry, final TreeRenderer renderer) throws IOException {
        printWarning();

        LanguageVersion langVersion = configuration.getLanguage().getDefaultVersion();
        @SuppressWarnings("PMD.CloseResource")
        LanguageProcessor processor = langRegistry.getProcessor(configuration.getLanguage());
        Parser parser = processor.services().getParser();

        @SuppressWarnings("PMD.CloseResource")
        TextFile textFile;
        if (configuration.isReadStdin()) {
            io.stderr.println("Reading from stdin...");
            textFile = TextFile.forReader(readFromSystemIn(), "stdin", langVersion);
        } else {
            textFile = TextFile.forPath(configuration.getFile(), configuration.getSourceEncoding(), langVersion);
        }

        // disable warnings for deprecated attributes
        Slf4jSimpleConfiguration.disableLogging(Attribute.class);

        try (TextDocument textDocument = TextDocument.create(textFile)) {

            ParserTask task = new ParserTask(textDocument, SemanticErrorReporter.noop(), langRegistry);
            RootNode root = parser.parse(task);

            renderer.renderSubtree(root, io.stdout);
        }
    }

    private Reader readFromSystemIn() {
        return new BufferedReader(new InputStreamReader(io.stdin));
    }

    private void printWarning() {
        io.stderr.println("-------------------------------------------------------------------------------");
        io.stderr.println("This command line utility is experimental. It might change at any time without");
        io.stderr.println("prior notice.");
        io.stderr.println("-------------------------------------------------------------------------------");
    }

    private <T extends PropertySource> T parseProperties(T bundle, Properties properties) {

        for (Entry<Object, Object> prop : properties.entrySet()) {
            PropertyDescriptor<?> d = bundle.getPropertyDescriptor(prop.getKey().toString());
            if (d == null) {
                throw bail("Unknown property '" + prop.getKey() + "'");
            }

            setProperty(d, bundle, prop.getValue().toString());
        }
        return bundle;
    }
    
    private <T> void setProperty(PropertyDescriptor<T> descriptor, PropertySource bundle, String value) {
        bundle.setProperty(descriptor, descriptor.valueFrom(value));
    }
    
    private AbortedException bail(String message) {
        io.stderr.println(message);
        io.stderr.println("Use --help for usage information");
        return new AbortedException();
    }

    private static final class AbortedException extends RuntimeException {

        private static final long serialVersionUID = -1925142332978792215L;
    }
}
