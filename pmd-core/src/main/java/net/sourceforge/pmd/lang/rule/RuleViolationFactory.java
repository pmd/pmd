/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Creates violations and controls suppression behavior for a language.
 *
 * TODO split this into violation decorators + violation suppressors.
 * There is no need to have language-specific violation classes.
 */
public interface RuleViolationFactory {


    RuleViolation createViolation(Rule rule, @NonNull Node location, String filename, String formattedMessage);


    SuppressedViolation suppressOrNull(Node location, RuleViolation violation);


}
