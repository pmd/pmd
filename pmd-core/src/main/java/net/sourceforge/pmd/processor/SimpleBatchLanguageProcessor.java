/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageVersionHandler;

/**
 * A {@link BatchLanguageProcessor} with a simple predetermined
 * {@link LanguageVersionHandler}.
 *
 * @author Clément Fournier
 */
public class SimpleBatchLanguageProcessor extends BatchLanguageProcessor<LanguagePropertyBundle> {
    private final LanguageVersionHandler handler;

    public SimpleBatchLanguageProcessor(LanguagePropertyBundle bundle, LanguageVersionHandler handler) {
        super(bundle);
        this.handler = handler;
    }

    @Override
    public @NonNull LanguageVersionHandler services() {
        return handler;
    }
}
