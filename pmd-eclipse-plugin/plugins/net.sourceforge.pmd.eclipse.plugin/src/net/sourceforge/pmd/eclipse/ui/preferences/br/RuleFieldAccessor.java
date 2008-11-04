package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.util.Util;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.rule.XPathRule;

/**
 * A value and label extractor interface for anything implementing the Rule interface
 * and may be real fields or values held as properties.
 * 
 * Value returned are typed as comparable to facilitate sorting. Never return null, 
 * return an empty string instead.
 * 
 * TODO - move this to PMD proper.
 * 
 * @author Brian Remedios
 */
public interface RuleFieldAccessor {
	
	/**
	 * @param rule Rule
	 * @return Comparable
	 */
	Comparable<?> valueFor(Rule rule);
	String labelFor(Rule rule);
	
	RuleFieldAccessor ruleSetName = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			return PMDPreferencePage.ruleSetNameFrom(rule);
		}
	};
	
	RuleFieldAccessor since = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			return rule.getSince();
		}
	};
	
	RuleFieldAccessor priority = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			return Integer.valueOf(rule.getPriority().getPriority());
		}
	};
	
	RuleFieldAccessor priorityName = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			return rule.getPriority().getName();
		}
	};
	
	RuleFieldAccessor name = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			return rule.getName();
		}
	};
	
	RuleFieldAccessor description = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			return rule.getDescription();
		}
	};
	
	RuleFieldAccessor usesDFA = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			return rule.usesDFA() ? Boolean.TRUE : Boolean.FALSE;
		}
	};
	
	RuleFieldAccessor message = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			return rule.getMessage();
		}
	};
	
	RuleFieldAccessor url = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			return rule.getExternalInfoUrl();
		}
	};
	
	RuleFieldAccessor properties = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			return PMDPreferencePage.propertyStringFrom(rule);
		}
	};
	
	RuleFieldAccessor exampleCount = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {			
			int count = rule.getExamples().size();
			return count > 0 ? Integer.toString(count) : "";
		}
	};
	
	RuleFieldAccessor ruleType = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			StringBuilder sb = new StringBuilder(3);
			if (rule.hasDescriptor(XPathRule.XPATH_DESCRIPTOR)) sb.append('X');
			if (rule.usesDFA()) sb.append('D');
			if (rule.usesTypeResolution()) sb.append('T');
			return sb.toString();
		}
		public String labelFor(Rule rule) {
		    List<String> types = new ArrayList<String>(3);
            if (rule.hasDescriptor(XPathRule.XPATH_DESCRIPTOR)) types.add("XPath");
            if (rule.usesDFA()) types.add("Dataflow");
            if (rule.usesTypeResolution()) types.add("Type resolving");
            return Util.asString(types, ", ");
		}
	};
		
	RuleFieldAccessor minLanguageVersion = new BasicRuleFieldAccessor() {
        public Comparable<?> valueFor(Rule rule) {	            
            LanguageVersion version = rule.getMinimumLanguageVersion();
            return version == null ? "" : version.getTerseName();
	    }
	};
	
   RuleFieldAccessor maxLanguageVersion = new BasicRuleFieldAccessor() {
        public Comparable<?> valueFor(Rule rule) {             
            return rule.getMaximumLanguageVersion();
        }
    };
	
	RuleFieldAccessor violationRegex = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			return rule.getProperty(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR);
		}
	};
	
	RuleFieldAccessor violationXPath = new BasicRuleFieldAccessor() {
		public Comparable<?> valueFor(Rule rule) {
			return rule.getProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR);
		}
	};
}
