/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.util.ResourceLoader;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * This class is used to parse a RuleSet reference value. Most commonly used for
 * specifying a RuleSet to process, or in a Rule 'ref' attribute value in the
 * RuleSet XML. The RuleSet reference can refer to either an external RuleSet or
 * the current RuleSet when used as a Rule 'ref' attribute value. An individual
 * Rule in the RuleSet can be indicated.
 *
 * For an external RuleSet, referring to the entire RuleSet, the format is
 * <i>ruleSetName</i>, where the RuleSet name is either a resource file path to
 * a RuleSet that ends with <code>'.xml'</code>, or a simple RuleSet name.
 *
 * A simple RuleSet name, is one which contains no path separators, and either
 * contains a '-' or is entirely numeric release number. A simple name of the
 * form <code>[language]-[name]</code> is short for the full RuleSet name
 * <code>rulesets/[language]/[name].xml</code>. A numeric release simple name of
 * the form <code>[release]</code> is short for the full PMD Release RuleSet
 * name <code>rulesets/releases/[release].xml</code>.
 *
 * For an external RuleSet, referring to a single Rule, the format is
 * <i>ruleSetName/ruleName</i>, where the RuleSet name is as described above. A
 * Rule with the <i>ruleName</i> should exist in this external RuleSet.
 *
 * For the current RuleSet, the format is <i>ruleName</i>, where the Rule name
 * is not RuleSet name (i.e. contains no path separators, '-' or '.xml' in it,
 * and is not all numeric). A Rule with the <i>ruleName</i> should exist in the
 * current RuleSet.
 *
 * <table>
 * <caption>Examples</caption> <thead>
 * <tr>
 * <th>String</th>
 * <th>RuleSet file name</th>
 * <th>Rule</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td>rulesets/java/basic.xml</td>
 * <td>rulesets/java/basic.xml</td>
 * <td>all</td>
 * </tr>
 * <tr>
 * <td>rulesets/java/basic.xml/EmptyCatchBlock</td>
 * <td>rulesets/java/basic.xml</td>
 * <td>EmptyCatchBlock</td>
 * </tr>
 * <tr>
 * <td>EmptyCatchBlock</td>
 * <td>null</td>
 * <td>EmptyCatchBlock</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @deprecated This is part of the internals of the {@link RuleSetLoader}.
 */
@Deprecated
@InternalApi
public class RuleSetReferenceId {

    // todo this class has issues... What is even an "external" ruleset?
    //  terminology and API should be clarified.

    private final boolean external;
    private final String ruleSetFileName;
    private final boolean allRules;
    private final String ruleName;
    private final RuleSetReferenceId externalRuleSetReferenceId;

    private final String originalRef;

    /**
     * Construct a RuleSetReferenceId for the given single ID string.
     *
     * @param id
     *            The id string.
     * @throws IllegalArgumentException
     *             If the ID contains a comma character.
     */
    public RuleSetReferenceId(final String id) {

        this(id, null, null);
    }

    private RuleSetReferenceId(final String ruleSetFileName, boolean external, String ruleName, RuleSetReferenceId externalRuleSetReferenceId) {
        this.ruleSetFileName = Objects.requireNonNull(ruleSetFileName);
        this.originalRef = ruleName == null ? ruleSetFileName : ruleSetFileName + "/" + ruleName;
        this.allRules = ruleName == null;
        this.external = external;
        this.ruleName = ruleName;
        this.externalRuleSetReferenceId = externalRuleSetReferenceId;
    }

    /**
     * Construct a RuleSetReferenceId for the given single ID string. If an
     * external RuleSetReferenceId is given, the ID must refer to a non-external
     * Rule. The external RuleSetReferenceId will be responsible for producing
     * the InputStream containing the Rule.
     *
     * @param id                         The id string.
     * @param externalRuleSetReferenceId A RuleSetReferenceId to associate with this new instance.
     *
     * @throws IllegalArgumentException If the ID contains a comma character.
     * @throws IllegalArgumentException If external RuleSetReferenceId is not external.
     * @throws IllegalArgumentException If the ID is not Rule reference when there is an external
     *                                  RuleSetReferenceId.
     */
    public RuleSetReferenceId(final String id, final RuleSetReferenceId externalRuleSetReferenceId) {
        this(id, externalRuleSetReferenceId, null);
    }

    /**
     * Construct a RuleSetReferenceId for the given single ID string. If an
     * external RuleSetReferenceId is given, the ID must refer to a non-external
     * Rule. The external RuleSetReferenceId will be responsible for producing
     * the InputStream containing the Rule.
     *
     * @param id                         The id string.
     * @param externalRuleSetReferenceId A RuleSetReferenceId to associate with this new instance.
     *
     * @throws IllegalArgumentException If the ID contains a comma character.
     * @throws IllegalArgumentException If external RuleSetReferenceId is not external.
     * @throws IllegalArgumentException If the ID is not Rule reference when there is an external
     *                                  RuleSetReferenceId.
     */
    RuleSetReferenceId(final String id,
                       final RuleSetReferenceId externalRuleSetReferenceId,
                       final @Nullable MessageReporter err) {
        this.originalRef = id;

        if (externalRuleSetReferenceId != null && !externalRuleSetReferenceId.isExternal()) {
            throw new IllegalArgumentException("Cannot pair with non-external <" + externalRuleSetReferenceId + ">.");
        }

        if (id != null && id.indexOf(',') >= 0) {
            throw new IllegalArgumentException(
                    "A single RuleSetReferenceId cannot contain ',' (comma) characters: " + id);
        }

        // Damn this parsing sucks, but my brain is just not working to let me
        // write a simpler scheme.

        if (isValidUrl(id)) {
            // A full RuleSet name
            external = true;
            ruleSetFileName = StringUtils.strip(id);
            allRules = true;
            ruleName = null;
        } else if (isFullRuleSetName(id)) {
            // A full RuleSet name
            external = true;
            ruleSetFileName = id;
            allRules = true;
            ruleName = null;
        } else {
            String tempRuleName = getRuleName(id);
            String tempRuleSetFileName = tempRuleName != null && id != null
                                         ? id.substring(0, id.length() - tempRuleName.length() - 1) : id;

            if (isValidUrl(tempRuleSetFileName)) {
                // remaining part is a xml ruleset file, so the tempRuleName is
                // probably a real rule name
                external = true;
                ruleSetFileName = StringUtils.strip(tempRuleSetFileName);
                ruleName = StringUtils.strip(tempRuleName);
                allRules = tempRuleName == null;
            } else if (isHttpUrl(id)) {
                // it's a url, we can't determine whether it's a full ruleset or
                // a single rule - so falling back to
                // a full RuleSet name
                external = true;
                ruleSetFileName = StringUtils.strip(id);
                allRules = true;
                ruleName = null;
            } else if (isFullRuleSetName(tempRuleSetFileName)) {
                // remaining part is a xml ruleset file, so the tempRuleName is
                // probably a real rule name
                external = true;
                ruleSetFileName = tempRuleSetFileName;
                ruleName = tempRuleName;
                allRules = tempRuleName == null;
            } else {
                // resolve the ruleset name - it's maybe a built in ruleset
                String expandedRuleset = resolveDeprecatedBuiltInRulesetShorthand(tempRuleSetFileName);
                String builtinRuleSet = expandedRuleset == null ? tempRuleSetFileName : expandedRuleset;
                if (checkRulesetExists(builtinRuleSet)) {
                    if (expandedRuleset != null && err != null) {
                        err.warn(
                            "Ruleset reference ''{0}'' uses a deprecated form, use ''{1}'' instead",
                            tempRuleSetFileName, builtinRuleSet
                        );
                    }

                    external = true;
                    ruleSetFileName = builtinRuleSet;
                    ruleName = tempRuleName;
                    allRules = tempRuleName == null;
                } else {
                    // well, we didn't find the ruleset, so it's probably not a
                    // internal ruleset.
                    // at this time, we don't know, whether the tempRuleName is
                    // a name of the rule
                    // or the file name of the ruleset file.
                    // It is assumed, that tempRuleName is actually the filename
                    // of the ruleset,
                    // if there are more separator characters in the remaining
                    // ruleset filename (tempRuleSetFileName).
                    // This means, the only reliable way to specify single rules
                    // within a custom rulesest file is
                    // only possible, if the ruleset file has a .xml file
                    // extension.
                    if (tempRuleSetFileName == null || tempRuleSetFileName.contains(File.separator)) {
                        external = true;
                        ruleSetFileName = id;
                        ruleName = null;
                        allRules = true;
                    } else {
                        external = externalRuleSetReferenceId != null && externalRuleSetReferenceId.isExternal();
                        ruleSetFileName = externalRuleSetReferenceId != null
                                ? externalRuleSetReferenceId.getRuleSetFileName() : null;
                        ruleName = id;
                        allRules = false;
                    }
                }
            }
        }

        if (this.external && this.ruleName != null && !this.ruleName.equals(id) && externalRuleSetReferenceId != null) {
            throw new IllegalArgumentException(
                    "Cannot pair external <" + this + "> with external <" + externalRuleSetReferenceId + ">.");
        }
        this.externalRuleSetReferenceId = externalRuleSetReferenceId;
    }

    @Nullable RuleSetReferenceId getParentRulesetIfThisIsARule() {
        if (ruleName == null) {
            return null;
        }
        return new RuleSetReferenceId(
            ruleSetFileName,
            external,
            null,

            null
        );
    }

    /**
     * Tries to load the given ruleset.
     *
     * @param name
     *            the ruleset name
     * @return <code>true</code> if the ruleset could be loaded,
     *         <code>false</code> otherwise.
     */
    private boolean checkRulesetExists(final String name) {
        boolean resourceFound = false;
        if (name != null) {
            try (InputStream ignored = new ResourceLoader().loadClassPathResourceAsStreamOrThrow(name)) {
                resourceFound = true;
            } catch (Exception ignored) {
                // ignored
            }
        }
        return resourceFound;
    }

    /**
     * Assumes that the ruleset name given is e.g. "java-basic". Then it will
     * return the full classpath name for the ruleset, in this example it would
     * return "rulesets/java/basic.xml".
     *
     * @param name
     *            the ruleset name
     * @return the full classpath to the ruleset
     */
    private String resolveDeprecatedBuiltInRulesetShorthand(final String name) {
        if (name == null) {
            return null;
        }
        // Likely a simple RuleSet name
        int index = name.indexOf('-');
        if (index > 0) {
            // Standard short name
            return "rulesets/" + name.substring(0, index) + '/' + name.substring(index + 1) + ".xml";
        }
        // A release RuleSet?
        if (name.matches("[0-9]+.*")) {
            return "rulesets/releases/" + name + ".xml";
        }
        // Appears to be a non-standard RuleSet name
        return null;
    }

    /**
     * Extracts the rule name out of a ruleset path. E.g. for
     * "/my/ruleset.xml/MyRule" it would return "MyRule". If no single rule is
     * specified, <code>null</code> is returned.
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

    private static boolean isHttpUrl(String name) {
        String stripped = StringUtils.strip(name);
        return stripped != null && (stripped.startsWith("http://") || stripped.startsWith("https://"));
    }

    private static boolean isValidUrl(String name) {
        if (isHttpUrl(name)) {
            String url = StringUtils.strip(name);
            try {
                // FIXME : Do we really need to perform a request? if it's a url we should treat it as one even if the server is down
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(ResourceLoader.TIMEOUT);
                connection.setReadTimeout(ResourceLoader.TIMEOUT);
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    return true;
                }
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    private static boolean isFullRuleSetName(String name) {

        return name != null && name.endsWith(".xml");
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
        return parse(referenceString, null);
    }

    static List<RuleSetReferenceId> parse(String referenceString,
                                          MessageReporter err) {
        List<RuleSetReferenceId> references = new ArrayList<>();
        if (referenceString != null && referenceString.trim().length() > 0) {

            if (referenceString.indexOf(',') == -1) {
                references.add(new RuleSetReferenceId(referenceString, null, err));
            } else {
                for (String name : referenceString.split(",")) {
                    references.add(new RuleSetReferenceId(name.trim(), null, err));
                }
            }
        }
        return references;
    }

    /**
     * Is this an external RuleSet reference?
     *
     * @return <code>true</code> if this is an external reference,
     *     <code>false</code> otherwise.
     */
    public boolean isExternal() {
        return external;
    }

    /**
     * Is this a reference to all Rules in a RuleSet, or a single Rule?
     *
     * @return <code>true</code> if this is a reference to all Rules,
     *         <code>false</code> otherwise.
     */
    public boolean isAllRules() {
        return allRules;
    }

    /**
     * Get the RuleSet file name.
     *
     * @return The RuleSet file name if this is an external reference,
     *         <code>null</code> otherwise.
     */
    public String getRuleSetFileName() {
        return ruleSetFileName;
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
     * subclasses must ensure they support this behavior. Delegates to an
     * external RuleSetReferenceId if there is one associated with this
     * instance.
     *
     * @param rl The {@link ResourceLoader} to use.
     * @return An InputStream to that resource.
     */
    public InputStream getInputStream(final ResourceLoader rl) throws IOException {
        if (externalRuleSetReferenceId == null) {
            if (StringUtils.isBlank(ruleSetFileName)) {
                throw notFoundException();
            }
            try {
                return rl.loadResourceAsStream(ruleSetFileName);
            } catch (FileNotFoundException ignored) {
                throw notFoundException();
            }
        } else {
            return externalRuleSetReferenceId.getInputStream(rl);
        }
    }

    private FileNotFoundException notFoundException() {
        return new FileNotFoundException("Cannot resolve rule/ruleset reference '" + originalRef
                + "'" + ".  Make sure the resource is a valid file or URL and is on the CLASSPATH. "
                + "Use --debug (or a fine log level) to see the current classpath.");
    }

    /**
     * Return the String form of this Rule reference.
     *
     * @return Return the String form of this Rule reference, which is
     *         <i>ruleSetFileName</i> for all Rule external references,
     *         <i>ruleSetFileName/ruleName</i>, for a single Rule external
     *         references, or <i>ruleName</i> otherwise.
     *
     * @deprecated Do not rely on the format of this method, it may be changed in PMD 7.
     */
    @Override
    @Deprecated
    public String toString() {
        if (ruleSetFileName != null) {
            if (allRules) {
                return ruleSetFileName;
            } else {
                return ruleSetFileName + '/' + ruleName;
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
