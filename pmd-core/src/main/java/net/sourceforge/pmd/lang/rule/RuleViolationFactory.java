/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.document.FileLocation;

/**
 * Creates violations and controls suppression behavior for a language.
 *
 * TODO split this into violation decorators + violation suppressors.
 * There is no need to have language-specific violation classes.
 */
public interface RuleViolationFactory {
    // todo move to package reporting


    default RuleViolation createViolation(Rule rule, Node node, FileLocation location, String formattedMessage) {
        return new ParametricRuleViolation(rule, location, formattedMessage);
    }


    SuppressedViolation suppressOrNull(Node location, RuleViolation violation);


}
