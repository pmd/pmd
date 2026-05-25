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
import net.sourceforge.pmd.lang.impl.BatchLanguageProcessor;

/**
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

    private final KotlinHandler kotlinHandler;

    KotlinLanguageProcessor(KotlinLanguageProperties bundle) {
        super(bundle);
        kotlinHandler = new KotlinHandler(parseTimeoutExecutor);
    }

    @Override
    public @NonNull LanguageVersionHandler services() {
        return kotlinHandler;
    }

    @Override
    public void close() throws Exception {
        parseTimeoutExecutor.shutdown();
        boolean result = parseTimeoutExecutor.awaitTermination(getProperties().getParseTimeoutSeconds() * 2L, TimeUnit.SECONDS);
        if (!result) {
            LOG.error("Couldn't properly shutdown parseTimeoutExecutor - threads might still be running!");
        }
        super.close();
    }
}
