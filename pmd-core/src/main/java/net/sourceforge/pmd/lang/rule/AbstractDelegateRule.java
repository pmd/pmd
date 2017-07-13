/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.MultiValuePropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Base class for Rule implementations which delegate to another Rule instance.
 */
public abstract class AbstractDelegateRule implements Rule {

    private Rule rule;

    public Rule getRule() {
        return rule;
    }

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
    public Set<PropertyDescriptor<?>> ignoredProperties() {
        return rule.ignoredProperties();
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
    public ParserOptions getParserOptions() {
        return rule.getParserOptions();
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
    public <V> void setProperty(MultiValuePropertyDescriptor<V> propertyDescriptor, V value) {
        rule.setProperty(propertyDescriptor, value);
    }


    @Override
    public <V> void setProperty(MultiValuePropertyDescriptor<V> propertyDescriptor, V value1, V value2) {
        rule.setProperty(propertyDescriptor, value1, value2);
    }


    @Override
    public <V> void setProperty(MultiValuePropertyDescriptor<V> propertyDescriptor, V value1, V value2, V... values) {
        rule.setProperty(propertyDescriptor, value1, value2, values);
    }


    @Override
    public Map<PropertyDescriptor<?>, Object> getPropertiesByPropertyDescriptor() {
        return rule.getPropertiesByPropertyDescriptor();
    }

    @Override
    public void setUsesDFA() {
        rule.setUsesDFA();
    }

    @Override
    public boolean usesDFA() {
        return rule.usesDFA();
    }

    @Override
    public void setUsesTypeResolution() {
        rule.setUsesTypeResolution();
    }

    @Override
    public boolean usesTypeResolution() {
        return rule.usesTypeResolution();
    }

    @Override
    public void setUsesMetrics() {
        rule.setUsesMetrics();
    }

    @Override
    public boolean usesMetrics() {
        return rule.usesMetrics();
    }

    @Override
    public boolean usesRuleChain() {
        return rule.usesRuleChain();
    }

    @Override
    public List<String> getRuleChainVisits() {
        return rule.getRuleChainVisits();
    }

    @Override
    public void addRuleChainVisit(Class<? extends Node> nodeClass) {
        rule.addRuleChainVisit(nodeClass);
    }

    @Override
    public void addRuleChainVisit(String astNodeName) {
        rule.addRuleChainVisit(astNodeName);
    }

    @Override
    public void start(RuleContext ctx) {
        rule.start(ctx);
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        rule.apply(nodes, ctx);
    }

    @Override
    public void end(RuleContext ctx) {
        rule.end(ctx);
    }

    /**
     * @see Rule#hasDescriptor(PropertyDescriptor)
     */
    @Override
    public boolean hasDescriptor(PropertyDescriptor<?> descriptor) {
        return rule.hasDescriptor(descriptor);
    }
}
