/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileAnalysis;
import net.sourceforge.pmd.lang.impl.BatchLanguageProcessor;

public class ApexLanguageProcessor
    extends BatchLanguageProcessor<ApexLanguageProperties> {

    private final ApexMultifileAnalysis multifileAnalysis;
    private final ApexLanguageHandler services;

    ApexLanguageProcessor(ApexLanguageProperties bundle) {
        super(bundle);
        this.multifileAnalysis = new ApexMultifileAnalysis(bundle);
        this.services = new ApexLanguageHandler();
    }

    @Override
    public @NonNull LanguageVersionHandler services() {
        return services;
    }

    public ApexMultifileAnalysis getMultiFileState() {
        return multifileAnalysis;
    }

}
