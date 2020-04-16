/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

/**
 * This is a tag interface to indicate that a Rule implementation class does not
 * support changes to it's Language. The Language is integral to the proper
 * functioning of the Rule.
 *
 * @deprecated No rule supports a change to their language. This will
 *     be made the default behaviour with PMD 7.0.0.
 */
@Deprecated
public interface ImmutableLanguage {
}
