/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.properties.AbstractPropertySource;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.InternalApiBridge;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Basic abstract implementation of all parser-independent methods of the Rule
 * interface.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public abstract class AbstractRule extends AbstractPropertySource implements Rule {

    private Language language;
    private LanguageVersion minimumLanguageVersion;
    private LanguageVersion maximumLanguageVersion;
    private boolean deprecated;
    private String name = getClass().getName();
    private String since;
    private String ruleClass = getClass().getName();
    private String ruleSetName;
    private String message;
    private String description;
    private List<String> examples = new ArrayList<>();
    private String externalInfoUrl;
    private RulePriority priority = RulePriority.LOW;
    private Set<String> ruleChainVisits = new LinkedHashSet<>();
    private Set<Class<? extends Node>> classRuleChainVisits = new LinkedHashSet<>();
    private RuleTargetSelector myStrategy;

    public AbstractRule() {
        definePropertyDescriptor(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR);
        definePropertyDescriptor(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR);
    }

    @Override
    protected String getPropertySourceType() {
        return "rule";
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(Language language) {
        if (this.language != null && !this.language.equals(language)) {
            throw new UnsupportedOperationException("The Language for Rule class " + this.getClass().getName()
                    + " is immutable and cannot be changed.");
        }
        this.language = language;
    }

    @Override
    public LanguageVersion getMinimumLanguageVersion() {
        return minimumLanguageVersion;
    }

    @Override
    public void setMinimumLanguageVersion(LanguageVersion minimumLanguageVersion) {
        if (minimumLanguageVersion != null && !minimumLanguageVersion.getLanguage().equals(getLanguage())) {
            throw new IllegalArgumentException("Version " + minimumLanguageVersion + " does not belong to language " + getLanguage());
        }
        this.minimumLanguageVersion = minimumLanguageVersion;
    }

    @Override
    public LanguageVersion getMaximumLanguageVersion() {
        return maximumLanguageVersion;
    }

    @Override
    public void setMaximumLanguageVersion(LanguageVersion maximumLanguageVersion) {
        if (maximumLanguageVersion != null && !maximumLanguageVersion.getLanguage().equals(getLanguage())) {
            throw new IllegalArgumentException("Version " + maximumLanguageVersion + " does not belong to language " + getLanguage());
        }
        this.maximumLanguageVersion = maximumLanguageVersion;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }

    @Override
    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSince() {
        return since;
    }

    @Override
    public void setSince(String since) {
        this.since = since;
    }

    @Override
    public String getRuleClass() {
        return ruleClass;
    }

    @Override
    public void setRuleClass(String ruleClass) {
        this.ruleClass = ruleClass;
    }

    @Override
    public String getRuleSetName() {
        return ruleSetName;
    }

    @Override
    public void setRuleSetName(String ruleSetName) {
        this.ruleSetName = ruleSetName;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<String> getExamples() {
        // TODO Needs to be externally immutable
        return examples;
    }

    @Override
    public void addExample(String example) {
        examples.add(example);
    }

    @Override
    public String getExternalInfoUrl() {
        return externalInfoUrl;
    }

    @Override
    public void setExternalInfoUrl(String externalInfoUrl) {
        this.externalInfoUrl = externalInfoUrl;
    }

    @Override
    public RulePriority getPriority() {
        return priority;
    }

    @Override
    public void setPriority(RulePriority priority) {
        this.priority = priority;
    }


    private Set<Class<? extends Node>> getClassRuleChainVisits() {
        if (classRuleChainVisits.isEmpty() && ruleChainVisits.isEmpty()) {
            return Collections.singleton(RootNode.class);
        }
        return classRuleChainVisits;
    }

    @Override
    public final RuleTargetSelector getTargetSelector() {
        if (myStrategy == null) {
            myStrategy = buildTargetSelector();
        }
        return myStrategy;
    }

    /**
     * Create the targeting strategy for this rule.
     * Use the factory methods of {@link RuleTargetSelector}.
     */
    @NonNull
    protected RuleTargetSelector buildTargetSelector() {
        Set<Class<? extends Node>> crvs = getClassRuleChainVisits();
        return crvs.isEmpty() ? RuleTargetSelector.forRootOnly()
                              : RuleTargetSelector.forTypes(crvs);
    }

    @Override
    public void start(RuleContext ctx) {
        // Override as needed
    }

    @Override
    public void end(RuleContext ctx) {
        // Override as needed
    }

    // TODO remove those methods, make Rules have type-safe access to a RuleContext

    /**
     * Cast the argument to a {@link RuleContext}. Use it to report violations:
     * <pre>{@code
     *  asCtx(data).addViolation(node);
     *  asCtx(data).addViolationWithMessage(node, "Some message");
     * }</pre>
     *
     * In longer term, rules will have type-safe access to a RuleContext, when the
     * rules use an appropriate visitor. Many rules have not been refactored yet.
     * Once this is done, this method will be deprecated as useless. Until then,
     * this is a way to hide the explicit cast to {@link RuleContext} in rules.
     */
    protected final RuleContext asCtx(Object ctx) {
        if (ctx instanceof RuleContext) {
            assert isThisRule(InternalApiBridge.getRule((RuleContext) ctx))
                : "not an appropriate rule context!";
            return (RuleContext) ctx;
        } else {
            throw new ClassCastException("Unexpected context object! " + ctx);
        }
    }

    private boolean isThisRule(Rule rule) {
        return rule == this // NOPMD CompareObjectsWithEquals
            || rule instanceof RuleReference && this.isThisRule(((RuleReference) rule).getRule());
    }

    /**
     * Rules are equal if:
     * <ol>
     * <li>They have the same implementation class.</li>
     * <li>They have the same name.</li>
     * <li>They have the same priority.</li>
     * <li>They share the same properties.</li>
     * </ol>
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true; // trivial
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractRule that = (AbstractRule) o;
        return Objects.equals(getName(), that.getName())
                && Objects.equals(getPriority(), that.getPriority())
                && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPriority(), super.hashCode());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Rule deepCopy() {
        Rule result;
        try {
            Constructor<? extends AbstractRule> declaredConstructor = getClass().getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            result = declaredConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
            // Can't happen... we already have an instance
            throw new RuntimeException(ignored); // in case it happens anyway, something is really wrong...
        }
        Rule rule = result;
        rule.setName(getName());
        rule.setLanguage(getLanguage());
        rule.setMinimumLanguageVersion(getMinimumLanguageVersion());
        rule.setMaximumLanguageVersion(getMaximumLanguageVersion());
        rule.setSince(getSince());
        rule.setMessage(getMessage());
        rule.setRuleSetName(getRuleSetName());
        rule.setExternalInfoUrl(getExternalInfoUrl());
        rule.setDescription(getDescription());
        for (final String example : getExamples()) {
            rule.addExample(example);
        }
        rule.setPriority(getPriority());
        for (final PropertyDescriptor<?> prop : getPropertyDescriptors()) {
            // define the descriptor only if it doesn't yet exist
            if (rule.getPropertyDescriptor(prop.name()) == null) {
                rule.definePropertyDescriptor(prop); // Property descriptors are immutable, and can be freely shared
            }

            if (isPropertyOverridden(prop)) {
                rule.setProperty((PropertyDescriptor<Object>) prop, getProperty((PropertyDescriptor<Object>) prop));
            }
        }
        return rule;
    }
}
