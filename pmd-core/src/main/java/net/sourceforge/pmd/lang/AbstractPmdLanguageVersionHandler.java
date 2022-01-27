/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.EnumUtils;

import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;


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



    /**
     * Returns the environment variable name that a user can set in order to override the default value.
     */
    String getEnvironmentVariableName(String langTerseName, PropertyDescriptor<?> propertyDescriptor) {
        if (langTerseName == null) {
            throw new IllegalStateException("Language is null");
        }
        return "PMD_" + langTerseName.toUpperCase(Locale.ROOT) + "_"
            + propertyDescriptor.name().toUpperCase(Locale.ROOT);
    }

    /**
     * @return environment variable that overrides the PropertyDesciptors default value. Returns null if no environment
     *     variable has been set.
     */

    String getEnvValue(String langTerseName, PropertyDescriptor<?> propertyDescriptor) {
        // note: since we use environent variables and not system properties,
        // tests override this method.
        return System.getenv(getEnvironmentVariableName(langTerseName, propertyDescriptor));
    }

    /**
     * Overrides the default PropertyDescriptors with values found in environment variables.
     * TODO: Move this to net.sourceforge.pmd.PMD#parserFor when CLI options are implemented
     */
    @Deprecated
    protected final void overridePropertiesFromEnv(String langTerseName, PropertySource source) {
        for (PropertyDescriptor<?> propertyDescriptor : source.getPropertyDescriptors()) {
            String propertyValue = getEnvValue(langTerseName, propertyDescriptor);

            if (propertyValue != null) {
                setPropertyCapture(source, propertyDescriptor, propertyValue);
            }
        }
    }

    @Deprecated
    private <T> void setPropertyCapture(PropertySource source, PropertyDescriptor<T> propertyDescriptor, String propertyValue) {
        T value = propertyDescriptor.valueFrom(propertyValue);
        source.setProperty(propertyDescriptor, value);
    }

}
