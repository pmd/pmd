/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ruleset;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * Builds a rule, validating its parameters throughout. The builder can define property descriptors, but not override
 * them. For that, use RuleFactory.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
class RuleBuilder {

    private List<PropertyDescriptor<?>> definedProperties = new ArrayList<>();
    private String name;
    private String clazz;
    private Language language;
    private LanguageVersion minimumVersion;
    private LanguageVersion maximumVersion;
    private String since;
    private String message;
    private String rulesetName;
    private String externalInfoUrl;
    private String description;
    private List<String> examples = new ArrayList<>(1);
    private RulePriority priority;
    private boolean isDeprecated;
    private boolean isUsesDfa;
    private boolean isUsesMetrics;
    private boolean isUsesTyperesolution;


    RuleBuilder(String name, String clazz, String language) {
        this.name = name;
        language(language);
        className(clazz);
    }


    public void usesDFA(boolean usesDFA) {
        isUsesDfa = usesDFA;
    }


    public void usesMetrics(boolean usesMetrics) {
        isUsesMetrics = usesMetrics;
    }


    public void usesTyperesolution(boolean usesTyperesolution) {
        isUsesTyperesolution = usesTyperesolution;
    }


    private RuleBuilder language(String languageName) {
        if (StringUtils.isBlank(languageName)) {
            throw new IllegalArgumentException("Blank language attribute");
        }

        Language lang = LanguageRegistry.findLanguageByTerseName(languageName);
        if (lang == null) {
            throw new IllegalArgumentException(
                "Unknown Language '" + languageName + "' for rule" + name + ", supported Languages are "
                    + LanguageRegistry.commaSeparatedTerseNamesForLanguage(LanguageRegistry.findWithRuleSupport()));
        }
        language = lang;
        return this;
    }


    private RuleBuilder className(String className) {
        if (StringUtils.isBlank(className)) {
            throw new IllegalArgumentException("The 'class' field of rule can't be null, nor empty.");
        }

        this.clazz = className;
        return this;
    }


    public RuleBuilder minimumLanguageVersion(String minimum) {
        LanguageVersion minimumLanguageVersion = language.getVersion(minimum);
        if (minimumLanguageVersion == null) {
            throw new IllegalArgumentException("Unknown minimum Language Version '" + minimum
                                                   + "' for Language '" + language.getTerseName() + "' for rule"
                                                   + name
                                                   + "; supported Language Versions are: "
                                                   + LanguageRegistry.commaSeparatedTerseNamesForLanguageVersion(language.getVersions()));
        }

        minimumVersion = minimumLanguageVersion;
        checkLanguageVersionsAreOrdered();
        return this;
    }


    public RuleBuilder maximumLanguageVersion(String maximum) {
        LanguageVersion maximumLanguageVersion = language.getVersion(maximum);
        if (maximumLanguageVersion == null) {
            throw new IllegalArgumentException("Unknown maximum Language Version '" + maximum
                                                   + "' for Language '" + language.getTerseName()
                                                   + "' for Rule " + name
                                                   + "; supported Language Versions are: "
                                                   + LanguageRegistry.commaSeparatedTerseNamesForLanguageVersion(language.getVersions()));
        }
        maximumVersion = maximumLanguageVersion;
        checkLanguageVersionsAreOrdered();
        return this;
    }


    private void checkLanguageVersionsAreOrdered() {
        if (minimumVersion != null && maximumVersion != null
            && minimumVersion.compareTo(maximumVersion) > 0) {
            throw new IllegalArgumentException(
                "The minimum Language Version '" + minimumVersion.getTerseName()
                    + "' must be prior to the maximum Language Version '"
                    + maximumVersion.getTerseName() + "' for Rule '" + name
                    + "'; perhaps swap them around?");
        }
    }


    public RuleBuilder since(String sinceStr) {
        if (StringUtils.isNotBlank(sinceStr)) {
            since = sinceStr;
        }
        return this;
    }


    public void externalInfoUrl(String externalInfoUrl) {
        this.externalInfoUrl = externalInfoUrl;
    }


    public void rulesetName(String rulesetName) {
        this.rulesetName = rulesetName;
    }


    public void message(String message) {
        this.message = message;
    }


    public void defineProperty(PropertyDescriptor<?> descriptor) {
        definedProperties.add(descriptor);
    }


    public void setDeprecated(boolean deprecated) {
        isDeprecated = deprecated;
    }


    public void description(String description) {
        this.description = description;
    }


    public void addExample(String example) {
        examples.add(example);
    }


    public void priority(int priority) {
        this.priority = RulePriority.valueOf(priority);
    }


    public Rule build() throws ClassNotFoundException, IllegalAccessException, InstantiationException {


        Rule rule = (Rule) RuleBuilder.class.getClassLoader().loadClass(clazz).newInstance();

        rule.setName(name);
        rule.setRuleClass(clazz);
        rule.setLanguage(language);
        rule.setMinimumLanguageVersion(minimumVersion);
        rule.setMaximumLanguageVersion(maximumVersion);
        rule.setSince(since);
        rule.setMessage(message);
        rule.setExternalInfoUrl(externalInfoUrl);
        rule.setDeprecated(isDeprecated);
        rule.setDescription(description);
        rule.setPriority(priority);

        for (String example : examples) {
            rule.addExample(example);
        }


        if (isUsesDfa) {
            rule.setUsesDFA();
        }

        if (isUsesMetrics) {
            rule.setUsesMetrics();
        }

        if (isUsesTyperesolution) {
            rule.setUsesTypeResolution();
        }

        for (PropertyDescriptor<?> descriptor : definedProperties) {
            rule.definePropertyDescriptor(descriptor);
        }


        return rule;
    }


}
