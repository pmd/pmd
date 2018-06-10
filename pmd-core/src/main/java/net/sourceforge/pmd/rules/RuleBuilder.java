/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.rules;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSetReference;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * Builds a rule, validating its parameters throughout. The builder can define property descriptors, but not override
 * them. For that, use {@link RuleFactory#decorateRule(Rule, RuleSetReference, Element)}.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class RuleBuilder {

    private List<PropertyDescriptor<?>> definedProperties = new ArrayList<>();
    private String name;
    private String clazz;
    private Language language;
    private String minimumVersion;
    private String maximumVersion;
    private String since;
    private String message;
    private String externalInfoUrl;
    private String description;
    private List<String> examples = new ArrayList<>(1);
    private RulePriority priority;
    private boolean isDeprecated;
    private boolean isUsesDfa;
    private boolean isUsesMultifile;
    private boolean isUsesTyperesolution;

    public RuleBuilder(String name, String clazz, String language) {
        this.name = name;
        language(language);
        className(clazz);
    }

    public void usesDFA(boolean usesDFA) {
        isUsesDfa = usesDFA;
    }

    public void usesMultifile(boolean usesMultifile) {
        isUsesMultifile = usesMultifile;
    }

    public void usesTyperesolution(boolean usesTyperesolution) {
        isUsesTyperesolution = usesTyperesolution;
    }

    private void language(String languageName) {
        if (StringUtils.isBlank(languageName)) {
            // Some languages don't need the attribute because the rule's
            // constructor calls setLanguage, see e.g. AbstractJavaRule
            return;
        }

        Language lang = LanguageRegistry.findLanguageByTerseName(languageName);
        if (lang == null) {
            throw new IllegalArgumentException(
                    "Unknown Language '" + languageName + "' for rule" + name + ", supported Languages are "
                    + LanguageRegistry.commaSeparatedTerseNamesForLanguage(LanguageRegistry.findWithRuleSupport()));
        }
        language = lang;
    }

    private void className(String className) {
        if (StringUtils.isBlank(className)) {
            throw new IllegalArgumentException("The 'class' field of rule can't be null, nor empty.");
        }

        this.clazz = className;
    }

    public void minimumLanguageVersion(String minimum) {
        minimumVersion = minimum;
    }

    public void maximumLanguageVersion(String maximum) {
        maximumVersion = maximum;
    }

    private void checkLanguageVersionsAreOrdered(Rule rule) {
        if (rule.getMinimumLanguageVersion() != null && rule.getMaximumLanguageVersion() != null
            && rule.getMinimumLanguageVersion().compareTo(rule.getMaximumLanguageVersion()) > 0) {
            throw new IllegalArgumentException(
                    "The minimum Language Version '" + rule.getMinimumLanguageVersion().getTerseName()
                    + "' must be prior to the maximum Language Version '"
                    + rule.getMaximumLanguageVersion().getTerseName() + "' for Rule '" + name
                    + "'; perhaps swap them around?");
        }
    }

    public void since(String sinceStr) {
        if (StringUtils.isNotBlank(sinceStr)) {
            since = sinceStr;
        }
    }

    public void externalInfoUrl(String externalInfoUrl) {
        this.externalInfoUrl = externalInfoUrl;
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


    public void priority(int priorityString) {
        this.priority = RulePriority.valueOf(priorityString);
    }

    // Must be loaded after rule construction to know the Language
    private void loadLanguageMinMaxVersions(Rule rule) {

        if (minimumVersion != null) {
            LanguageVersion minimumLanguageVersion = rule.getLanguage().getVersion(minimumVersion);
            if (minimumLanguageVersion == null) {
                throwUnknownLanguageVersionException("minimum", minimumVersion);
            } else {
                rule.setMinimumLanguageVersion(minimumLanguageVersion);
            }
        }

        if (maximumVersion != null) {
            LanguageVersion maximumLanguageVersion = rule.getLanguage().getVersion(maximumVersion);
            if (maximumLanguageVersion == null) {
                throwUnknownLanguageVersionException("maximum", maximumVersion);
            } else {
                rule.setMaximumLanguageVersion(maximumLanguageVersion);
            }
        }

        checkLanguageVersionsAreOrdered(rule);
    }

    private void throwUnknownLanguageVersionException(String minOrMax, String unknownVersion) {
        throw new IllegalArgumentException("Unknown " + minOrMax + " Language Version '" + unknownVersion
                                           + "' for Language '" + language.getTerseName()
                                           + "' for Rule " + name
                                           + "; supported Language Versions are: "
                                           + LanguageRegistry.commaSeparatedTerseNamesForLanguageVersion(language.getVersions()));
    }

    public Rule build() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Rule rule = (Rule) RuleBuilder.class.getClassLoader().loadClass(clazz).newInstance();

        rule.setName(name);
        rule.setRuleClass(clazz);

        if (rule.getLanguage() == null) {
            rule.setLanguage(language);
        }

        loadLanguageMinMaxVersions(rule);
        rule.setSince(since);
        rule.setMessage(message);
        rule.setExternalInfoUrl(externalInfoUrl);
        rule.setDeprecated(isDeprecated);
        rule.setDescription(description);
        rule.setPriority(priority == null ? RulePriority.LOW : priority);

        for (String example : examples) {
            rule.addExample(example);
        }

        if (isUsesDfa) {
            rule.setDfa(isUsesDfa);
        }
        if (isUsesMultifile) {
            rule.setMultifile(isUsesMultifile);
        }
        if (isUsesTyperesolution) {
            rule.setTypeResolution(isUsesTyperesolution);
        }

        for (PropertyDescriptor<?> descriptor : definedProperties) {
            if (!rule.getPropertyDescriptors().contains(descriptor)) {
                rule.definePropertyDescriptor(descriptor);
            }
        }

        return rule;
    }
}
