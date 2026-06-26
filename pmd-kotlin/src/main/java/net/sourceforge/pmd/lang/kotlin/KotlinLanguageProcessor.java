/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.impl.BatchLanguageProcessor;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;

/**
 * Language processor for Kotlin. Extends the default batch processor with a
 * pre-analysis step: before any file is parsed, all Kotlin source files are
 * analyzed by kotlin-type-mapper to resolve types. The resulting type data is
 * then set as node attributes ({@code @TypeName}, {@code @ReturnTypeName}) on
 * declaration nodes, making them available in the PMD Designer and via XPath.
 *
 * <p>If type analysis fails (e.g. Kotlin compiler not on classpath), the processor
 * falls back gracefully: nodes have no type attributes and rule evaluation still
 * completes.
 *
 * @since 7.25.0
 */
public class KotlinLanguageProcessor extends BatchLanguageProcessor<KotlinLanguageProperties> {
    private static final Logger LOG = LoggerFactory.getLogger(KotlinLanguageProcessor.class);

    private final ExecutorService parseTimeoutExecutor =
            Executors.newCachedThreadPool(r -> {
                Thread t = new Thread(r, "kotlin-parse-timeout");
                t.setDaemon(true);
                return t;
            });

    private final KotlinHandler baseHandler;
    private final KotlinTypeAwarenessSupport typeAwareness;

    KotlinLanguageProcessor(KotlinLanguageProperties bundle) {
        super(bundle);
        this.baseHandler = new KotlinHandler(parseTimeoutExecutor);
        this.typeAwareness = new KotlinTypeAwarenessSupport(new KotlinAuxClasspathResolver(bundle));
    }

    @Override
    public @NonNull LanguageVersionHandler services() {
        return new AnnotatingKotlinHandler();
    }

    @Override
    public @NonNull AutoCloseable launchAnalysis(@NonNull AnalysisTask task) {
        typeAwareness.prepare(task.getFiles(), getLanguage());
        return super.launchAnalysis(task);
    }

    private final class AnnotatingKotlinHandler extends KotlinHandler {

        private AnnotatingKotlinHandler() {
            super(parseTimeoutExecutor);
        }

        @Override
        public XPathHandler getXPathHandler() {
            return baseHandler.getXPathHandler();
        }

        @Override
        public Parser getParser() {
            final Parser base = baseHandler.getParser();
            return task -> {
                KtKotlinFile root = (KtKotlinFile) base.parse(task);
                typeAwareness.annotateIfPossible(
                        root,
                        task.getTextDocument().getFileId().getAbsolutePath(),
                        task.getTextDocument().getText().toString());
                return root;
            };
        }
    }

    @Override
    public void close() throws Exception {
        parseTimeoutExecutor.shutdown();
        boolean result = parseTimeoutExecutor.awaitTermination(getProperties().getParseTimeoutSeconds() * 2L, TimeUnit.SECONDS);
        if (!result) {
            LOG.error("Couldn't properly shutdown parseTimeoutExecutor - threads might still be running!");
        }
        typeAwareness.clear();
        super.close();
    }
}
