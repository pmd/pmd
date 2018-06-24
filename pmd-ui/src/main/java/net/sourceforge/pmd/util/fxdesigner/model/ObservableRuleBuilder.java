/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.util.Optional;

import org.reactfx.collection.LiveArrayList;
import org.reactfx.value.Var;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.rules.RuleBuilder;
import net.sourceforge.pmd.util.fxdesigner.util.PropertyDescriptorSpec;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil.PersistentProperty;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil.PersistentSequence;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Holds info about a rule, and can build it to validate it.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ObservableRuleBuilder implements SettingsOwner {

    private Var<Language> language = Var.newSimpleVar(LanguageRegistry.getDefaultLanguage());
    private Var<String> name = Var.newSimpleVar("");
    private Var<Class<?>> clazz = Var.newSimpleVar(null);

    // doesn't contain the "xpath" and "version" properties for XPath rules
    private ListProperty<PropertyDescriptorSpec> ruleProperties = new SimpleListProperty<>(FXCollections.observableArrayList(PropertyDescriptorSpec.extractor()));
    private Var<ObservableList<String>> examples = Var.newSimpleVar(new LiveArrayList<>());

    private Var<LanguageVersion> minimumVersion = Var.newSimpleVar(null);
    private Var<LanguageVersion> maximumVersion = Var.newSimpleVar(null);

    private Var<String> since = Var.newSimpleVar("");

    private Var<String> message = Var.newSimpleVar("");
    private Var<String> externalInfoUrl = Var.newSimpleVar("");
    private Var<String> description = Var.newSimpleVar("");

    private Var<RulePriority> priority = Var.newSimpleVar(RulePriority.MEDIUM);
    private Var<Boolean> deprecated = Var.newSimpleVar(false);
    private Var<Boolean> usesDfa = Var.newSimpleVar(false);
    private Var<Boolean> usesMultifile = Var.newSimpleVar(false);
    private Var<Boolean> usesTypeResolution = Var.newSimpleVar(false);


    public Language getLanguage() {
        return language.getValue();
    }


    public void setLanguage(Language language) {
        this.language.setValue(language);
    }


    public Var<Language> languageProperty() {
        return language;
    }


    @PersistentProperty
    public String getName() {
        return name.getValue();
    }


    public void setName(String name) {
        this.name.setValue(name);
    }


    public Var<String> nameProperty() {
        return name;
    }


    @PersistentProperty
    public Class<?> getClazz() {
        return clazz.getValue();
    }


    public void setClazz(Class<?> clazz) {
        this.clazz.setValue(clazz);
    }


    public Var<Class<?>> clazzProperty() {
        return clazz;
    }


    @PersistentSequence
    public ObservableList<PropertyDescriptorSpec> getRuleProperties() {
        return ruleProperties.getValue();
    }


    public void setRuleProperties(ObservableList<PropertyDescriptorSpec> ruleProperties) {
        this.ruleProperties.setValue(ruleProperties);
    }


    public ListProperty<PropertyDescriptorSpec> rulePropertiesProperty() {
        return ruleProperties;
    }


    public LanguageVersion getMinimumVersion() {
        return minimumVersion.getValue();
    }


    public void setMinimumVersion(LanguageVersion minimumVersion) {
        this.minimumVersion.setValue(minimumVersion);
    }


    public Var<LanguageVersion> minimumVersionProperty() {
        return minimumVersion;
    }


    public LanguageVersion getMaximumVersion() {
        return maximumVersion.getValue();
    }


    public void setMaximumVersion(LanguageVersion maximumVersion) {
        this.maximumVersion.setValue(maximumVersion);
    }


    public Var<LanguageVersion> maximumVersionProperty() {
        return maximumVersion;
    }


    @PersistentProperty
    public String getSince() {
        return since.getValue();
    }


    public void setSince(String since) {
        this.since.setValue(since);
    }


    public Var<String> sinceProperty() {
        return since;
    }


    @PersistentProperty
    public String getMessage() {
        return message.getValue();
    }


    public void setMessage(String message) {
        this.message.setValue(message);
    }


    public Var<String> messageProperty() {
        return message;
    }


    @PersistentProperty
    public String getExternalInfoUrl() {
        return externalInfoUrl.getValue();
    }


    public void setExternalInfoUrl(String externalInfoUrl) {
        this.externalInfoUrl.setValue(externalInfoUrl);
    }


    public Var<String> externalInfoUrlProperty() {
        return externalInfoUrl;
    }


    @PersistentProperty
    public String getDescription() {
        return description.getValue();
    }


    public void setDescription(String description) {
        this.description.setValue(description);
    }


    public Var<String> descriptionProperty() {
        return description;
    }


    public Var<ObservableList<String>> getExamples() {
        return examples;
    }


    public void setExamples(ObservableList<String> examples) {
        this.examples.setValue(examples);
    }


    @PersistentProperty
    public RulePriority getPriority() {
        return priority.getValue();
    }


    public void setPriority(RulePriority priority) {
        this.priority.setValue(priority);
    }


    public Var<RulePriority> priorityProperty() {
        return priority;
    }


    public boolean isDeprecated() {
        return deprecated.getValue();
    }


    public void setDeprecated(boolean deprecated) {
        this.deprecated.setValue(deprecated);
    }


    public Var<Boolean> deprecatedProperty() {
        return deprecated;
    }


    public boolean isUsesDfa() {
        return usesDfa.getValue();
    }


    public void setUsesDfa(boolean usesDfa) {
        this.usesDfa.setValue(usesDfa);
    }


    public Var<Boolean> usesDfaProperty() {
        return usesDfa;
    }


    public boolean isUsesMultifile() {
        return usesMultifile.getValue();
    }


    public void setUsesMultifile(boolean usesMultifile) {
        this.usesMultifile.setValue(usesMultifile);
    }


    public Var<Boolean> usesMultifileProperty() {
        return usesMultifile;
    }


    public boolean getUsesTypeResolution() {
        return usesTypeResolution.getValue();
    }


    public void setUsesTypeResolution(boolean usesTypeResolution) {
        this.usesTypeResolution.setValue(usesTypeResolution);
    }


    public Var<Boolean> usesTypeResolutionProperty() {
        return usesTypeResolution;
    }


    /**
     * Returns true if the parameters of the rule are consistent and the rule can be built.
     *
     * @return whether the rule can be built
     */
    public boolean canBuild() {
        try {
            build();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    /**
     * Builds the rule.
     *
     * @return the built rule.
     *
     * @throws IllegalArgumentException if parameters are incorrect
     */
    public Optional<Rule> build() throws IllegalArgumentException {

        try {
            RuleBuilder builder = new RuleBuilder(name.getValue(),
                                                  clazz.getValue().getCanonicalName(),
                                                  language.getValue().getTerseName());

            builder.minimumLanguageVersion(minimumVersion.getValue().getTerseName());
            builder.maximumLanguageVersion(maximumVersion.getValue().getTerseName());

            builder.message(message.getValue());
            builder.since(since.getValue());
            builder.externalInfoUrl(externalInfoUrl.getValue());
            builder.description(description.getValue());
            builder.priority(priority.getValue().getPriority());

            builder.setDeprecated(deprecated.getValue());
            builder.usesDFA(usesDfa.getValue());
            builder.usesTyperesolution(usesTypeResolution.getValue());
            builder.usesMultifile(usesMultifile.getValue());

            ruleProperties.getValue().stream().map(PropertyDescriptorSpec::build).forEach(builder::defineProperty);
            examples.getValue().forEach(builder::addExample);

            return Optional.of(builder.build());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }

}
