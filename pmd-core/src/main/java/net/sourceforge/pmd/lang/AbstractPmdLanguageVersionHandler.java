/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;

import net.sourceforge.pmd.lang.ast.AstProcessingStage;


/**
 * Base language version handler for languages that support PMD, i.e. can build an AST
 * and support AST processing stages.
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
public abstract class AbstractPmdLanguageVersionHandler extends AbstractLanguageVersionHandler {


    private final List<? extends AstProcessingStage<?>> processingStages;


    /**
     * Declare processing stages within an enum. An enum is the best way
     * to declare them since the illegality of forward references naturally
     * prevents circular dependencies to be declared. The natural ordering
     * on enums is also a sound and stable ordering for processing stages.
     *
     * @param processingStagesEnum Enum class
     * @param <T>                  Type of the enum class
     */
    protected <T extends Enum<T> & AstProcessingStage<T>> AbstractPmdLanguageVersionHandler(Class<T> processingStagesEnum) {
        this.processingStages = EnumUtils.getEnumList(processingStagesEnum);
    }


    /**
     * Declare no optional processing stages as of yet.
     */
    protected AbstractPmdLanguageVersionHandler() {
        this.processingStages = Collections.emptyList();
    }


    @Override
    public final List<? extends AstProcessingStage<?>> getProcessingStages() {
        return processingStages;
    }


}
