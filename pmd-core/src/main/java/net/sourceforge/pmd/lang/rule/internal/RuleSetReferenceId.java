/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.rule.RuleSetLoader;
import net.sourceforge.pmd.util.internal.ResourceLoader;

/**
 * This class is used to reference either a complete ruleset with all rules or to reference a single rule
 * within a rule set.
 *
 * <p>A {@link RuleSetReferenceId} is said to be "absolute" if it knows about the ruleset it references.
 * It is "relative", if only the name of the referenced rule is known. In that case, the {@link RuleSetReferenceId}
 * needs to be paired with an absolute reference.
 *
 * <p>This class can parse reference values from string. Most commonly used for
 * specifying a RuleSet to process, or in a Rule 'ref' attribute value in the
 * RuleSet XML. The RuleSet reference can refer to either a specific RuleSet file or
 * the current RuleSet when used as a Rule 'ref' attribute value. An individual
 * Rule in the RuleSet can be selected.
 *
 * <p>For referring an entire RuleSet, the format is
 * <i>ruleSetName</i>, where the ruleSetName is a resource file path, a classpath or a URL
 * that ends with <code>'.xml'</code>.
 *
 * <p>Referring to a single Rule, the format is
 * <i>ruleSetName/ruleName</i>, where the ruleSetName is as described above. A
 * Rule with the <i>ruleName</i> should exist in the referenced RuleSet.
 *
 * <p>For the current RuleSet, the format is <i>ruleName</i>, where the Rule name
 * is not RuleSet name (i.e. contains no path separators or '.xml' in it).
 * A Rule with the <i>ruleName</i> should exist in the current RuleSet.
 *
 * <table>
 * <caption>Examples</caption>
 * <thead>
 * <tr>
 *     <th>String</th>
 *     <th>RuleSet file name</th>
 *     <th>Rule</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 *     <td>rulesets/java/basic.xml</td>
 *     <td>rulesets/java/basic.xml</td>
 *     <td>null (all rules)</td>
 * </tr>
 * <tr>
 *     <td>rulesets/java/basic.xml/EmptyCatchBlock</td>
 *     <td>rulesets/java/basic.xml</td>
 *     <td>EmptyCatchBlock</td>
 * </tr>
 * <tr>
 *     <td>EmptyCatchBlock</td>
 *     <td>null (current rule set)</td>
 *     <td>EmptyCatchBlock</td>
 * </tr>
 * <tr>
 *     <td>https://raw.githubusercontent.com/pmd/pmd/master/<wbr />pmd-java/src/main/resources/<wbr />rulesets/java/quickstart.xml/ConstantsInInterface</td>
 *     <td>https://raw.githubusercontent.com/pmd/pmd/master/<wbr />pmd-java/src/main/resources/<wbr />rulesets/java/quickstart.xml</td>
 *     <td>ConstantsInInterface</td>
 * </tr>
 * <tr>
 *     <td>https://example.org/ruleset/MyRule</td>
 *     <td>https://example.org/ruleset/MyRule</td>
 *     <td>null (all rules, see note below)</td>
 * </tr>
 * <tr>
 *     <td>https://example.org/ruleset.xml/MyRule</td>
 *     <td>https://example.org/ruleset.xml</td>
 *     <td>MyRule (see note below)</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * <p>Note: When specifying a URL, the URL won't be checked until the ruleset is actually loaded. This might result
 * in ambiguity in the following cases: if "https://example.org/ruleset/MyRule" will be interpreted as reference
 * to a rulesets and all rules are referenced. To avoid this ambiguity, rulesets should always use the extension ".xml",
 * e.g. "https://example.org/ruleset.xml/MyRule".
 *
 * <p>Note: This is part of the internals of the {@link RuleSetLoader}.
 */
public class RuleSetReferenceId {

    // might be a file path, classpath or URI. Can be null.
    private final @Nullable String ruleSetReference;
    // referenced rule within the ruleSet. Can be null.
    private final @Nullable String ruleName;

    private final @NonNull String originalRef;


    // Helper constructor, that does not parse the ruleSetReference string
    private RuleSetReferenceId(final String ruleSetReference, final String ruleName) {
        this.ruleSetReference = ruleSetReference;
        this.ruleName = ruleName;
        this.originalRef = ruleSetReference;
    }

    /**
     * Construct a RuleSetReferenceId for the given single reference string.
     *
     * @param reference
     *            The reference string.
     * @throws IllegalArgumentException
     *             If the ID contains a comma character.
     */
    public RuleSetReferenceId(final String reference) {
        this(reference, (RuleSetReferenceId) null);
    }

    /**
     * Construct a RuleSetReferenceId for the given single ID string. If an
     * absolute RuleSetReferenceId is given, the ID must refer to a simple
     * Rule. The rule will be resolved within the given absolute RuleSetReference.
     *
     * @param id                         The id string.
     * @param absoluteRuleSetReferenceId A RuleSetReferenceId to associate with this new instance.
     *
     * @throws IllegalArgumentException If the ID contains a comma character.
     * @throws IllegalArgumentException If absolute RuleSetReferenceId is not absolute.
     * @throws IllegalArgumentException If the ID is not Rule reference when there is an absoluteRuleSetReferenceId
     */
    public RuleSetReferenceId(final String id, final RuleSetReferenceId absoluteRuleSetReferenceId) {
        this.originalRef = StringUtils.trim(id);

        if (originalRef != null && originalRef.indexOf(',') >= 0) {
            throw new IllegalArgumentException(
                    "A single RuleSetReferenceId cannot contain ',' (comma) characters: " + id);
        }
        if (absoluteRuleSetReferenceId != null && !absoluteRuleSetReferenceId.isAbsolute()) {
            throw new IllegalArgumentException("Cannot pair with relative <" + absoluteRuleSetReferenceId + ">.");
        }
        if (isFullRuleSetName(originalRef) && absoluteRuleSetReferenceId != null) {
            throw new IllegalArgumentException(
                    "Cannot pair absolute <" + originalRef + "> with absolute <" + absoluteRuleSetReferenceId + ">.");
        }
        if (originalRef == null && absoluteRuleSetReferenceId == null) {
            throw new IllegalArgumentException("Either ID or absoluteRuleSetReferenceId is required");
        }

        // try to split originalRef into ruleset and rule
        String tempRuleName = getRuleName(originalRef);
        String tempRuleSetReference = tempRuleName == null
                ? null
                : originalRef.substring(0, originalRef.length() - tempRuleName.length() - 1);
        if (isFullRuleSetName(tempRuleSetReference) && absoluteRuleSetReferenceId != null) {
            throw new IllegalArgumentException(
                    "Cannot pair absolute <" + originalRef + "> with absolute <" + absoluteRuleSetReferenceId + ">.");
        }

        if (absoluteRuleSetReferenceId != null && absoluteRuleSetReferenceId.isAbsolute()) {
            // referencing a rule within another ruleset
            ruleSetReference = absoluteRuleSetReferenceId.getRuleSetFileName();
            ruleName = originalRef;
        } else if (isFullRuleSetName(originalRef)) {
            // A full RuleSet name - ends with .xml
            ruleSetReference = originalRef;
            ruleName = null;
        } else {
            if (isFullRuleSetName(tempRuleSetReference)) {
                // only interpret last part as rule name, if the remaining part is a full ruleset name (ending in .xml)
                ruleSetReference = tempRuleSetReference;
                ruleName = tempRuleName;
            } else if (originalRef.indexOf('/') == -1 && originalRef.indexOf('\\') == -1) {
                // it's a simple name, assume we only reference a rule name here
                ruleSetReference = null;
                ruleName = originalRef;
            } else {
                // in any other case, the name is more complex, likely a URI or something
                // assuming a complete ruleset reference
                ruleSetReference = originalRef;
                ruleName = null;
            }
        }
    }

    public @Nullable RuleSetReferenceId getParentRulesetIfThisIsARule() {
        if (ruleName == null) {
            return null;
        }
        return new RuleSetReferenceId(ruleSetReference, (String) null);
    }


    /**
     * Extracts the rule name out of a ruleset path. E.g. for
     * "/my/ruleset.xml/MyRule" it would return "MyRule". If no single rule is
     * specified (in other words: no separated last path name part), {@code null} is returned.
     *
     * @param rulesetName
     *            the full rule set path
     * @return the rule name or <code>null</code>.
     */
    private String getRuleName(final String rulesetName) {
        String result = null;
        if (rulesetName != null) {
            // Find last path separator if it exists... this might be a rule
            // name
            final int separatorIndex = Math.max(rulesetName.lastIndexOf('/'), rulesetName.lastIndexOf('\\'));
            if (separatorIndex >= 0 && separatorIndex != rulesetName.length() - 1) {
                result = rulesetName.substring(separatorIndex + 1);
            }
        }
        return result;
    }

    private static boolean isFullRuleSetName(String name) {
        return name != null && name.toLowerCase(Locale.ROOT).endsWith(".xml");
    }

    /**
     * Parse a String comma separated list of RuleSet reference IDs into a List
     * of RuleReferenceId instances.
     *
     * @param referenceString A comma separated list of RuleSet reference IDs.
     *
     * @return The corresponding List of RuleSetReferenceId instances.
     */
    // TODO deprecate and remove
    public static List<RuleSetReferenceId> parse(String referenceString) {
        List<RuleSetReferenceId> references = new ArrayList<>();
        if (referenceString != null && referenceString.trim().length() > 0) {

            if (referenceString.indexOf(',') == -1) {
                references.add(new RuleSetReferenceId(referenceString));
            } else {
                for (String name : referenceString.split(",")) {
                    references.add(new RuleSetReferenceId(name.trim()));
                }
            }
        }
        return references;
    }

    /**
     * Does this {@link RuleSetReferenceId} contain a reference to a ruleset. That means, it either
     * references all rules with the ruleset or a specific rule. But in any case, the ruleset is known.
     *
     * @return {@code true} if the ruleset is known, {@code false} when only the rule name is known.
     */
    public boolean isAbsolute() {
        return ruleSetReference != null;
    }

    /**
     * Is this a reference to all Rules in a RuleSet, or a single Rule?
     *
     * @return <code>true</code> if this is a reference to all Rules,
     *         <code>false</code> otherwise.
     */
    public boolean isAllRules() {
        return ruleName == null;
    }

    /**
     * Get the RuleSet file name.
     *
     * @return The RuleSet file name if this is an absolute reference,
     *         <code>null</code> otherwise.
     */
    public String getRuleSetFileName() {
        return ruleSetReference;
    }

    /**
     * Get the Rule name.
     *
     * @return The Rule name. The Rule name.
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * Try to load the RuleSet resource with the specified ResourceLoader. Multiple
     * attempts to get independent InputStream instances may be made, so
     * subclasses must ensure they support this behavior.
     *
     * @param rl The {@link ResourceLoader} to use.
     * @return An InputStream to that resource.
     */
    public InputStream getInputStream(final ResourceLoader rl) throws IOException {
        if (!isAbsolute()) {
            throw new IllegalArgumentException("Cannot resolve rule/ruleset reference '" + this + "' - reference is not absolute");
        }
        try {
            return rl.loadResourceAsStream(ruleSetReference);
        } catch (FileNotFoundException ignored) {
            throw notFoundException();
        }
    }

    private FileNotFoundException notFoundException() {
        return new FileNotFoundException("Cannot resolve rule/ruleset reference '" + originalRef
                + "'" + ".  Make sure the resource is a valid file or URL and is on the CLASSPATH. "
                + "Use --debug (or a fine log level) to see the current classpath.");
    }

    /**
     * Return a string representation of this Rule reference.
     *
     * <p>Warning: Do not rely on the format of this method, as it might be changed without prior notice.
     *
     * @return Return the String form of this Rule reference, which is
     *         <i>ruleSetFileName</i> for all Rule absolute references,
     *         <i>ruleSetFileName/ruleName</i>, for a single Rule absolute
     *         references, or <i>ruleName</i> otherwise.
     */
    public String toNormalizedReference() {
        if (isAbsolute()) {
            if (isAllRules()) {
                return ruleSetReference;
            } else {
                return ruleSetReference + '/' + ruleName;
            }
        } else {
            return ruleName;
        }
    }

    /**
     * String representation of this reference. Do not rely on the format of this method,
     * instead use {@link #toNormalizedReference()}.
     */
    @Override
    public String toString() {
        return toNormalizedReference();
    }
}
