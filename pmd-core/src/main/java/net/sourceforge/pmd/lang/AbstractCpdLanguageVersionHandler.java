/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;


/**
 * Base language version handler for languages that only support CPD.
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
public abstract class AbstractCpdLanguageVersionHandler extends AbstractLanguageVersionHandler {
    @Override
    public List<? extends AstProcessingStage<?>> getProcessingStages() {
        return Collections.emptyList();
    }


    protected abstract String getLanguageName();


    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        throw new UnsupportedOperationException("getRuleViolationFactory() is not supported for " + getLanguageName());
    }
}
