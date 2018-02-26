/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Stores the auto fixer class and the node for later application. The context allows to apply the fixes to the node.
 */
public class RuleViolationFixContext {

    private static final Logger LOG = Logger.getLogger(RuleViolationFixContext.class.getName());

    private final Class<? extends RuleViolationFix> ruleViolationFixClass;
    private final Node nodeToFix;

    /* package-private */ RuleViolationFixContext(final Class<? extends RuleViolationFix> ruleViolationFixClass,
                                                  final Node nodeToFix) {
        this.ruleViolationFixClass = requireNonNull(ruleViolationFixClass);
        this.nodeToFix = requireNonNull(nodeToFix);
    }

    /**
     * Attempts to apply the auto fixes to the corresponding node. If an exception is thrown, the fixes are ignored
     * and logged.
     */
    public void applyFixToNode() {
        try {
            instanceRuleViolationFixAndApply();
        } catch (final NoSuchMethodException e) {
            LOG.severe("Parameterless public constructor for rule violation fix class: " + ruleViolationFixClass
                    + " could not be found. Ignoring fixes for this class.");
        } catch (final IllegalAccessException e) {
            LOG.severe("Parameterless public constructor for rule violation fix class: " + ruleViolationFixClass
                    + " is inaccessible due to current Java language access control. Ignoring fixes for this class.");
        } catch (final InstantiationException e) {
            LOG.severe("Parameterless public constructor for rule violation fix class: " + ruleViolationFixClass
                    + " corresponds to an abstract class. Ignoring fixes for this class.");
        } catch (final InvocationTargetException e) {
            LOG.severe("Parameterless public constructor for rule violation fix class: " + ruleViolationFixClass
                    + " threw an exception when called. Ignoring fixes for this class.");
        }
    }

    /**
     * Attempts to instance a rule violation fix from the corresponding class using reflection. It is required that
     * the class implementing the {@link RuleViolationFix} has a public parameterless constructor.
     *
     * @throws NoSuchMethodException     if a matching method is not found.
     * @throws IllegalAccessException    if this {@code Constructor} object
     *                                   is enforcing Java language access control and the underlying
     *                                   constructor is inaccessible.
     * @throws InstantiationException    if the class that declares the
     *                                   underlying constructor represents an abstract class.
     * @throws InvocationTargetException if the underlying constructor
     *                                   throws an exception.
     */
    private void instanceRuleViolationFixAndApply()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Constructor<? extends RuleViolationFix> parameterlessConstructor = ruleViolationFixClass.getConstructor();

        final RuleViolationFix ruleViolationFix = parameterlessConstructor.newInstance();
        ruleViolationFix.applyToNode(nodeToFix);
    }
}
