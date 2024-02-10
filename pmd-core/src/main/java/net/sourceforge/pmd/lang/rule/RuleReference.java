/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.internal.RuleSetReference;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.util.StringUtil;

/**
 * This class represents a Rule which is a reference to Rule defined in another
 * RuleSet. All details of the Rule are delegated to the underlying referenced
 * Rule, but those operations which modify overridden aspects of the rule are
 * explicitly tracked. Modification operations which set a value to the current
 * underlying value do not override.
 */
public class RuleReference implements Rule {

    private Rule rule;
    private LanguageVersion minimumLanguageVersion;
    private LanguageVersion maximumLanguageVersion;
    private Boolean deprecated;
    private String name;
    private List<PropertyDescriptor<?>> propertyDescriptors;
    private Map<PropertyDescriptor<?>, Object> propertyValues;
    private String message;
    private String description;
    private List<String> examples;
    private String externalInfoUrl;
    private RulePriority priority;
    private RuleSetReference ruleSetReference;

    /**
     * Create a new reference to the given rule.
     *
     * @param theRule the referenced rule
     * @param theRuleSetReference the rule set, where the rule is defined
     */
    public RuleReference(Rule theRule, RuleSetReference theRuleSetReference) {
        rule = theRule;
        ruleSetReference = theRuleSetReference;
    }


    /** copy constructor */
    private RuleReference(RuleReference ref) {
        this.rule = ref.rule.deepCopy();
        this.minimumLanguageVersion = ref.minimumLanguageVersion;
        this.maximumLanguageVersion = ref.maximumLanguageVersion;
        this.deprecated = ref.deprecated;
        this.name = ref.name;
        this.propertyDescriptors = ref.propertyDescriptors;
        this.propertyValues = ref.propertyValues == null ? null : new HashMap<>(ref.propertyValues);
        this.message = ref.message;
        this.description = ref.description;
        this.examples = ref.examples == null ? null : new ArrayList<>(ref.examples);
        this.externalInfoUrl = ref.externalInfoUrl;
        this.priority = ref.priority;
        this.ruleSetReference = ref.ruleSetReference;
    }

    public Rule getRule() {
        return rule;
    }

    public LanguageVersion getOverriddenMinimumLanguageVersion() {
        return minimumLanguageVersion;
    }

    @Override
    public void setMinimumLanguageVersion(LanguageVersion minimumLanguageVersion) {
        // Only override if different than current value, or if already
        // overridden.
        if (!Objects.equals(minimumLanguageVersion, rule.getMinimumLanguageVersion()) || this.minimumLanguageVersion != null) {
            rule.setMinimumLanguageVersion(minimumLanguageVersion); // might throw
            this.minimumLanguageVersion = minimumLanguageVersion;
        }
    }

    public LanguageVersion getOverriddenMaximumLanguageVersion() {
        return maximumLanguageVersion;
    }

    @Override
    public void setMaximumLanguageVersion(LanguageVersion maximumLanguageVersion) {
        // Only override if different than current value, or if already
        // overridden.
        if (!Objects.equals(maximumLanguageVersion, rule.getMaximumLanguageVersion()) || this.maximumLanguageVersion != null) {
            rule.setMaximumLanguageVersion(maximumLanguageVersion); // might throw
            this.maximumLanguageVersion = maximumLanguageVersion;
        }
    }

    public Boolean isOverriddenDeprecated() {
        return deprecated;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated != null && deprecated;
    }

    @Override
    public void setDeprecated(boolean deprecated) {
        // Deprecation does not propagate to the underlying Rule. It is the
        // Rule reference itself which is being deprecated.
        this.deprecated = deprecated ? deprecated : null;
    }

    public String getOverriddenName() {
        return name;
    }

    public String getOriginalName() {
        return rule.getName();
    }

    @Override
    public void setName(String name) {
        // Only override if different than current value, or if already
        // overridden.
        if (!isSame(name, rule.getName()) || this.name != null) {
            this.name = name;
        }
    }

    @Override
    public String getName() {
        if (this.name != null) {
            return this.name;
        }
        return rule.getName();
    }

    public String getOverriddenMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        // Only override if different than current value, or if already
        // overridden.
        if (!isSame(message, rule.getMessage()) || this.message != null) {
            this.message = message;
            rule.setMessage(message);
        }
    }

    public String getOverriddenDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        // Only override if different than current value, or if already
        // overridden.
        if (!isSame(description, rule.getDescription()) || this.description != null) {
            this.description = description;
            rule.setDescription(description);
        }
    }

    public List<String> getOverriddenExamples() {
        return examples;
    }

    @Override
    public void addExample(String example) {
        // TODO Intuitively, if some examples are overridden (even with empty value), then
        // I think we should discard the previous ones. If the rule needs new examples,
        // then the previous ones are not relevant.

        // TODO Meaningful override of examples is hard, because they are merely
        // a list of strings. How does one indicate override of a particular
        // value? Via index? Rule.setExample(int, String)? But the XML format
        // does not provide a means of overriding by index, not unless you took
        // the position in the XML file to indicate corresponding index to
        // override. But that means you have to override starting from index 0.
        // This would be so much easier if examples had to have names, like
        // properties.

        // Only override if different than current values.
        if (!contains(rule.getExamples(), example)) {
            if (examples == null) {
                examples = new ArrayList<>(1);
            }
            // TODO Fix later. To keep example overrides from being unbounded,
            // we're only going to keep track of the last one.
            examples.clear();
            examples.add(example);
            rule.addExample(example);
        }
    }

    public String getOverriddenExternalInfoUrl() {
        return externalInfoUrl;
    }

    @Override
    public void setExternalInfoUrl(String externalInfoUrl) {
        // Only override if different than current value, or if already
        // overridden.
        if (!isSame(externalInfoUrl, rule.getExternalInfoUrl()) || this.externalInfoUrl != null) {
            this.externalInfoUrl = externalInfoUrl;
            rule.setExternalInfoUrl(externalInfoUrl);
        }
    }

    public RulePriority getOverriddenPriority() {
        return priority;
    }

    @Override
    public void setPriority(RulePriority priority) {
        // Only override if different than current value, or if already
        // overridden.
        if (priority != rule.getPriority() || this.priority != null) {
            this.priority = priority;
            rule.setPriority(priority);
        }
    }


    @Override
    public List<PropertyDescriptor<?>> getOverriddenPropertyDescriptors() {
        return propertyDescriptors == null ? Collections.<PropertyDescriptor<?>>emptyList()
                                           : new ArrayList<>(propertyDescriptors);
    }

    @Override
    public void definePropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) throws IllegalArgumentException {
        // Define on the underlying Rule, where it is impossible to have two
        // property descriptors with the same name. Therefore, there is no need
        // to check if the property is already overridden at this level.
        rule.definePropertyDescriptor(propertyDescriptor);
        if (propertyDescriptors == null) {
            propertyDescriptors = new ArrayList<>();
        }
        propertyDescriptors.add(propertyDescriptor);
    }


    @Override
    public Map<PropertyDescriptor<?>, Object> getOverriddenPropertiesByPropertyDescriptor() {
        return propertyValues == null ? new HashMap<>() : new HashMap<>(propertyValues);
    }

    @Override
    public <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value) {
        // Only override if different than current value.
        if (!Objects.equals(rule.getProperty(propertyDescriptor), value)) {
            if (propertyValues == null) {
                propertyValues = new HashMap<>();
            }
            propertyValues.put(propertyDescriptor, value);
            rule.setProperty(propertyDescriptor, value);
        }
    }


    public RuleSetReference getRuleSetReference() {
        return ruleSetReference;
    }


    private static boolean isSame(String s1, String s2) {
        return StringUtil.isSame(s1, s2, true, false, true);
    }


    private static boolean contains(Collection<String> collection, String s1) {
        for (String s2 : collection) {
            if (isSame(s1, s2)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasDescriptor(PropertyDescriptor<?> descriptor) {
        return propertyDescriptors != null && propertyDescriptors.contains(descriptor)
                || rule.hasDescriptor(descriptor);
    }

    @Override
    public boolean isPropertyOverridden(PropertyDescriptor<?> descriptor) {
        return propertyValues != null && propertyValues.containsKey(descriptor);
    }

    @Override
    public Rule deepCopy() {
        return new RuleReference(this);
    }

    /**
     * Checks whether this rule reference explicitly overrides any of the possible
     * attributes of the referenced rule.
     * @return <code>true</code> if there is at least one attribute overridden. <code>false</code> if
     *     the referenced rule is referenced without any change.
     */
    public boolean hasOverriddenAttributes() {
        return deprecated != null || description != null || examples != null || externalInfoUrl != null
                || maximumLanguageVersion != null || minimumLanguageVersion != null
                || message != null || name != null || priority != null
                || propertyDescriptors != null || propertyValues != null;
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
    public LanguageVersion getMaximumLanguageVersion() {
        return rule.getMaximumLanguageVersion();
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
    public String getDescription() {
        return rule.getDescription();
    }

    @Override
    public List<String> getExamples() {
        return rule.getExamples();
    }

    @Override
    public String getExternalInfoUrl() {
        return rule.getExternalInfoUrl();
    }

    @Override
    public RulePriority getPriority() {
        return rule.getPriority();
    }

    @Override
    public RuleTargetSelector getTargetSelector() {
        return rule.getTargetSelector();
    }

    @Override
    public void initialize(LanguageProcessor languageProcessor) {
        rule.initialize(languageProcessor);
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
    public Map<PropertyDescriptor<?>, Object> getPropertiesByPropertyDescriptor() {
        return rule.getPropertiesByPropertyDescriptor();
    }

    @Override
    public String dysfunctionReason() {
        return rule.dysfunctionReason();
    }
}
