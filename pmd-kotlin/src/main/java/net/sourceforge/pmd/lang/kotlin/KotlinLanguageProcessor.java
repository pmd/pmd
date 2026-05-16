/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.JvmLanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.impl.BatchLanguageProcessor;

/**
 * Language processor for Kotlin.
 *
 * <p>Wires the {@link JvmLanguagePropertyBundle} (which carries the {@code auxClasspath}
 * property) into the processing pipeline, so users can pass {@code --aux-classpath} on
 * the command line and it will be picked up by Kotlin analysis.
 *
 * <p>The constructor signature is intentionally compatible with the type-mapper extension
 * that will be added in a later PR.
 */
class KotlinLanguageProcessor extends BatchLanguageProcessor<JvmLanguagePropertyBundle> {

    private final KotlinHandler handler;

    KotlinLanguageProcessor(JvmLanguagePropertyBundle bundle, KotlinHandler handler) {
        super(bundle);
        this.handler = handler;
    }

    @Override
    public @NonNull LanguageVersionHandler services() {
        return handler;
    }
}
