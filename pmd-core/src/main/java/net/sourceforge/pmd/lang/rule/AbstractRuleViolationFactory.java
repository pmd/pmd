/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import net.sourceforge.pmd.lang.rule.impl.DefaultRuleViolationFactory;

/**
 * @deprecated This is kept for binary compatibility with the 6.x designer, yet will
 *     go away in 7.0. Use {@link DefaultRuleViolationFactory}
 */
@Deprecated
public class AbstractRuleViolationFactory extends DefaultRuleViolationFactory {
}
