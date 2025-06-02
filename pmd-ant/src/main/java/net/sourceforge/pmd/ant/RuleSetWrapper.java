/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

/**
 * Part of PMD Ant task configuration. Setters of this class are interpreted by Ant as properties
 * settable in the XML. This is therefore published API.
 *
 * <p>This class is used to configure {@link net.sourceforge.pmd.lang.rule.RuleSet} as nested XML tags.
 * It might look like this:
 *
 * <pre>{@code
 * <pmd>
 *   <ruleset>rulesets/java/quickstart.xml</ruleset>
 * </pmd>
 * }</pre>
 *
 * @see PMDTask#addRuleset(RuleSetWrapper)
 */
public class RuleSetWrapper {
    private String file;

    public final String getFile() {
        return file;
    }

    public final void addText(String t) {
        this.file = t;
    }
}
