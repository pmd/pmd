/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.internal.PropertyParsingUtil.DEPRECATED_RULE_PROPERTY_MARKER;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.test.PmdRuleTst;

class VariableCanBeInlinedTest extends PmdRuleTst {

    @Test
    void statementOrderMattersPropertyIsMarkedDeprecated() {
        PropertyDescriptor<?> descriptor =
                new VariableCanBeInlinedRule().getPropertyDescriptor("statementOrderMatters");

        assertNotNull(descriptor);
        assertTrue(descriptor.description().startsWith(DEPRECATED_RULE_PROPERTY_MARKER));
    }
}
