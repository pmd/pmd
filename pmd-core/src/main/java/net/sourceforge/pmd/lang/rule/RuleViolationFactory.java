/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.FileLocation;

/**
 * Creates violations and controls suppression behavior for a language.
 *
 * TODO split this into violation decorators + violation suppressors.
 * There is no need to have language-specific violation classes.
 *
 * <p>Since PMD 6.43.0, {@link RuleContext} has been enriched with methods that should
 * be strongly preferred to using this interface directly. The interface will change a
 * lot in PMD 7.
 */
public interface RuleViolationFactory {
    // todo move to package reporting


    default RuleViolation createViolation(Rule rule, Node node, FileLocation location, String formattedMessage) {
        return new ParametricRuleViolation(rule, location, formattedMessage);
    }


    SuppressedViolation suppressOrNull(Node location, RuleViolation violation);


}
