package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.plugin.UISettings;
import net.sourceforge.pmd.eclipse.ui.quickfix.PMDResolutionGenerator;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.rule.XPathRule;

/**
 * A value and label extractor interface for anything implementing the Rule interface
 * and may be real fields or values held as properties.
 *
 * Value returned are typed as comparable to facilitate sorting. Never return null,
 * return an empty string instead.
 *
 * @author Brian Remedios
 */
public interface RuleFieldAccessor {

	// NOTE: If you update these values then you also need to update
	// the tooltip that references them: 'preference.ruleset.column.rule_type.tooltip'

	String[] ruleTypeGeneric= new String[] { "-", "Generic" };
	String[] ruleTypeXPath	= new String[] { "X", "XPath" };
	String[] ruleTypeDFlow	= new String[] { "D", "Dataflow" };
	String[] ruleTypeTypeRes= new String[] { "T", "Type resolving" };

	/**
	 * @param rule Rule
	 * @return Comparable
	 */
	Comparable<?> valueFor(Rule rule);

	Comparable<?> valueFor(RuleCollection collection);
	
	Set<Comparable<?>> uniqueValuesFor(RuleCollection collection);
	
	String labelFor(Rule rule);
		
	RuleFieldAccessor since = new BasicRuleFieldAccessor() {
		public Comparable<String> valueFor(Rule rule) {
			return rule.getSince();
		}
	};

	RuleFieldAccessor priority = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			return rule.getPriority();
		}
		public String labelFor(Rule rule) {
			return UISettings.labelFor(rule.getPriority());
		}
		public Comparable<?> valueFor(RuleCollection collection) {
			return RuleUtil.commonPriority(collection);
		}
	};

	RuleFieldAccessor priorityName = new BasicRuleFieldAccessor() {
		public Comparable<String> valueFor(Rule rule) {
			return UISettings.labelFor(rule.getPriority());
		}
	};

	RuleFieldAccessor fixCount = new BasicRuleFieldAccessor() {
		public Comparable<Integer> valueFor(Rule rule) {
			return PMDResolutionGenerator.fixCountFor(rule);
		}
	};

	RuleFieldAccessor name = new BasicRuleFieldAccessor() {
		public Comparable<String> valueFor(Rule rule) {
			return rule.getName();
		}
	};

	RuleFieldAccessor description = new BasicRuleFieldAccessor() {
		public Comparable<String> valueFor(Rule rule) {
			return rule.getDescription();
		}
	};

	RuleFieldAccessor usesDFA = new BasicRuleFieldAccessor() {
		public Comparable<Boolean> valueFor(Rule rule) {
			return rule.usesDFA();
		}
		public Comparable<?> valueFor(RuleCollection collection) {
			return RuleUtil.allUseDfa(collection);
		}
	};

	RuleFieldAccessor message = new BasicRuleFieldAccessor() {
		public Comparable<String> valueFor(Rule rule) {
			return rule.getMessage();
		}
	};

	RuleFieldAccessor url = new BasicRuleFieldAccessor() {
		public Comparable<String> valueFor(Rule rule) {
			return rule.getExternalInfoUrl();
		}
	};

	RuleFieldAccessor exampleCount = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			int count = rule.getExamples().size();
			return count > 0 ? Integer.toString(count) : "";
		}
	};

	RuleFieldAccessor ruleType = new BasicRuleFieldAccessor() {
		public Comparable<String> valueFor(Rule rule) {
			StringBuilder sb = new StringBuilder(3);
			if (rule.hasDescriptor(XPathRule.XPATH_DESCRIPTOR)) sb.append(ruleTypeXPath[0]);
			if (rule.usesDFA()) sb.append(ruleTypeDFlow[0]);
			if (rule.usesTypeResolution()) sb.append(ruleTypeTypeRes[0]);
			if (sb.length() == 0) sb.append(ruleTypeGeneric[0]);
			return sb.toString();
		}
		public String labelFor(Rule rule) {
		    List<String> types = new ArrayList<String>(3);
            if (rule.hasDescriptor(XPathRule.XPATH_DESCRIPTOR)) types.add(ruleTypeXPath[1]);
	//	    if (if (RuleUtil.isXPathRule(rule))    TODO
            if (rule.usesDFA()) types.add(ruleTypeDFlow[1]);
            if (rule.usesTypeResolution()) types.add(ruleTypeTypeRes[1]);
            if (types.isEmpty()) types.add(ruleTypeGeneric[1]);
            return Util.asString(types, ", ");
		}
	};

	RuleFieldAccessor language = new BasicRuleFieldAccessor() {
        public Comparable<Language> valueFor(Rule rule) {
            return rule.getLanguage();
	    }
        
        public String labelFor(Rule rule) {
        	return rule.getLanguage().getName();
        }
	};

	RuleFieldAccessor minLanguageVersion = new BasicRuleFieldAccessor() {
        public Comparable<LanguageVersion> valueFor(Rule rule) {
            return rule.getMinimumLanguageVersion();
	    }
	};

   RuleFieldAccessor maxLanguageVersion = new BasicRuleFieldAccessor() {
        public Comparable<LanguageVersion> valueFor(Rule rule) {
            return rule.getMaximumLanguageVersion();
        }
    };

	RuleFieldAccessor violationRegex = new BasicRuleFieldAccessor() {
		public Comparable<String> valueFor(Rule rule) {
			return rule.getProperty(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR);
		}
	};

	RuleFieldAccessor violationXPath = new BasicRuleFieldAccessor() {
		public Comparable<String> valueFor(Rule rule) {
			return rule.getProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR);
		}
	};
	
	RuleFieldAccessor nonDefaultProperyCount = new BasicRuleFieldAccessor() {
		public Comparable<Integer> valueFor(Rule rule) {
			return RuleUtil.modifiedPropertiesIn(rule).size();
		}
	};
}
