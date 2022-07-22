/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import java.util.function.Function;

import net.sourceforge.pmd.lang.LanguageModuleBase;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.processor.BatchLanguageProcessor;

/**
 * The simplest implementation of a language, where only a {@link LanguageVersionHandler}
 * needs to be implemented.
 *
 * @author Clément Fournier
 */
public class SimpleLanguageModuleBase extends LanguageModuleBase {

    private final Function<LanguagePropertyBundle, LanguageVersionHandler> handler;

    protected SimpleLanguageModuleBase(LanguageMetadata metadata, LanguageVersionHandler handler) {
        this(metadata, p -> handler);
    }

    public SimpleLanguageModuleBase(LanguageMetadata metadata, Function<LanguagePropertyBundle, LanguageVersionHandler> makeHandler) {
        super(metadata);
        this.handler = makeHandler;
    }

    @Override
    public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        LanguageVersionHandler services = handler.apply(bundle);
        return new BatchLanguageProcessor<LanguagePropertyBundle>(bundle) {
            @Override
            public LanguageVersionHandler services() {
                return services;
            }
        };
    }

}
