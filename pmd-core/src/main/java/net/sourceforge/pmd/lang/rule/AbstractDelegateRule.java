/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.MultiValuePropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;

/**
 * Base class for Rule implementations which delegate to another Rule instance.
 *
 * @deprecated This is only relevant to {@link RuleReference}, but prevents sharing the implementation
 * of {@link net.sourceforge.pmd.properties.AbstractPropertySource}. Will be removed in 7.0.0
 */
@Deprecated
public abstract class AbstractDelegateRule implements Rule {

    private Rule rule;

    public Rule getRule() {
        return rule;
    }


    /**
     * @deprecated This will be removed in 7.0.0
     * I mark it specially deprecated because it's inherited by rule reference,
     * even though a RuleReference has no business setting its rule after construction
     */
    @Deprecated
    public void setRule(Rule rule) {
        this.rule = rule;
    }

    @Override
    public Language getLanguage() {
        return rule.getLanguage();
    }

    @Override
    public void setLanguage(Language language) {
        rule.setLanguage(language);
    }

    @Override
    public LanguageVersion getMinimumLanguageVersion() {
        return rule.getMinimumLanguageVersion();
    }

    @Override
    public void setMinimumLanguageVersion(LanguageVersion minimumlanguageVersion) {
        rule.setMinimumLanguageVersion(minimumlanguageVersion);
    }

    @Override
    public LanguageVersion getMaximumLanguageVersion() {
        return rule.getMaximumLanguageVersion();
    }

    @Override
    public void setMaximumLanguageVersion(LanguageVersion maximumlanguageVersion) {
        rule.setMaximumLanguageVersion(maximumlanguageVersion);
    }

    @Override
    public boolean isDeprecated() {
        return rule.isDeprecated();
    }

    @Override
    public void setDeprecated(boolean deprecated) {
        rule.setDeprecated(deprecated);
    }

    /**
     * @see PropertySource#dysfunctionReason()
     */
    @Override
    public String dysfunctionReason() {
        return rule.dysfunctionReason();
    }

    @Override
    public String getName() {
        return rule.getName();
    }

    @Override
    public void setName(String name) {
        rule.setName(name);
    }

    @Override
    public String getSince() {
        return rule.getSince();
    }

    @Override
    public void setSince(String since) {
        rule.setSince(since);
    }

    @Override
    public String getRuleClass() {
        return rule.getRuleClass();
    }

    @Override
    public void setRuleClass(String ruleClass) {
        rule.setRuleClass(ruleClass);
    }

    @Override
    public String getRuleSetName() {
        return rule.getRuleSetName();
    }

    @Override
    public void setRuleSetName(String name) {
        rule.setRuleSetName(name);
    }

    @Override
    public String getMessage() {
        return rule.getMessage();
    }

    @Override
    public void setMessage(String message) {
        rule.setMessage(message);
    }

    @Override
    public String getDescription() {
        return rule.getDescription();
    }

    @Override
    public void setDescription(String description) {
        rule.setDescription(description);
    }

    @Override
    public List<String> getExamples() {
        return rule.getExamples();
    }

    @Override
    public void addExample(String example) {
        rule.addExample(example);
    }

    @Override
    public String getExternalInfoUrl() {
        return rule.getExternalInfoUrl();
    }

    @Override
    public void setExternalInfoUrl(String url) {
        rule.setExternalInfoUrl(url);
    }

    @Override
    public RulePriority getPriority() {
        return rule.getPriority();
    }

    @Override
    public void setPriority(RulePriority priority) {
        rule.setPriority(priority);
    }

    @Override
    public void definePropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) throws IllegalArgumentException {
        rule.definePropertyDescriptor(propertyDescriptor);
    }

    @Override
    public PropertyDescriptor<?> getPropertyDescriptor(String name) {
        return rule.getPropertyDescriptor(name);
    }

    @Override
    public List<PropertyDescriptor<?>> getPropertyDescriptors() {
        return rule.getPropertyDescriptors();
    }

    @Override
    public <T> T getProperty(PropertyDescriptor<T> propertyDescriptor) {
        return rule.getProperty(propertyDescriptor);
    }

    @Override
    public <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value) {
        rule.setProperty(propertyDescriptor, value);
    }


    @Override
    public <V> void setProperty(MultiValuePropertyDescriptor<V> propertyDescriptor, V... values) {
        rule.setProperty(propertyDescriptor, values);
    }

    @Override
    public boolean isPropertyOverridden(PropertyDescriptor<?> propertyDescriptor) {
        return rule.isPropertyOverridden(propertyDescriptor);
    }

    @Override
    public Map<PropertyDescriptor<?>, Object> getPropertiesByPropertyDescriptor() {
        return rule.getPropertiesByPropertyDescriptor();
    }

    @Override
    public RuleTargetSelector getTargetSelector() {
        return rule.getTargetSelector();
    }

    @Override
    public void start(RuleContext ctx) {
        rule.start(ctx);
    }

    @Override
    public void apply(Node target, RuleContext ctx) {
        rule.apply(target, ctx);
    }

    @Override
    public void end(RuleContext ctx) {
        rule.end(ctx);
    }

    @Override
    public void initialize(LanguageProcessor languageProcessor) {
        rule.initialize(languageProcessor);
    }

    /**
     * @see Rule#hasDescriptor(PropertyDescriptor)
     */
    @Override
    public boolean hasDescriptor(PropertyDescriptor<?> descriptor) {
        return rule.hasDescriptor(descriptor);
    }
}
