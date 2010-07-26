package net.sourceforge.pmd.eclipse.ui.preferences.panelmanagers;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.stat.StatisticalRule;
/**
 *
 * @author Brian Remedios
 */
public class Configuration {

	// properties that should not be shown in the PerRuleProperty page
	public static final PropertyDescriptor<?>[] excludedRuleProperties = new PropertyDescriptor<?>[] {
		Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR,
		Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR,
		XPathRule.XPATH_DESCRIPTOR,
		XPathRule.VERSION_DESCRIPTOR,
		StatisticalRule.SIGMA_DESCRIPTOR,
		StatisticalRule.TOP_SCORE_DESCRIPTOR
		};

	public static Map<PropertyDescriptor<?>, Object> filteredPropertiesOf(Rule rule) {

		Map<PropertyDescriptor<?>, Object> valuesByProp = rule.getPropertiesByPropertyDescriptor();

		for (PropertyDescriptor<?> excludedRuleProperty : excludedRuleProperties) {
			valuesByProp.remove(excludedRuleProperty);
		}

		return valuesByProp;
	}
}
