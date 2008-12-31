/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.util.ResourceLoader;
import net.sourceforge.pmd.util.StringUtil;

/**
 * This class is used to parse a RuleSet reference value.  Most commonly used for specifying a
 * RuleSet to process, or in a Rule 'ref' attribute value in the RuleSet XML.  The RuleSet reference
 * can refer to either an external RuleSet or the current RuleSet when used as a Rule 'ref'
 * attribute value.  An individual Rule in the RuleSet can be indicated.
 * 
 * For an external RuleSet, referring to the entire RuleSet, the format is <i>ruleSetName</i>,
 * where the RuleSet name is either a resource file path to a RuleSet that ends with
 * <code>'.xml'</code>.</li>, or a simple RuleSet name.
 * 
 * A simple RuleSet name, is one which contains no path separators, and either contains a '-' or is
 * entirely numeric release number.  A simple name of the form <code>[language]-[name]</code> is
 * short for the full RuleSet name <code>rulesets/[language]/[name].xml</code>.  A numeric release
 * simple name of the form <code>[release]</code> is short for the full PMD Release RuleSet name
 * <code>rulesets/releases/[release].xml</code>.
 * 
 * For an external RuleSet, referring to a single Rule, the format is <i>ruleSetName/ruleName</i>,
 * where the RuleSet name is as described above.  A Rule with the <i>ruleName</i> should exist
 * in this external RuleSet.
 * 
 * For the current RuleSet, the format is <i>ruleName</i>, where the Rule name is not RuleSet name
 * (i.e. contains no path separators, '-' or '.xml' in it, and is not all numeric).  A Rule with the
 * <i>ruleName</i> should exist in the current RuleSet.
 * 
 * <table>
 *    <caption>Examples</caption>
 *    <thead>
 *       <tr>
 *    	    <th>String</th>
 *    	    <th>RuleSet file name</th>
 *    	    <th>Rule</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *    	    <td>rulesets/java/basic.xml</td>
 *    	    <td>rulesets/java/basic.xml</td>
 *    	    <td>all</td>
 *       </tr>
 *       <tr>
 *    	    <td>java-basic</td>
 *    	    <td>rulesets/java/basic.xml</td>
 *    	    <td>all</td>
 *       </tr>
 *       <tr>
 *    	    <td>50</td>
 *    	    <td>rulesets/releases/50.xml</td>
 *    	    <td>all</td>
 *       </tr>
 *       <tr>
 *    	    <td>rulesets/java/basic.xml/EmptyCatchBlock</td>
 *    	    <td>rulesets/java/basic.xml</td>
 *    	    <td>EmptyCatchBlock</td>
 *       </tr>
 *       <tr>
 *    	    <td>EmptyCatchBlock</td>
 *    	    <td>null</td>
 *    	    <td>EmptyCatchBlock</td>
 *       </tr>
 *    </tbody>
 * </table>
 */
public class RuleSetReferenceId {
    private final boolean external;
    private final String ruleSetFileName;
    private final boolean allRules;
    private final String ruleName;
    private final RuleSetReferenceId externalRuleSetReferenceId;

    /**
     * Construct a RuleSetReferenceId for the given single ID string.
     * @param id The id string.
     * @throws IllegalArgumentException If the ID contains a comma character.
     */
    public RuleSetReferenceId(final String id) {
	this(id, null);
    }

    /**
     * Construct a RuleSetReferenceId for the given single ID string.
     * If an external RuleSetReferenceId is given, the ID must refer to a non-external Rule.  The
     * external RuleSetReferenceId will be responsible for producing the InputStream containing
     * the Rule.
     * 
     * @param id The id string.
     * @param externalRuleSetReferenceId A RuleSetReferenceId to associate with this new instance.
     * @throws IllegalArgumentException If the ID contains a comma character.
     * @throws IllegalArgumentException If external RuleSetReferenceId is not external.
     * @throws IllegalArgumentException If the ID is not Rule reference when there is an external RuleSetReferenceId.
     */
    public RuleSetReferenceId(final String id, final RuleSetReferenceId externalRuleSetReferenceId) {
	if (externalRuleSetReferenceId != null && !externalRuleSetReferenceId.isExternal()) {
	    throw new IllegalArgumentException("Cannot pair with non-external <" + externalRuleSetReferenceId + ">.");
	}
	if (id != null && id.indexOf(',') >= 0) {
	    throw new IllegalArgumentException("A single RuleSetReferenceId cannot contain ',' (comma) characters: "
		    + id);
	}

	// Damn this parsing sucks, but my brain is just not working to let me write a simpler scheme.
	if (StringUtil.isEmpty(id) || isFullRuleSetName(id)) {
	    // A full RuleSet name
	    external = true;
	    ruleSetFileName = id;
	    allRules = true;
	    ruleName = null;
	} else {
	    // Find last path separator if it exists...
	    final int separatorIndex = Math.max(id.lastIndexOf('/'), id.lastIndexOf('\\'));
	    if (separatorIndex >= 0 && separatorIndex != id.length() - 1) {
		final String name = id.substring(0, separatorIndex);
		external = true;
		if (isFullRuleSetName(name)) {
		    // A full RuleSet name
		    ruleSetFileName = name;
		} else {
		    // Likely a simple RuleSet name
		    int index = name.indexOf('-');
		    if (index >= 0) {
			// Standard short name
			ruleSetFileName = "rulesets/" + name.substring(0, index) + "/" + name.substring(index + 1)
				+ ".xml";
		    } else {
			// A release RuleSet?
			if (name.matches("[0-9]+.*")) {
			    ruleSetFileName = "rulesets/releases/" + name + ".xml";
			} else {
			    // Appears to be a non-standard RuleSet name
			    ruleSetFileName = name;
			}
		    }
		}

		// Everything left should be a Rule name
		allRules = false;
		ruleName = id.substring(separatorIndex + 1);
	    } else {
		// Likely a simple RuleSet name
		int index = id.indexOf('-');
		if (index >= 0) {
		    // Standard short name
		    external = true;
		    ruleSetFileName = "rulesets/" + id.substring(0, index) + "/" + id.substring(index + 1) + ".xml";
		    allRules = true;
		    ruleName = null;
		} else {
		    // A release RuleSet?
		    if (id.matches("[0-9]+.*")) {
			external = true;
			ruleSetFileName = "rulesets/releases/" + id + ".xml";
			allRules = true;
			ruleName = null;
		    } else {
			// Must be a Rule name
			external = externalRuleSetReferenceId != null ? true : false;
			ruleSetFileName = externalRuleSetReferenceId != null ? externalRuleSetReferenceId
				.getRuleSetFileName() : null;
			allRules = false;
			ruleName = id;
		    }
		}
	    }
	}

	if (this.external && this.ruleName != null && !this.ruleName.equals(id) && externalRuleSetReferenceId != null) {
	    throw new IllegalArgumentException("Cannot pair external <" + this + "> with external <"
		    + externalRuleSetReferenceId + ">.");
	}
	this.externalRuleSetReferenceId = externalRuleSetReferenceId;
    }

    private static boolean isFullRuleSetName(String name) {
	return name.endsWith(".xml");
    }

    /**
     * Parse a String comma separated list of RuleSet reference IDs into a List of
     * RuleReferenceId instances.
     * @param referenceString A comma separated list of RuleSet reference IDs.
     * @return The corresponding List of RuleSetReferenceId instances.
     */
    public static List<RuleSetReferenceId> parse(String referenceString) {
	List<RuleSetReferenceId> references = new ArrayList<RuleSetReferenceId>();
	if (referenceString.indexOf(',') == -1) {
	    references.add(new RuleSetReferenceId(referenceString));
	} else {
	    for (String name : referenceString.split(",")) {
		references.add(new RuleSetReferenceId(name));
	    }
	}
	return references;
    }

    /**
     * Is this an external RuleSet reference?
     * @return <code>true</code> if this is an external reference, <code>false</code> otherwise.
     */
    public boolean isExternal() {
	return external;
    }

    /**
     * Is this a reference to all Rules in a RuleSet, or a single Rule? 
     * @return <code>true</code> if this is a reference to all Rules, <code>false</code> otherwise.
     */
    public boolean isAllRules() {
	return allRules;
    }

    /**
     * Get the RuleSet file name.
     * @return The RuleSet file name if this is an external reference, <code>null</code> otherwise.
     */
    public String getRuleSetFileName() {
	return ruleSetFileName;
    }

    /**
     * Get the Rule name.
     * @return The Rule name.
     * The Rule name.
     */
    public String getRuleName() {
	return ruleName;
    }

    /**
     * Try to load the RuleSet resource with the specified ClassLoader.  Multiple attempts to get
     * independent InputStream instances may be made, so subclasses must ensure they support this
     * behavior.  Delegates to an external RuleSetReferenceId if there is one associated with this
     * instance.
     *
     * @param classLoader The ClassLoader to use.
     * @return An InputStream to that resource.
     * @throws RuleSetNotFoundException if unable to find a resource.
     */
    public InputStream getInputStream(ClassLoader classLoader) throws RuleSetNotFoundException {
	if (externalRuleSetReferenceId == null) {
	    InputStream in = StringUtil.isEmpty(ruleSetFileName) ? null : ResourceLoader.loadResourceAsStream(
		    ruleSetFileName, classLoader);
	    if (in == null) {
		throw new RuleSetNotFoundException(
			"Can't find resource "
				+ ruleSetFileName
				+ ".  Make sure the resource is a valid file or URL or is on the CLASSPATH.  Here's the current classpath: "
				+ System.getProperty("java.class.path"));
	    }
	    return in;
	} else {
	    return externalRuleSetReferenceId.getInputStream(classLoader);
	}
    }

    /**
     * Return the String form of this Rule reference.
     * @return Return the String form of this Rule reference, which is <i>ruleSetFileName</i> for
     * all Rule external references, <i>ruleSetFileName/ruleName</i>, for a single Rule
     * external references, or <i>ruleName</i> otherwise.
     */
    public String toString() {
	if (ruleSetFileName != null) {
	    if (allRules) {
		return ruleSetFileName;
	    } else {
		return ruleSetFileName + "/" + ruleName;
	    }

	} else {
	    if (allRules) {
		return "anonymous all Rule";
	    } else {
		return ruleName;
	    }
	}
    }
}
