package net.sourceforge.pmd;

import java.util.logging.Logger;

/**
 * Provides a mapping from simple RuleSet names to RuleSet resource name.
 * The general form of a RuleSet resource is
 * <code>/rulesets/[language]/[name].xml</code>.  The simple name form of this
 * RuleSet is <code>[language]-[name]</code>.
 * <p>
 * The general form of a PMD release RuleSets is
 * <code>/rulesets/releases/[release].xml</code>.  The simple name form of this
 * RuleSet is <code>[release]</code>.
 */
public final class SimpleRuleSetNameMapper {

    private static final Logger LOG = Logger.getLogger(SimpleRuleSetNameMapper.class.getName());

    @SuppressWarnings("PMD.AvoidStringBufferField")
    private final StringBuilder rulesets = new StringBuilder();

    public SimpleRuleSetNameMapper(String ruleString) {
	if (ruleString.indexOf(',') == -1) {
	    check(ruleString);
	    return;
	}
	for (String name : ruleString.split(",")) {
	    check(name);
	}
    }

    public String getRuleSets() {
	return rulesets.toString();
    }

    protected void check(String name) {
	// Short names never contain the 'rulesets' in them.
	final String resourceName;
	if (name.indexOf("rulesets") == -1) {
	    // No path separators or periods, assume it as a short name
	    if (name.indexOf('/') < 0 && name.indexOf('\\') < 0 && name.indexOf('.') < 0) {
		// Contains a '-' character?
		int index = name.indexOf('-');
		if (index >= 0) {
		    // Standard short name
		    resourceName = "rulesets/" + name.substring(0, index) + "/" + name.substring(index + 1) + ".xml";
		} else {
		    // A release RuleSet?
		    if (name.matches("[0-9]+.*")) {
			resourceName = "rulesets/" + name + ".xml";
		    } else {
			// Appears to be a RuleSet resource name
			resourceName = name;
		    }
		}
	    } else {
		// Appears to be a RuleSet resource name
		resourceName = name;
	    }
	} else {
	    // Appears to be a RuleSet resource name
	    resourceName = name;
	}

	append(resourceName);
    }

    protected void append(String name) {
	if (rulesets.length() > 0) {
	    rulesets.append(',');
	}
	rulesets.append(name);
    }
}
