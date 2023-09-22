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

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSetReference;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.StringUtil;

/**
 * This class represents a Rule which is a reference to Rule defined in another
 * RuleSet. All details of the Rule are delegated to the underlying referenced
 * Rule, but those operations which modify overridden aspects of the rule are
 * explicitly tracked. Modification operations which set a value to the current
 * underlying value do not override.
 */
public class RuleReference extends AbstractDelegateRule {

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
        setRule(theRule);
        ruleSetReference = theRuleSetReference;
    }


    /** copy constructor */
    private RuleReference(RuleReference ref) {

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

        this.setRule(ref.getRule().deepCopy());
    }

    public LanguageVersion getOverriddenMinimumLanguageVersion() {
        return minimumLanguageVersion;
    }

    @Override
    public void setMinimumLanguageVersion(LanguageVersion minimumLanguageVersion) {
        // Only override if different than current value, or if already
        // overridden.
        if (!Objects.equals(minimumLanguageVersion, super.getMinimumLanguageVersion()) || this.minimumLanguageVersion != null) {
            super.setMinimumLanguageVersion(minimumLanguageVersion); // might throw
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
        if (!Objects.equals(maximumLanguageVersion, super.getMaximumLanguageVersion()) || this.maximumLanguageVersion != null) {
            super.setMaximumLanguageVersion(maximumLanguageVersion); // might throw
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
        return super.getName();
    }

    @Override
    public void setName(String name) {
        // Only override if different than current value, or if already
        // overridden.
        if (!isSame(name, super.getName()) || this.name != null) {
            this.name = name;
        }
    }

    @Override
    public String getName() {
        if (this.name != null) {
            return this.name;
        }
        return super.getName();
    }

    public String getOverriddenMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        // Only override if different than current value, or if already
        // overridden.
        if (!isSame(message, super.getMessage()) || this.message != null) {
            this.message = message;
            super.setMessage(message);
        }
    }

    public String getOverriddenDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        // Only override if different than current value, or if already
        // overridden.
        if (!isSame(description, super.getDescription()) || this.description != null) {
            this.description = description;
            super.setDescription(description);
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
        if (!contains(super.getExamples(), example)) {
            if (examples == null) {
                examples = new ArrayList<>(1);
            }
            // TODO Fix later. To keep example overrides from being unbounded,
            // we're only going to keep track of the last one.
            examples.clear();
            examples.add(example);
            super.addExample(example);
        }
    }

    public String getOverriddenExternalInfoUrl() {
        return externalInfoUrl;
    }

    @Override
    public void setExternalInfoUrl(String externalInfoUrl) {
        // Only override if different than current value, or if already
        // overridden.
        if (!isSame(externalInfoUrl, super.getExternalInfoUrl()) || this.externalInfoUrl != null) {
            this.externalInfoUrl = externalInfoUrl;
            super.setExternalInfoUrl(externalInfoUrl);
        }
    }

    public RulePriority getOverriddenPriority() {
        return priority;
    }

    @Override
    public void setPriority(RulePriority priority) {
        // Only override if different than current value, or if already
        // overridden.
        if (priority != super.getPriority() || this.priority != null) {
            this.priority = priority;
            super.setPriority(priority);
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
        super.definePropertyDescriptor(propertyDescriptor);
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
        if (!Objects.equals(super.getProperty(propertyDescriptor), value)) {
            if (propertyValues == null) {
                propertyValues = new HashMap<>();
            }
            propertyValues.put(propertyDescriptor, value);
            super.setProperty(propertyDescriptor, value);
        }
    }


    public RuleSetReference getRuleSetReference() {
        return ruleSetReference;
    }


    /**
     * @deprecated There's no use in setting the ruleset reference after construction
     */
    @Deprecated
    public void setRuleSetReference(RuleSetReference ruleSetReference) {
        this.ruleSetReference = ruleSetReference;
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
                || super.hasDescriptor(descriptor);
    }

    /**
     * @deprecated Use {@link #isPropertyOverridden(PropertyDescriptor)} instead
     */
    @Deprecated
    public boolean hasOverriddenProperty(PropertyDescriptor<?> descriptor) {
        return isPropertyOverridden(descriptor);
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
}
