/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule;

/**
 * This is a tag interface to indicate that a Rule implementation class does
 * not support changes to it's Language.  The Language is integral to the
 * proper functioning of the Rule.
 */
public interface ImmutableLanguage {
}
