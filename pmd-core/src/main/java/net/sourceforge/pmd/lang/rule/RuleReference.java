/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSetReference;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.StringUtil;

/**
 * This class represents a Rule which is a reference to Rule defined in another
 * RuleSet. All details of the Rule are delegated to the underlying referenced
 * Rule, but those operations which modify overridden aspects of the rule are
 * explicitly tracked. Modification operations which set a value to the current
 * underlying value do not override.
 */
public class RuleReference extends AbstractDelegateRule {

    private Language language;
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

    private static final List<PropertyDescriptor<?>> EMPTY_DESCRIPTORS = new ArrayList<>(0);

    public RuleReference() {
    }

    public RuleReference(Rule theRule, RuleSetReference theRuleSetReference) {
        setRule(theRule);
        ruleSetReference = theRuleSetReference;
    }

    public Language getOverriddenLanguage() {
        return language;
    }

    @Override
    public void setLanguage(Language language) {
        // Only override if different than current value, or if already
        // overridden.
        if (!isSame(language, super.getLanguage()) || this.language != null) {
            this.language = language;
            super.setLanguage(language);
        }
    }

    public LanguageVersion getOverriddenMinimumLanguageVersion() {
        return minimumLanguageVersion;
    }

    @Override
    public void setMinimumLanguageVersion(LanguageVersion minimumLanguageVersion) {
        // Only override if different than current value, or if already
        // overridden.
        if (!isSame(minimumLanguageVersion, super.getMinimumLanguageVersion()) || this.minimumLanguageVersion != null) {
            this.minimumLanguageVersion = minimumLanguageVersion;
            super.setMinimumLanguageVersion(minimumLanguageVersion);
        }
    }

    public LanguageVersion getOverriddenMaximumLanguageVersion() {
        return maximumLanguageVersion;
    }

    @Override
    public void setMaximumLanguageVersion(LanguageVersion maximumLanguageVersion) {
        // Only override if different than current value, or if already
        // overridden.
        if (!isSame(maximumLanguageVersion, super.getMaximumLanguageVersion()) || this.maximumLanguageVersion != null) {
            this.maximumLanguageVersion = maximumLanguageVersion;
            super.setMaximumLanguageVersion(maximumLanguageVersion);
        }
    }

    public Boolean isOverriddenDeprecated() {
        return deprecated;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated != null && deprecated.booleanValue();
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

    public List<PropertyDescriptor<?>> getOverriddenPropertyDescriptors() {

        return propertyDescriptors == null ? EMPTY_DESCRIPTORS : propertyDescriptors;
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

    public Map<PropertyDescriptor<?>, Object> getOverriddenPropertiesByPropertyDescriptor() {
        return propertyValues;
    }

    @Override
    public <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value) {
        // Only override if different than current value.
        if (!isSame(super.getProperty(propertyDescriptor), value)) {
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

    public void setRuleSetReference(RuleSetReference ruleSetReference) {
        this.ruleSetReference = ruleSetReference;
    }

    private static boolean isSame(String s1, String s2) {
        return StringUtil.isSame(s1, s2, true, false, true);
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    private static boolean isSame(Object o1, Object o2) {
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return isSame((Object[]) o1, (Object[]) o2);
        }
        return o1 == o2 || o1 != null && o2 != null && o1.equals(o2);
    }

    @SuppressWarnings("PMD.UnusedNullCheckInEquals")
    // TODO: fix UnusedNullCheckInEquals rule for Arrays
    private static boolean isSame(Object[] a1, Object[] a2) {
        return a1 == a2 || a1 != null && a2 != null && Arrays.equals(a1, a2);
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

    public boolean hasOverriddenProperty(PropertyDescriptor<?> descriptor) {
        return propertyValues != null && propertyValues.containsKey(descriptor);
    }

    @Override
    public boolean usesDefaultValues() {

        List<PropertyDescriptor<?>> descriptors = getOverriddenPropertyDescriptors();
        if (!descriptors.isEmpty()) {
            return false;
        }

        for (PropertyDescriptor<?> desc : descriptors) {
            if (!isSame(desc.defaultValue(), getProperty(desc))) {
                return false;
            }
        }

        if (!getRule().usesDefaultValues()) {
            return false;
        }

        return true;
    }

    @Override
    public void useDefaultValueFor(PropertyDescriptor<?> desc) {

        // not sure if we should go all the way through to the real thing?
        getRule().useDefaultValueFor(desc);

        if (propertyValues == null) {
            return;
        }

        propertyValues.remove(desc);

        if (propertyDescriptors != null) {
            propertyDescriptors.remove(desc);
        }
    }
}
